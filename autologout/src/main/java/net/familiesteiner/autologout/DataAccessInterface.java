/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.Set;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.UserConfiguration;

/**
 *
 * @author bertel
 */
public interface DataAccessInterface {
    public void save(SessionSummary sessionSummary);
    public Set<SessionSummary> loadAllSessionSummaries();
    public Set<UserConfiguration> loadAllUserConfigurations();
}
