/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.Set;
import net.familiesteiner.autologout.domain.User;
import net.familiesteiner.autologout.exception.LogoutImpossibleException;

/**
 *
 * @author bertel
 */
public interface DBusAdapterInterface {
        public Set<User> identifyActiveSessions();
        public void requestLogout(User user) throws LogoutImpossibleException;
        public void forceLogout(User user) throws LogoutImpossibleException;
        public void lock(User user);
        public void unlock(User user);
}
