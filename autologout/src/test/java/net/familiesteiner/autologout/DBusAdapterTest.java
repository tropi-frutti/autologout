/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.Set;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.dbus.exceptions.DBusException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bertel
 */
public class DBusAdapterTest {
    
    public DBusAdapterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class DBusAdapter.
     */
    @Test
    public void testInit() throws DBusException {
        DBusAdapter instance = new DBusAdapter();
        instance.init();
    }

    /**
     * Test of identifyActiveSessions method, of class DBusAdapter.
     */
    @Test
    public void testIdentifyActiveSessions() {
        Manager consoleKitManager = null;
        DBusAdapter instance = new DBusAdapter();
        Set expResult = null;
//        Set result = instance.identifyActiveSsessions(consoleKitManager);
    }
}