/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author bertel
 */
public class AutologoutModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SessionProcessorInterface.class).to(SessionProcessor.class);
        bind(DBusAdapterInterface.class).to(DBusAdapter.class);
    }
    
    @Provides
    DataAccessInterface provideDataAccessInterface() {
        DataAccess dataAccess = new DataAccess();
        String rootDirectory = System.getProperty("net.familiesteiner.autologout.rootdirectory", "/var/lib/autologout");
        dataAccess.setRootDirectory(rootDirectory);
        return dataAccess;
    }
    
    @Provides
    Manager provideManager() {
        try {
            DBusConnection bus = DBusConnection.getConnection(DBusConnection.SYSTEM);
                Manager ck_proxy = (Manager) bus.getRemoteObject("org.freedesktop.ConsoleKit", "/org/freedesktop/ConsoleKit/Manager");
            return ck_proxy;
        } catch (DBusException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
