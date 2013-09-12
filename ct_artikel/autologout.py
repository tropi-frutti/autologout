#!/usr/bin/python

# auto-logout: enforces time limits for computer usage.
# Allowed login times and a maximal usage time for each user can be
# configured (see CONFIGURATION).
# Uses ConsoleKit D-Bus interface to track the X11 session of each user;
# remote or text console logins are ignored and should be disabled.
# On a PC, users can have exactly one X11 session.
# Uses SessionManager D-Bus interface to logout users.
# Uses UPower D-Bus interface to track suspend/resume.   

import dbus, os, pwd, gobject, time
from dbus.mainloop.glib import DBusGMainLoop
from datetime import datetime, timedelta
import pickle, signal

# global variables and constants
users = {}      # user limits and login times, indexed by UID
owners = {}     # UID of owners of tracked sessions, indexed by session ID
iface = ''      # needed to catch "lid closed" events
store_path = '/var/lib/autologout'   # store for user objects


# helper functions

def get_uname(uid):
# returns user name for UID
	try:
		user_pw = pwd.getpwuid(uid)
		return(user_pw[0])
	except:
		return 'guest_session'

def get_uid(uname):
# returns UID for user name
	user_pw = pwd.getpwnam(uname)
	return(user_pw[2])

def seconds(s):
# converts a string 'hh:mm[:ss]' to seconds since midnight
	if ':' in s:
		try:
			h, m, sec = s.split(':', 2)
			return(int(h)*60*60 + int(m)*60 + int(sec))
		except:
			h, m = s.split(':', 1)
			return(int(h)*60*60 + int(m)*60)
	else:
		return(0)

def time_str(seconds):
# converts seconds to string 'hh:mm:ss'
	h = seconds / 3600
	m = (seconds - h*3600) / 60
	s = seconds - h*3600 - m*60
	return str(h) + ':' + str(m) + ':' + str(s) 
	 
def now():
# returns the time in seconds since midnight
	dt = datetime.now()
	tm = dt.timetuple()
	return tm.tm_sec + 60*tm.tm_min + 3600*tm.tm_hour

def get_session_address(uid):
# returns DBUS address of user UID's session or ''
	address = ''
	# search in /proc for the gnome-session process of user UID, 
	for path in os.listdir('/proc/'):
		fname = '/proc/' + path + '/exe'
		if os.path.exists(fname):
			stat = os.lstat(fname)
			if stat.st_uid == uid:
				if 'gnome-session' in os.readlink(fname):
				# find session bus address in environment
					f = open('/proc/' + path + '/environ', 'rb')
					environ = f.read()
					env = environ.split('\0')
					for e in env:
						if '=' in e:
							var, val = e.split('=', 1)
						if 'DBUS_SESSION_BUS_ADDRESS' == var:
							address = val
					f.close()
	return address

def terminate(uid):
# terminates gnome session for user with given uid
	time.sleep(10)               # wait for session to start up
	address = get_session_address(uid)
	# connect to users's session bus; call session manager's Logout function
	if address != '':
		session_bus = dbus.bus.BusConnection(address)
		try:
			session_manager = session_bus.get_object('org.gnome.SessionManager',
			'/org/gnome/SessionManager')
			i = dbus.UInt32(0)
			session_manager.Logout(i)     # logout with cancel option
		except:
			return False
		time.sleep(300)               # wait 5 mintes
		try:                          # user might have logged out meanwhile
			users[uid].deactivate()   # hard kill: no deactivate signal is sent
			i = dbus.UInt32(2)
			session_manager.Logout(i)     # logout immediately
		except:
			return False
	return False   # needed to stop timer

def write_user(uid):
# writes user object for UID to disk 
	f = open(store_path + '/' + str(uid), 'w')
	pickle.dump(users[uid], f)
	f.close()

def read_user(uid):
# writes user object for UID to disk 
	try:
		f = open(store_path + '/' + str(uid), 'r')
		users[uid] = pickle.load(f)
		f.close()
	except:
		pass



class User:
# manages the limits and session status of one user with defined limits
	def __init__(self, name='', uid=0, start='0:00', end='24:00', limit=''):
		self.start = seconds(start)   # start of allowed use interval
		self.end = seconds(end)       # end of allowed use interval
		self.limit = seconds(limit)   # length of allowed use interval
		if name != '':           	  # user name and UID
			self.uname = name         # if both are given, name overrides UID
			self.uid = get_uid(name)
		else:
			self.uid = uid
			self.uname = get_uname(uid)
		self.active_since = -1        # time session was activated
		self.cumulated = 0            # cumulates seconds over active periods
		self.last_reset = datetime.now() # last reset of cumulated useage time
		gobject.timeout_add_seconds(24*60*60, self.reset)


	def reset(self):
	# useage data are resetted each 24 hours
		self.cumulated = 0
		self.last_reset = datetime.now()
		write_user(self.uid)


	def activate(self):
	# user's X11 session is started, detected at program startup,
	# activated after a fast user switch or after a resume from susped/hibernate
		self.active_since = now()
		write_user(self.uid)
		delta = datetime.now() - self.last_reset
		if delta.days >= 1:    # at least 24 hours have passed since last reset
			self.reset()
		if self.active_since > self.end:       # too late
			terminate(self.uid)
		elif self.active_since < self.start:     # too early
			terminate(self.uid)
		elif self.limit > 0:                     # user has a limit
			if self.cumulated >= self.limit:   # limit exceeded
				terminate(self.uid)
			else:                              # trigger terminate
				gobject.timeout_add_seconds(self.limit - self.cumulated,
				self.check_terminate)


	def deactivate(self):
	# user session has been deactivated or ended, or KeyboardInterrupt occured
		if self.active_since > 0:               # session is active
			if now() > self.active_since:
				self.cumulated += now() - self.active_since
			else:                         # midnight has passed
				self.cumulated += (24*60*60 - self.active_since) + now()
			self.active_since = -1
		write_user(self.uid)


	def active_changed(self, is_active):
	# callback for ActiveChanged signal
		if is_active:
			self.activate()
		else:
			self.deactivate()


	def check_terminate(self):
	# triggered when time limit might be reached
		if self.active_since > -1:    # session is active
			if now() > self.active_since:
				all_cumulated = self.cumulated + (now() - self.active_since)
			else:         # midnight has passed
				all_cumulated = self.cumulated + (24*60*60 - self.active_since) + now()
			if all_cumulated >= self.limit - 10:  # close to limit:
				terminate(self.uid)               # terminate now
			else:                                 # trigger terminate
				gobject.timeout_add
				_seconds(self.limit - all_cumulated,
				self.check_terminate)
		else:                         # session is inactive:
			pass                      # will be checked on next activation
		return False

