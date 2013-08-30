/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.User;
import net.familiesteiner.autologout.domain.UserConfiguration;
import org.freedesktop.dbus.exceptions.DBusException;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bertel
 */
public class SessionProcessor implements SessionProcessorInterface {
    private static Logger LOG = LoggerFactory.getLogger(TimerService.class);
    DBusAdapterInterface dbusAdapter = null;
    DataAccessInterface dataAccess = null;
    Map<User,SessionSummary> sessionSummaries = new HashMap<User,SessionSummary>();
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
    public void countCurrentActiveSessions() {
        XStream xstream = new XStream();
        Date now = new Date();

        Set<User> users = this.dbusAdapter.identifyActiveSessions();
        for (User user : users) {
            LOG.info("active User: " + user.getUid());
            LOG.info("active User xml: " + xstream.toXML(user));
            SessionSummary sessionSummary = this.sessionSummaries.get(user);
            if (sessionSummary == null) {
                sessionSummary = new SessionSummary(user);
                this.sessionSummaries.put(user, sessionSummary);
            }
            sessionSummary.addActiveTime(now);

            LOG.info("active session xml: " + xstream.toXML(sessionSummary));
            LOG.info("active time: " + sessionSummary.countActiveMinutes());
       }
    }

    @Override
    public void loadSessions() {
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
    }

    @Override
    public void saveSessions() {
        for (Map.Entry<User, SessionSummary> entry : sessionSummaries.entrySet()) {
            SessionSummary sessionSummary = entry.getValue();
            if (sessionSummary.isDirty()) {
                this.dataAccess.save(sessionSummary);
            }
        }
    }

    @Override
    public void reenableClosedSessions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handleExceededSessions() {
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
            }
            
            if (true == logoutActionIdentified) {
                try {
                    // check if warn or logout
                    if (sessionSummary.isAlreadyWarnedToday()) {
                        if (sessionSummary.isWarningDelayTimedOut(userConfiguration.getWarningDelay())) {
                            this.dbusAdapter.forceLogout(user);                            
                        }
                    }
                    else {
                        sessionSummary.setWarnTime(new Date());
                        this.dbusAdapter.requestLogout(user);
                    }
                } catch (DBusException ex) {
                    java.util.logging.Logger.getLogger(SessionProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
