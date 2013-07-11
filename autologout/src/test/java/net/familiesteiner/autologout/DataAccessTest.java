/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.io.File;
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
        System.out.println("save");
        User user = new User(123);
        instance.save(user);
    }

    /**
     * Test of loadAll method, of class DataAccess.
     */
    @Test
    public void testLoadAll() {
        instance.setRootDirectory("src/test/resources/testdata");
        System.out.println("loadAll");
        Set expResult = new HashSet<User>();
        expResult.add(new User(321));
        Set result = instance.loadAll();
        assertEquals(expResult, result);
    }
}