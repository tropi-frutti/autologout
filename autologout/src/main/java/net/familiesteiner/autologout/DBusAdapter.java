/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.familiesteiner.autologout.domain.User;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.ConsoleKit.Seat;
import org.freedesktop.ConsoleKit.Session;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author bertel
 */
public class DBusAdapter implements DBusAdapterInterface, DBusSigHandler<Seat.SessionAdded> {
    DBusConnection bus;
    Manager consoleKitManager;

    public Manager getConsoleKitManager() {
        return consoleKitManager;
    }
    
    public void init() throws DBusException {
        DBusConnection bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
    	Manager ck_proxy = (Manager) bus.getRemoteObject("org.freedesktop.ConsoleKit", "/org/freedesktop/ConsoleKit/Manager");
    }
    
    @Inject
    public DBusAdapter(Manager consoleKitManager) {
        this.consoleKitManager = consoleKitManager;
    }
    
    public void registerSignalHandler(Seat seat) throws DBusException {
            bus.addSigHandler(Seat.SessionAdded.class, seat, this);
    }
    
    @Override
    public Set<User> identifyActiveSessions() {
        Set<User> result = new HashSet<User>();
        List<DBusInterface> list = consoleKitManager.GetSeats();
    	for (DBusInterface object : list) {
            Seat seat = (Seat) object;
            List<DBusInterface> sessions = seat.GetSessions();
            for (DBusInterface sessionObject : sessions) {
                Session session = (Session) sessionObject;
                if (session.IsActive()) {
                    UInt32 user = session.GetUnixUser();
                    result.add(new User(user));
                }
            }
        }      
        return result;
    }

    public void handle(Seat.SessionAdded t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
