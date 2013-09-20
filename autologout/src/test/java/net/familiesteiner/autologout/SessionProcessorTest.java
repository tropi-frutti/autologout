/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.User;
import net.familiesteiner.autologout.domain.UserConfiguration;
import net.familiesteiner.autologout.exception.LogoutImpossibleException;
import org.freedesktop.dbus.exceptions.DBusException;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author steinorb
 */
public class SessionProcessorTest {
    DBusAdapterInterface dbusAdapter = null;
    DataAccessInterface dataAccess = null;
    
    @BeforeClass
    public static void setUpClass() {
        DateFactory.getInstance().setTestMode(true);
    }
    
    @Before
    public void setUp() {
        dbusAdapter = mock(DBusAdapterInterface.class);
        dataAccess = mock(DataAccessInterface.class);
    }
    
    /**
     * Test of calculateActiveTimes method, of class SessionProcessor.
     */
    @Test
    public void testCalculateActiveTimes() {
        Set<User> users = new HashSet<User>();
        users.add(new User(123));
        when(dbusAdapter.identifyActiveSessions()).thenReturn(users);
        SessionProcessor instance = new SessionProcessor(dbusAdapter, dataAccess);
        instance.calculateActiveTimes();
    }
    
    @Test
    public void testHandleExceededSessions_noUser() {
        SessionProcessor instance = new SessionProcessor(dbusAdapter, dataAccess);
        instance.handleExceededSessions();
    }

    @Test
    public void testHandleExceededSessions_nonExpiredUser() throws DBusException, LogoutImpossibleException {
        SessionProcessor instance = new SessionProcessor(dbusAdapter, dataAccess);
        User user = new User(123);
        UserConfiguration userConfiguration = new UserConfiguration(user);
        userConfiguration.setAllowedFromHour(0);
        userConfiguration.setAllowedFromMinute(0);
        userConfiguration.setAllowedUntilHour(23);
        userConfiguration.setAllowedUntilMinute(59);
        userConfiguration.setWarningDelay(5);
        userConfiguration.setOnlineLimit(2);
        
        SessionSummary sessionSummary = new SessionSummary(user);
        sessionSummary.addActiveTime(new DateTime(2013, 1, 1, 12, 10));
        sessionSummary.addActiveTime(new DateTime(2013, 1, 1, 12, 11));
        
        DateFactory.getInstance().setNow(new DateTime(2013, 1, 1, 12, 12));
        
        instance.userConfigurations = new HashMap<User, UserConfiguration>();
        instance.userConfigurations.put(user, userConfiguration);
        
        instance.sessionSummaries = new HashMap<User, SessionSummary>();
        instance.sessionSummaries.put(user, sessionSummary);
        
        instance.handleExceededSessions();

        verify(dbusAdapter, never()).requestLogout(user);
        verify(dbusAdapter, never()).forceLogout(user);
    }
    
    @Test
    public void testHandleExceededSessions_expiredUser() throws DBusException, LogoutImpossibleException {
        SessionProcessor instance = new SessionProcessor(dbusAdapter, dataAccess);
        User user = new User(123);
        UserConfiguration userConfiguration = new UserConfiguration(user);
        userConfiguration.setAllowedFromHour(0);
        userConfiguration.setAllowedFromMinute(0);
        userConfiguration.setAllowedUntilHour(23);
        userConfiguration.setAllowedUntilMinute(59);
        userConfiguration.setWarningDelay(5);
        userConfiguration.setOnlineLimit(1);
        
        SessionSummary sessionSummary = new SessionSummary(user);
        sessionSummary.addActiveTime(new DateTime(2013, 1, 1, 12, 10));
        sessionSummary.addActiveTime(new DateTime(2013, 1, 1, 12, 11));
        
        DateFactory.getInstance().setNow(new DateTime(2013, 1, 1, 12, 12));
        
        instance.userConfigurations = new HashMap<User, UserConfiguration>();
        instance.userConfigurations.put(user, userConfiguration);
        
        instance.sessionSummaries = new HashMap<User, SessionSummary>();
        instance.sessionSummaries.put(user, sessionSummary);
        
        instance.handleExceededSessions();

        verify(dbusAdapter).requestLogout(user);
        verify(dbusAdapter, never()).forceLogout(user);
    }
}