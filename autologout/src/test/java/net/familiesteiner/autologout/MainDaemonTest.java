package net.familiesteiner.autologout;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.freedesktop.ConsoleKit.Manager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class MainDaemonTest
{
    @Test
    public void testGuice()
    {
        Injector injector = Guice.createInjector(new AutologoutTestModule());
        TimerService timerService = injector.getInstance(TimerService.class);
        
        assertNotNull("timerService must be created", timerService);
        SessionProcessor sessionProcessor = (SessionProcessor) timerService.getSessionProcessor();
        assertNotNull("processor must be set", sessionProcessor);
        DBusAdapter adapter = (DBusAdapter) sessionProcessor.getDbusAdapter();
        assertNotNull("adapter must be set", adapter);
        Manager manager = adapter.getConsoleKitManager();
        assertNotNull("manager must be set", manager);


    }
}
