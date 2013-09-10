/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import java.util.Date;
import net.familiesteiner.autologout.DateFactory;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author bertel
 */
public class SessionSummaryTest {
    
    @BeforeClass
    public static void setUpClass() {
        DateFactory.getInstance().setTestMode(true);
        DateFactory.getInstance().setNow(new DateTime(2013, 1, 1, 15, 30));
    }
    
    /**
     * Test of hashCode method, of class SessionSummary.
     */
    @Test
    public void testHashCode_equals() {
        SessionSummary instance1 = new SessionSummary(new User(123));
        SessionSummary instance2 = new SessionSummary(new User(123));
        SessionSummary instance3 = new SessionSummary(new User(321));
        assertEquals("hashcodes of same instances different", instance1.hashCode(), instance2.hashCode());
        assertTrue("instance1 and instance2 should be same", instance1.equals(instance2));
        if (instance1.hashCode() == instance3.hashCode()) {
            assertFalse("instance1 and instance3 should be different", instance1.equals(instance3));
        }
    }

    /**
     * Test of addActiveTime method, of class SessionSummary.
     */
    @Test
    public void testAddActiveTimeSameTime() {
        DateTime activeTime = new DateTime();
        SessionSummary instance = new SessionSummary(new User(123));
        instance.addActiveTime(activeTime);
        instance.addActiveTime(activeTime);
        
        assertEquals("the number of elements in activeTimes is wrong", 1, instance.activeTimes.size());
    }

    /**
     * Test of clearOutdatedActiveTimes method, of class SessionSummary.
     */
    
    @Test
    public void testClearOutdatedActiveTimes() {
        DateTime validUntil = new DateTime(2013, 1, 1, 15, 0);
        SessionSummary instance = new SessionSummary(new User(123));
        instance.addActiveTime(new DateTime(2013, 1, 1, 15, 20));
        instance.addActiveTime(new DateTime(2013, 1, 1, 14, 30));
        instance.addActiveTime(new DateTime(2013, 1, 1, 15, 25));
        instance.clearOutdatedActiveTimes(validUntil);
        
        assertEquals("the leftover time is wrong", 2, instance.countActiveMinutes());
    }

    /**
     * Test of countActiveMinutes method, of class SessionSummary.
     */
    @Test
    public void testCountActiveMinutes() {

        SessionSummary instance = new SessionSummary(new User(123));

        long result = instance.countActiveMinutes();
        assertEquals("wrong number of active minutes", 0L, result);
        
        instance.addActiveTime(new DateTime(2013, 1, 1, 15, 20));
        result = instance.countActiveMinutes();
        assertEquals("wrong number of active minutes", 1L, result);
        instance.addActiveTime(new DateTime(2013, 1, 1, 15, 30));
        result = instance.countActiveMinutes();
        assertEquals("wrong number of active minutes", 2L, result);
   }
   
   @Test
   public void testIsAlreadyWarnedToday() {
        SessionSummary instance = new SessionSummary(new User(123));
        
        assertFalse("warning must not be happened", instance.isAlreadyWarnedToday());
        
        DateTime warnTimeYesterday = new DateTime(2012, 12, 31, 15, 30);
        instance.setWarnTime(warnTimeYesterday.toDate());       
        assertFalse("warning must not be happened", instance.isAlreadyWarnedToday());
        
        DateTime warnTimeToday = new DateTime(2013, 1, 1, 10, 0);
        instance.setWarnTime(warnTimeToday.toDate());       
        assertTrue("warning must be happened", instance.isAlreadyWarnedToday());
   }
}