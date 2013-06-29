package net.familiesteiner.autologout;

import java.util.List;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.ConsoleKit.Seat;
import org.freedesktop.ConsoleKit.Session;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 * Hello world!
 *
 */
public class App implements DBusSigHandler<Seat.SessionAdded>
{
    public static void main( String[] args ) throws DBusException
    {
        System.out.println( "Hello World!" );
        App myApp = new App();
        myApp.doIt();
    }
    
    public void doIt() throws DBusException {
    	DBusConnection bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
    	Manager ck_proxy = (Manager) bus.getRemoteObject("org.freedesktop.ConsoleKit", "/org/freedesktop/ConsoleKit/Manager");
        List<DBusInterface> list = ck_proxy.GetSeats();
    	for (DBusInterface object : list) {
            Seat seat = (Seat) object;
            List<DBusInterface> sessions = seat.GetSessions();
            for (DBusInterface sessionObject : sessions) {
                Session session = (Session) sessionObject;
                UInt32 user = session.GetUnixUser();
                long uid = user.longValue();
                user = session.GetUser();
                uid = user.longValue();
                session.GetX11Display();
            }
            bus.addSigHandler(Seat.SessionAdded.class, seat, this);
        }        
    }

    public void handle(Seat.SessionAdded t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
