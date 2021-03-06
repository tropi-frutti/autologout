#!/usr/bin/python

# auto-logout: enforces time limits for computer usage.
# Allowed login times and a maximal usage time for each user can be
# configured (see CONFIGURATION).
# Uses ConsoleKit D-Bus interface to track the X11 session of each user;
# remote or text console logins are ignored and should be disabled.
# On a PC, users can have exactly one X11 session.
# Uses SessionManager D-Bus interface to logout users.
# Uses UPower D-Bus interface to track suspend/resume.   

import dbus, os, pwd, gobject, time, sys
from dbus.mainloop.glib import DBusGMainLoop
from datetime import datetime, timedelta
import pickle, signal, argparse


# helper functions

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

def terminate(uid, force):
# terminates gnome session for user with given uid
    address = get_session_address(uid)
    # connect to users's session bus; call session manager's Logout function
    if address != '':
        try:
            session_bus = dbus.bus.BusConnection(address) 
            print 2
            session_manager = session_bus.get_object('org.gnome.SessionManager',
            '/org/gnome/SessionManager')
            print 3
            if force == False:
                i = dbus.UInt32(0)
                session_manager.Logout(i)     # logout with cancel option
                return True
            else:
                i = dbus.UInt32(2)
                session_manager.Logout(i)     # logout immediately
                return True
        except:
            print "Unexpected error: ", sys.exc_info()[0]
            return False            
    return False  

# main

parser = argparse.ArgumentParser(description='Log out a given user.')
parser.add_argument('uid', type=int, nargs=1,
                   help='unix user id')
parser.add_argument('--force', action='store_true',
                   help='forces log out')
                
args = parser.parse_args()

uid = int(args.uid[0])

print terminate(uid, args.force)