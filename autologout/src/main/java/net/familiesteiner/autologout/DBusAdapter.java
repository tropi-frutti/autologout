/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
import net.familiesteiner.autologout.exception.LogoutImpossibleException;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.ConsoleKit.Seat;
import org.freedesktop.ConsoleKit.Session;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.gnome.SessionManager;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author bertel
 */
public class DBusAdapter implements DBusAdapterInterface, DBusSigHandler<Seat.SessionAdded> {
    DBusConnection bus;
    Manager consoleKitManager;
    private static XLogger LOG = XLoggerFactory.getXLogger(DBusAdapter.class);

    public Manager getConsoleKitManager() {
        return consoleKitManager;
    }
    
    public void init() throws DBusException {
        LOG.entry();
        DBusConnection bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
    	Manager ck_proxy = (Manager) bus.getRemoteObject("org.freedesktop.ConsoleKit", "/org/freedesktop/ConsoleKit/Manager");
        LOG.exit();
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
        LOG.entry();
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
        LOG.exit(result);
        return result;
    }

    public void handle(Seat.SessionAdded t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getSessionAddress(User user) {
        LOG.entry(user);
        String address = null;
        Path dir = Paths.get("/proc");
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            directorySearch:
            for (Path path : stream) {
               Path exepath = path.resolve("exe");
               if (Files.exists(exepath)) {
                    int uid = (Integer)Files.getAttribute(path, "unix:uid");                   
                    if (uid == user.getUid()) {
                        Path target = Files.readSymbolicLink(exepath);
                        if (target.endsWith("gnome-session")) {
                           LOG.debug("found gnome session: " + exepath);
                           Path environpath = path.resolve("environ");
                            // load file environ
                           BufferedReader envReader = Files.newBufferedReader(environpath, Charset.defaultCharset());
                           String line = null;
                           while((line = envReader.readLine()) != null) {
                               // search for DBUS_SESSION_BUS_ADDRESS
                               String[] parts = line.split("\0"); // \u0000 should also work
                               for (int i = 0; i < parts.length; i++) {
                                   String env = parts[i];
                                   final String DBUS_SESSION_BUS_ADDRESSNAME="DBUS_SESSION_BUS_ADDRESS=";
                                   if(env.startsWith(DBUS_SESSION_BUS_ADDRESSNAME)) {
                                       address = env.substring(DBUS_SESSION_BUS_ADDRESSNAME.length());
                                       break directorySearch;
                                   }
                               }
                           }
                        }
                    }
               }
            }
        } catch (IOException ex) {
            LOG.catching(ex);
        }
        if (address == null) {
            throw new RuntimeException("no session address found for user " + user);
        }
        
        LOG.exit(address);
        return address;
    }

    public void requestLogout(User user) throws LogoutImpossibleException {
        LOG.entry(user);
        logoutWithScript(user, false);
        LOG.exit();
    }

    public void forceLogout(User user) throws LogoutImpossibleException {
        LOG.entry(user);
        logoutWithScript(user, true);
        LOG.exit();
    }
    
    private void logoutWithScript(User user, boolean force) {
        LOG.entry();
        StringBuilder command = new StringBuilder("/usr/share/autologout/request_logout.py");
        if (force) {
            command.append(" --force");
        }
        
        command.append(" ");
        command.append(user.getUid());
        
        try {
            String line;
            Process process = Runtime.getRuntime().exec(command.toString());
            BufferedReader bri = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while((line = bri.readLine()) != null ) {
                System.out.println(line);
            }
            bri.close();
            while((line = bre.readLine()) != null ) {
                System.err.println(line);
            }
            bre.close();
            
            int returnCode = process.waitFor();
        } catch (IOException ex) {
            LOG.catching(ex);
        } catch (InterruptedException ex) {
            LOG.catching(ex);
        }
        LOG.exit();
    }
    
}