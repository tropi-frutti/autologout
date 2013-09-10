/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.User;
import net.familiesteiner.autologout.domain.UserConfiguration;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author bertel
 */
public class DataAccessTest {
    DataAccess instance = null;
    String testRootDir = System.getProperty("java.io.tmpdir") + "/autologout";
    
    public DataAccessTest() {
    }
    
    @Before
    public void setUp() {
        instance = new DataAccess();
        instance.setRootDirectory(testRootDir);
        
        File testRootDirFile = new File(testRootDir);
        testRootDirFile.mkdir();
    }
    
    /**
     * Test of save method, of class DataAccess.
     */
    @Test
    public void testSave() {
        User user = new User(123);
        SessionSummary sessionSummary = new SessionSummary(user);
        sessionSummary.addActiveTime(new DateTime());
        instance.save(sessionSummary);
    }

    /**
     * Test of loadAll method, of class DataAccess.
     */
    @Test
    public void loadAllSessionSummaries() {
        instance.setRootDirectory("src/test/resources/testdata");
        Set<SessionSummary> expResult = new HashSet<SessionSummary>();
        SessionSummary sessionSummary = new SessionSummary((new User(321)));
        expResult.add(sessionSummary);
        Set result = instance.loadAllSessionSummaries();
        assertEquals(expResult, result);
        SessionSummary resultSummary = (SessionSummary) result.toArray()[0];
        assertEquals("number of contained elements is wrong", 2, resultSummary.countActiveMinutes());
    }

    /**
     * Test of loadAll method, of class DataAccess.
     */
    @Test
    public void loadAllUserConfigurations() {
        instance.setRootDirectory("src/test/resources/testdata");
        Set<UserConfiguration> result = instance.loadAllUserConfigurations();
        assertEquals("wrong number of elements", 2, result.size());
        assertTrue("user config 321 is not contained", result.contains(new UserConfiguration(new User(321))));
        assertTrue("user config 322 is not contained", result.contains(new UserConfiguration(new User(322))));
        for (UserConfiguration userConfiguration : result) {
            if(userConfiguration.getUser().getUid() == 321) {
                assertEquals("online limit is wrong", 60, userConfiguration.getOnlineLimit());                
            }
            else {
                assertEquals("online limit is wrong", 30, userConfiguration.getOnlineLimit());                                
            }
        }
    }
}