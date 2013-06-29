package org.freedesktop.ConsoleKit;
import java.util.List;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Seat extends DBusInterface
{
   public static class DeviceRemoved extends DBusSignal
   {
      public final Struct2 a;
      public DeviceRemoved(String path, Struct2 a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class DeviceAdded extends DBusSignal
   {
      public final Struct3 a;
      public DeviceAdded(String path, Struct3 a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class SessionRemoved extends DBusSignal
   {
      public final DBusInterface a;
      public SessionRemoved(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class SessionAdded extends DBusSignal
   {
      public final DBusInterface a;
      public SessionAdded(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class ActiveSessionChanged extends DBusSignal
   {
      public final String a;
      public ActiveSessionChanged(String path, String a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }

  public void ActivateSession(DBusInterface ssid);
  public boolean CanActivateSessions();
  public DBusInterface GetActiveSession();
  public List<Struct1> GetDevices();
  public List<DBusInterface> GetSessions();
  public DBusInterface GetId();

}
