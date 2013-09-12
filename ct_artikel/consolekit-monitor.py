#!/usr/bin/python

# monitors adding and removing of seats and sessions via ConsoleKit

import dbus, os, gobject, pwd, sys
from dbus.mainloop.glib import DBusGMainLoop



# callback functions for D-Bus signals

def session_added(session):
# new session added (signal SessionAdded)
# connects to ConsoleKit's session interface
# and prints some information about the session 
	ses_proxy = bus.get_object('org.freedesktop.ConsoleKit', session)
	try:       # may fail when a guest session starts up
		local = ses_proxy.IsLocal()
		user = ses_proxy.GetUnixUser()
	except:
		user = -1
	print 'Session', session,
	if user > -1:
		if local == True:
			print '(lokal)'
			print 'X11 Display:', ses_proxy.GetX11Display(), 
			print '(' + ses_proxy.GetX11DisplayDevice() + ')'
		else:
			print '(remote)'
			print 'Display Device:', ses_proxy.GetDisplayDevice()
			print 'remote host name:', ses_proxy.GetRemoteHostName()
		user_pw = pwd.getpwuid(user)
		username = user_pw[0]
		print 'User:', username
	print


def session_removed(session):
# session removed
	print 'Session', session, 'removed\n'


def seat_added(seat):
# new seat added (signal SeatAdded)
# checks for sessions on the new seat
# and connects SessionAdded and SessionRemoved callbacks
	print 'Seat', seat, 'added\n'
	seat_proxy = bus.get_object('org.freedesktop.ConsoleKit', seat)
	for s in seat_proxy.GetSessions():  # sessions on the new seat?
		session_added(s)
	seat_proxy.connect_to_signal('SessionAdded', session_added)
	seat_proxy.connect_to_signal('SessionRemoved', session_removed)


def seat_removed(seat):
# seat removed
	print 'Seat', seat, 'removed\n'




# main

# root privilege needed to talk to ConsoleKit
if os.geteuid() != 0:
	print 'root privileges needed, sorry!'
	sys.exit()
# set up a DBus event loop
dbus.mainloop.glib.DBusGMainLoop(set_as_default=True)

# connect to the system bus
bus = dbus.SystemBus() 
# create a proxy object for ConsoleKit Manager interface
ck_proxy = bus.get_object('org.freedesktop.ConsoleKit',
	'/org/freedesktop/ConsoleKit/Manager')
# wire callback functions for signals SeatAdded and SeatRemoved
ck_proxy.connect_to_signal('SeatAdded', seat_added)
ck_proxy.connect_to_signal('SeatRemoved', seat_removed)

# list existing seats with their sessions
# wire callback functions for SessionAdded and SessionRemoved
seats = ck_proxy.GetSeats()
for seat in seats:
	print 'Existing seat:', seat
	seat_proxy = bus.get_object('org.freedesktop.ConsoleKit', seat)
	seat_proxy.connect_to_signal('SessionAdded', session_added)
	seat_proxy.connect_to_signal('SessionRemoved', session_removed)
	try:
		active = seat_proxy.GetActiveSession()
	except:
		active = ''
	print 'Sessions on', seat, ':'
	for s in seat_proxy.GetSessions(): # get sessions on this seat
		print s,
		if s == active:
			print '(active)'
		else:
			print
	print

# print information abount existing sessions
sessions = ck_proxy.GetSessions()
for session in sessions:
	session_added(session)

# enter loop
loop = gobject.MainLoop()
loop.run()

