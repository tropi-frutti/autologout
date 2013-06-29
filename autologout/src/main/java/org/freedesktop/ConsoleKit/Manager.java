package org.freedesktop.ConsoleKit;


import java.util.List;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

public interface Manager extends DBusInterface
{
   public static class SystemIdleHintChanged extends DBusSignal
   {
      public final boolean a;
      public SystemIdleHintChanged(String path, boolean a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class SeatRemoved extends DBusSignal
   {
      public final DBusInterface a;
      public SeatRemoved(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class SeatAdded extends DBusSignal
   {
      public final DBusInterface a;
      public SeatAdded(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }

  public String GetSystemIdleSinceHint();
  public boolean GetSystemIdleHint();
  public List<DBusInterface> GetSessionsForUser(UInt32 uid);
  public List<DBusInterface> GetSessionsForUnixUser(UInt32 uid);
  public DBusInterface GetCurrentSession();
  public DBusInterface GetSessionForUnixProcess(UInt32 pid);
  public DBusInterface GetSessionForCookie(String cookie);
  public List<DBusInterface> GetSessions();
  public List<DBusInterface> GetSeats();
  public boolean CloseSession(String cookie);
  public String OpenSessionWithParameters(List<Struct1> parameters);
  public String OpenSession();
  public boolean CanStop();
  public void Stop();
  public boolean CanRestart();
  public void Restart();

}

