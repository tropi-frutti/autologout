/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.Set;
import net.familiesteiner.autologout.domain.User;

/**
 *
 * @author bertel
 */
public interface DBusAdapterInterface {
        public Set<User> identifyActiveSessions();
        public String getSessionAddress(User user);
}
