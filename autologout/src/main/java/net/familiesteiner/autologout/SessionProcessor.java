/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.User;
import net.familiesteiner.autologout.domain.UserConfiguration;
import org.freedesktop.dbus.exceptions.DBusException;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author bertel
 */
public class SessionProcessor implements SessionProcessorInterface {
    private static XLogger LOG = XLoggerFactory.getXLogger(SessionProcessor.class);
    DBusAdapterInterface dbusAdapter = null;
    DataAccessInterface dataAccess = null;
    Map<User,SessionSummary> sessionSummaries = new HashMap<User,SessionSummary>();

    /** helper to inject data for testing */
    void setSessionSummaries(Map<User, SessionSummary> sessionSummaries) {
        this.sessionSummaries = sessionSummaries;
    }

    /** helper to inject data for testing */
    public void setUserConfigurations(Map<User, UserConfiguration> userConfigurations) {
        this.userConfigurations = userConfigurations;
    }
    Map<User, UserConfiguration> userConfigurations = new HashMap<User,UserConfiguration>();

    public DBusAdapterInterface getDbusAdapter() {
        return dbusAdapter;
    }
    
    @Inject
    public SessionProcessor(DBusAdapterInterface dbusAdapter, DataAccessInterface dataAccess) {
        this.dbusAdapter = dbusAdapter;
        this.dataAccess = dataAccess;
    }

    /**
     *
     */
    @Override
    public void calculateActiveTimes() {
        LOG.entry();
        DateTime now = DateFactory.getInstance().now();
        

        Set<User> users = this.dbusAdapter.identifyActiveSessions();
        for (User user : users) {
            LOG.info("active User: " + user.getUid());
            SessionSummary sessionSummary = this.sessionSummaries.get(user);
            if (sessionSummary == null) {
                sessionSummary = new SessionSummary(user);
                this.sessionSummaries.put(user, sessionSummary);
            }
            sessionSummary.addActiveTime(now);
            
            // delete online times from last week
            sessionSummary.clearOutdatedActiveTimes(DateFactory.getInstance().getStartOfWeek());

            LOG.info("active time: " + sessionSummary.countActiveMinutes());
       }
        LOG.exit();
    }

    @Override
    public void loadSessions() {
        LOG.entry();
        this.sessionSummaries.clear();
        Set<SessionSummary> storedSessionSummaries = this.dataAccess.loadAllSessionSummaries();
        for (SessionSummary sessionSummary : storedSessionSummaries) {
            this.sessionSummaries.put(sessionSummary.getUser(), sessionSummary);
            sessionSummary.setDirty(false);
        }
        
        this.userConfigurations.clear();
        Set<UserConfiguration> loadedUserConfigurations = this.dataAccess.loadAllUserConfigurations();
        for (UserConfiguration userConfiguration : loadedUserConfigurations) {
            this.userConfigurations.put(userConfiguration.getUser(), userConfiguration);
        }
        LOG.exit();
    }

    @Override
    public void saveSessions() {
        LOG.entry();
        for (Map.Entry<User, SessionSummary> entry : sessionSummaries.entrySet()) {
            SessionSummary sessionSummary = entry.getValue();
            if (sessionSummary.isDirty()) {
                this.dataAccess.save(sessionSummary);
            }
        }
        LOG.exit();
    }

    @Override
    public void reenableClosedSessions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handleExceededSessions() {
        LOG.entry();
        for (Map.Entry<User, SessionSummary> entry : sessionSummaries.entrySet()) {
            User user = entry.getKey();
            SessionSummary sessionSummary = entry.getValue();
            UserConfiguration userConfiguration = this.userConfigurations.get(user);
            boolean logoutActionIdentified = false;
            if (null != userConfiguration) {
                
                // 1. check if allowed now
                Interval allowedInterval = userConfiguration.getAllowedInterval();
                if (allowedInterval.containsNow() == false) {
                    logoutActionIdentified = true;
                }
                
                // 2. check if time sum has exceeded
                long onlineLimit = userConfiguration.getOnlineLimit();
                long activeMinutes = sessionSummary.countActiveMinutes();
                if (activeMinutes > onlineLimit) {
                    logoutActionIdentified = true;
                }
                
                if (true == logoutActionIdentified) {
                    try {
                        // check if warn or logout
                        if (sessionSummary.isAlreadyWarnedToday()) {
                            long warningDelay = userConfiguration.getWarningDelay();
                            if (!sessionSummary.isWarningDelayTimedOut(warningDelay)) {
                                LOG.debug("user already warned but still in warning period: " + user);                    
                                // wait until force time occurs
                            } else {                            
                                LOG.info("forcing user to log out: " + user);                    
                                this.dbusAdapter.forceLogout(user);
                            }
                        }
                        else {
                            LOG.info("warning user to log out: " + user);                    
                            sessionSummary.markAsWarned();
                            this.dbusAdapter.requestLogout(user);
                        }
                    } catch (DBusException ex) {
                        LOG.catching(ex);
                    }
                }
                else {
                    LOG.debug("no action required for user: " + user);                    
                }
            }
            else {
                // session summary without user configuration
                LOG.debug("no user configuration for user with session summary: " + user);
            }
        }
        LOG.exit();
    }
}
