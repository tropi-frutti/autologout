/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public String getSessionAddress(User user) {
        String address = null;
        Path dir = Paths.get("/proc");
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            for (Path path : stream) {
               Path exepath = path.resolve("exe");
               if (Files.exists(exepath)) {
                    int uid = (Integer)Files.getAttribute(exepath, "unix:uid");                   
                    if (uid == user.getUid()) {
                        Path target = Files.readSymbolicLink(exepath);
                        if (target.endsWith("gnome-session")) {
                           Path environpath = path.resolve("environ");
                            // load file environ
                           BufferedReader envReader = Files.newBufferedReader(environpath, Charset.forName("US_ASCII"));
                           String line = null;
                           while((line = envReader.readLine()) != null) {
                               // search for DBUS_SESSION_BUS_ADDRESS
                               String[] parts = line.split("\0"); // \u0000 should also work
                               for (int i = 0; i < parts.length; i++) {
                                   String env = parts[i];
                                   final String DBUS_SESSION_BUS_ADDRESSNAME="DBUS_SESSION_BUS_ADDRESS=";
                                   if(env.startsWith(DBUS_SESSION_BUS_ADDRESSNAME)) {
                                       address = env.substring(DBUS_SESSION_BUS_ADDRESSNAME.length());
                                   }
                               }
                           }
                        }
                    }
               }
            }
        } catch (IOException ex) {
            Logger.getLogger(DBusAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }

    public void warnUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