# end class User



# callback functions for ConsoleKit signals

def session_added(session):
# new session added; 
# non-X11 sessions (remote or text console logins) are ignored!
	ses_proxy = bus.get_object('org.freedesktop.ConsoleKit', session)
	try:                # GetUnixUser may fail when a guest session starts up
		uid = ses_proxy.GetUnixUser()
		x11display = ses_proxy.GetX11Display()
	except:
		return
	if x11display != '':                # X11 session
		if uid in users.keys():         # User object for session owner exists
			uname = get_uname(uid)
			owners[session] = uid
			if ses_proxy.IsActive():    # active session
				users[uid].activate()
			try:
				ses_proxy.connect_to_signal('ActiveChanged',
				users[uid].active_changed)
			except KeyError:     # always raised: signal parameter UInt32
				pass             # doesn't match args of User.active_changed()


def session_removed(session):
# session removed
	if session in owners.keys():     # tracked session
		del owners[session]          # remove session from owners


def seat_added(seat):
# new seat added
# checks for sessions, connects signals SessionAdded and SessionRemoved
	seat_proxy = bus.get_object('org.freedesktop.ConsoleKit', seat)
	for ses in seat_proxy.GetSessions():  # sessions on the new seat?
		session_added(ses)
	seat_proxy.connect_to_signal('SessionAdded', session_added)
	seat_proxy.connect_to_signal('SessionRemoved', session_removed)


def sleeping():
# system is going to suspend or hibernate
	for session in owners.keys():      # check tracked sessions
		ses_proxy = bus.get_object('org.freedesktop.ConsoleKit', session)
		if ses_proxy.IsActive():    # active session
				users[owners[session]].deactivate()

def resuming():
# system is resuming from suspend or hibernate
	for session in owners.keys():      # check tracked sessions
		ses_proxy = bus.get_object('org.freedesktop.ConsoleKit', session)
		if ses_proxy.IsActive():    # active session
				users[owners[session]].activate()

def changed():
# UPower status has changed. We are only interested in lid closing events
	if iface.Get('', 'LidIsClosed'):
		sleeping()



# main

# CONFIGURATION
# user limits, indexed by UID; adapt to your own needs. User objects are created.
# foo: two and a half hours between 14:00 and 20:00
users[get_uid('foo')] = User(name='foo', start='14:00', end='20:00', limit='2:30')
users[get_uid('bar')] = User(name='bar')  # no limits
users[115] = User(uid=115, limit='0:01')  # guest session, 1 minute only
users[116] = User(uid=116, limit='0:01')  # guest session
users[117] = User(uid=117, limit='0:01')  # guest session
users[118] = User(uid=118, limit='0:01')  # guest session

# get dumped data from disk, if available
# overwrites limits; so remove status files, if configuration is changed
for u in users.keys():
	read_user(u)

# root privilege needed to talk to ConsoleKit
if os.geteuid() != 0:
	print 'root privileges needed, sorry!'

else:
# create directory for storage of user objects
	if not os.path.exists(store_path):
		os.mkdir(store_path)
# set up a DBus event loop
	dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)
# connect to the system bus
	bus = dbus.SystemBus() 
# create a proxy object for ConsoleKit Manager interface
	ck_proxy = bus.get_object('org.freedesktop.ConsoleKit',
	'/org/freedesktop/ConsoleKit/Manager')
# callback functions for signal SeatAdded
	ck_proxy.connect_to_signal('SeatAdded', seat_added)
# scan existing seats
	seats = ck_proxy.GetSeats()
	for seat in seats:
		seat_added(seat)

# connect to UPower and catch signal Sleeping if version >= 0.9.4,
# else catch Changed(LidIsClosed) event (UPower bug)
	upower_proxy = bus.get_object('org.freedesktop.UPower',
	'/org/freedesktop/UPower')
	iface = dbus.Interface(upower_proxy, 'org.freedesktop.DBus.Properties')
	version = iface.Get('', 'DaemonVersion')
	maj, min, sub = version.split('.')
	if int(min) == 9 and int(sub) < 4:   # UPower < 0.9.4
		upower_proxy.connect_to_signal('Changed', changed)
	else:
		upower_proxy.connect_to_signal('Sleeping', sleeping)
	upower_proxy.connect_to_signal('Resuming', resuming)

# enter event loop, catch KeyboardInterrupt
	try:
		loop = gobject.MainLoop()
		loop.run()
	except KeyboardInterrupt:
		for u in users.keys():
			users[u].deactivate()   # update user objects

