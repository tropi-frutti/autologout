/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import java.util.Date;
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
    
    /**
     * Test of hashCode method, of class SessionSummary.
     */
    @Test
    public void testHashCode_equals() {
        System.out.println("hashCode");
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
        System.out.println("addActiveTime");
        Date activeTime = new Date();
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
        System.out.println("clearOutdatedActiveTimes");
        Date validUntil = new Date(50000);
        SessionSummary instance = new SessionSummary(new User(123));
        instance.addActiveTime(new Date(51000));
        instance.addActiveTime(new Date(49000));
        instance.addActiveTime(new Date(52000));
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
        
        instance.addActiveTime(new Date(0));
        result = instance.countActiveMinutes();
        assertEquals("wrong number of active minutes", 1L, result);
        instance.addActiveTime(new Date(12345));
        result = instance.countActiveMinutes();
        assertEquals("wrong number of active minutes", 2L, result);
   }
}