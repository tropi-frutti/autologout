package org.gnome;
import java.util.List;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
public interface SessionManager extends DBusInterface
{
   public static class SessionOver extends DBusSignal
   {
      public SessionOver(String path) throws DBusException
      {
         super(path);
      }
   }
   public static class SessionRunning extends DBusSignal
   {
      public SessionRunning(String path) throws DBusException
      {
         super(path);
      }
   }
   public static class InhibitorRemoved extends DBusSignal
   {
      public final DBusInterface a;
      public InhibitorRemoved(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class InhibitorAdded extends DBusSignal
   {
      public final DBusInterface a;
      public InhibitorAdded(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class ClientRemoved extends DBusSignal
   {
      public final DBusInterface a;
      public ClientRemoved(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class ClientAdded extends DBusSignal
   {
      public final DBusInterface a;
      public ClientAdded(String path, DBusInterface a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }

  public void RequestReboot();
  public void RequestShutdown();
  public boolean IsSessionRunning();
  public void Logout(UInt32 mode);
  public boolean CanShutdown();
  public void Shutdown();
  public boolean IsAutostartConditionHandled(String condition);
  public List<DBusInterface> GetInhibitors();
  public List<DBusInterface> GetClients();
  public boolean IsInhibited(UInt32 flags);
  public void Uninhibit(UInt32 inhibit_cookie);
  public UInt32 Inhibit(String app_id, UInt32 toplevel_xid, String reason, UInt32 flags);
  public void UnregisterClient(DBusInterface client_id);
  public DBusInterface RegisterClient(String app_id, String client_startup_id);
  public void InitializationError(String message, boolean fatal);
  public void Setenv(String variable, String value);

}
