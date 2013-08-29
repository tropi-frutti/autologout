/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.Set;
import net.familiesteiner.autologout.domain.User;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author bertel
 */
public interface DBusAdapterInterface {
        public Set<User> identifyActiveSessions();
        public String getSessionAddress(User user);
        public void requestLogout(User user) throws DBusException;
        public void forceLogout(User user) throws DBusException;
}
