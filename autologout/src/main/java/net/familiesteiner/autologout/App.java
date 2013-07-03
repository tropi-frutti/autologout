package net.familiesteiner.autologout;

import java.util.List;
import java.util.logging.Level;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.ConsoleKit.Seat;
import org.freedesktop.ConsoleKit.Session;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App implements Daemon, DBusSigHandler<Seat.SessionAdded>
{
    public static void main( String[] args ) throws DBusException
    {
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.info( "Hello World!" );
        TimerService service = new TimerService();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        service.destroy();
        logger.info("Goodbye");
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

    public void init(DaemonContext context) throws DaemonInitException, Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void start() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void stop() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
