/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.familiesteiner.autologout.domain.User;
import org.freedesktop.ConsoleKit.Manager;
import org.freedesktop.ConsoleKit.Seat;
import org.freedesktop.ConsoleKit.Session;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author steinorb
 */
public class DBusAdapterTest {
    
    Manager manager = null;
    DBusAdapter instance = null;
    
    @Before
    public void setUp() {
        manager = mock(Manager.class);
        instance = new DBusAdapter(manager);
}
    
    /**
     * Test of identifyActiveSessions method, of class DBusAdapter.
     */
    @Test
    public void testIdentifyActiveSessions_NoSeat() {
        when(manager.GetSeats()).thenReturn(new LinkedList<DBusInterface>());
        Set expResult = new HashSet();
        Set result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }

    @Test
    public void testIdentifyActiveSessions_NoSession() {
        LinkedList<DBusInterface> seats = new LinkedList<DBusInterface>();
        Seat seat = mock(Seat.class);
        seats.add(seat);
        LinkedList<DBusInterface> sessions = new LinkedList<DBusInterface>();
        when(manager.GetSeats()).thenReturn(seats);
        when(seat.GetSessions()).thenReturn(sessions);
        Set expResult = new HashSet();
        Set result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }

    @Test
    public void testIdentifyActiveSessions_OneActiveSession() {
        LinkedList<DBusInterface> seats = new LinkedList<DBusInterface>();
        Seat seat = mock(Seat.class);
        seats.add(seat);
        LinkedList<DBusInterface> sessions = new LinkedList<DBusInterface>();
        Session session = mock(Session.class);
        sessions.add(session);
        when(manager.GetSeats()).thenReturn(seats);
        when(seat.GetSessions()).thenReturn(sessions);
        when(session.GetUnixUser()).thenReturn(new UInt32(123));
        when(session.IsActive()).thenReturn(Boolean.TRUE);
        Set<User> expResult = new HashSet();
        expResult.add(new User(123));
        Set<User> result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIdentifyActiveSessions_OneInactiveSession() {
        LinkedList<DBusInterface> seats = new LinkedList<DBusInterface>();
        Seat seat = mock(Seat.class);
        seats.add(seat);
        LinkedList<DBusInterface> sessions = new LinkedList<DBusInterface>();
        Session session = mock(Session.class);
        sessions.add(session);
        when(manager.GetSeats()).thenReturn(seats);
        when(seat.GetSessions()).thenReturn(sessions);
        when(session.GetUnixUser()).thenReturn(new UInt32(123));
        when(session.IsActive()).thenReturn(Boolean.FALSE);
        Set<User> expResult = new HashSet();
        Set<User> result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }

    @Test
    public void testIdentifyActiveSessions_TwoActiveSession() {
        LinkedList<DBusInterface> seats = new LinkedList<DBusInterface>();
        Seat seat = mock(Seat.class);
        seats.add(seat);
        LinkedList<DBusInterface> sessions = new LinkedList<DBusInterface>();
        Session session1 = mock(Session.class);
        sessions.add(session1);
        Session session2 = mock(Session.class);
        sessions.add(session2);
        when(manager.GetSeats()).thenReturn(seats);
        when(seat.GetSessions()).thenReturn(sessions);
        when(session1.GetUnixUser()).thenReturn(new UInt32(123));
        when(session1.IsActive()).thenReturn(Boolean.TRUE);
        when(session2.GetUnixUser()).thenReturn(new UInt32(123));
        when(session2.IsActive()).thenReturn(Boolean.TRUE);
        Set<User> expResult = new HashSet();
        expResult.add(new User(123));
        Set<User> result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }
    @Test
    public void testIdentifyActiveSessions_TwoSeatsAndSameSessions() {
        LinkedList<DBusInterface> seats = new LinkedList<DBusInterface>();
        Seat seat1 = mock(Seat.class);
        seats.add(seat1);
        Seat seat2 = mock(Seat.class);
        seats.add(seat2);
        LinkedList<DBusInterface> sessions1 = new LinkedList<DBusInterface>();
        Session session1 = mock(Session.class);
        sessions1.add(session1);
        LinkedList<DBusInterface> sessions2 = new LinkedList<DBusInterface>();
        Session session2 = mock(Session.class);
        sessions2.add(session2);
        when(manager.GetSeats()).thenReturn(seats);
        when(seat1.GetSessions()).thenReturn(sessions1);
        when(session1.GetUnixUser()).thenReturn(new UInt32(123));
        when(session1.IsActive()).thenReturn(Boolean.TRUE);
        when(session2.GetUnixUser()).thenReturn(new UInt32(123));
        when(session2.IsActive()).thenReturn(Boolean.TRUE);
        Set<User> expResult = new HashSet();
        expResult.add(new User(123));
        Set<User> result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }
    @Test
    public void testIdentifyActiveSessions_TwoSeatsAndDifferentSessions() {
        LinkedList<DBusInterface> seats = new LinkedList<DBusInterface>();
        Seat seat1 = mock(Seat.class);
        seats.add(seat1);
        Seat seat2 = mock(Seat.class);
        seats.add(seat2);
        LinkedList<DBusInterface> sessions1 = new LinkedList<DBusInterface>();
        Session session1 = mock(Session.class);
        sessions1.add(session1);
        LinkedList<DBusInterface> sessions2 = new LinkedList<DBusInterface>();
        Session session2 = mock(Session.class);
        sessions2.add(session2);
        when(manager.GetSeats()).thenReturn(seats);
        when(seat1.GetSessions()).thenReturn(sessions1);
        when(seat2.GetSessions()).thenReturn(sessions2);
        when(session1.GetUnixUser()).thenReturn(new UInt32(123));
        when(session1.IsActive()).thenReturn(Boolean.TRUE);
        when(session2.GetUnixUser()).thenReturn(new UInt32(321));
        when(session2.IsActive()).thenReturn(Boolean.TRUE);
        Set<User> expResult = new HashSet();
        expResult.add(new User(123));
        expResult.add(new User(321));
        Set<User> result = instance.identifyActiveSessions();
        assertNotNull(result);
        assertEquals(expResult.size(),result.size());
        assertEquals(expResult, result);
    }
    
//    @Test
    public void testGetSessionAddress() {
        String address = instance.getSessionAddress(new User(1000));
        assertNotNull("the address must not be null", address);
    }
    
//    @Test
    public void testRequestLogout() throws DBusException {
        instance.requestLogout(new User(1000));
    }
    
//    @Test
    public void testForceLogout() throws DBusException {
        instance.forceLogout(new User(1000));
    }

 }