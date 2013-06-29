package org.freedesktop.ConsoleKit;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Session extends DBusInterface
{
   public static class Unlock extends DBusSignal
   {
      public Unlock(String path) throws DBusException
      {
         super(path);
      }
   }
   public static class Lock extends DBusSignal
   {
      public Lock(String path) throws DBusException
      {
         super(path);
      }
   }
   public static class IdleHintChanged extends DBusSignal
   {
      public final boolean a;
      public IdleHintChanged(String path, boolean a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }
   public static class ActiveChanged extends DBusSignal
   {
      public final boolean a;
      public ActiveChanged(String path, boolean a) throws DBusException
      {
         super(path, a);
         this.a = a;
      }
   }

  public void SetIdleHint(boolean idle_hint);
  public String GetIdleSinceHint();
  public boolean GetIdleHint();
  public void Unlock();
  public void Lock();
  public void Activate();
  public String GetCreationTime();
  public boolean IsLocal();
  public boolean IsActive();
  public String GetLoginSessionId();
  public String GetRemoteHostName();
  public String GetDisplayDevice();
  public String GetX11DisplayDevice();
  public String GetX11Display();
  public UInt32 GetUnixUser();
  public UInt32 GetUser();
  public String GetSessionType();
  public DBusInterface GetSeatId();
  public DBusInterface GetId();

}
