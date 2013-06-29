package org.freedesktop.ConsoleKit;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;
public final class Struct3 extends Struct
{
   @Position(0)
   public final String a;
   @Position(1)
   public final String b;
  public Struct3(String a, String b)
  {
   this.a = a;
   this.b = b;
  }
}
