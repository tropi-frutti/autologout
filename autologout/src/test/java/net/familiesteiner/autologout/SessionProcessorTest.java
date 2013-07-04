/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.HashSet;
import java.util.Set;
import net.familiesteiner.autologout.domain.User;
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
    
    public SessionProcessorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        dbusAdapter = mock(DBusAdapterInterface.class);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of ping method, of class SessionProcessor.
     */
    @Test
    public void testPing() {
        System.out.println("ping");
        SessionProcessor instance = new SessionProcessor(dbusAdapter);
        instance.ping();
    }

    /**
     * Test of traceCurrentActiveSessions method, of class SessionProcessor.
     */
    @Test
    public void testTraceCurrentActiveSessions() {
        System.out.println("traceCurrentActiveSessions");
        Set<User> users = new HashSet<User>();
        users.add(new User(123));
        when(dbusAdapter.identifyActiveSessions()).thenReturn(users);
        SessionProcessor instance = new SessionProcessor(dbusAdapter);
        instance.traceCurrentActiveSessions();
    }
}