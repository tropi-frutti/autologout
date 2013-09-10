package net.familiesteiner.autologout;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Hello world!
 *
 */
public class MainDaemon implements Daemon
{
    private static XLogger LOG = XLoggerFactory.getXLogger(MainDaemon.class);
    TimerService timerService;
    
    public static void main( String[] args ) throws DBusException
    {
        LOG.entry((Object[]) args);
        MainDaemon daemon = new MainDaemon();
        try {
            daemon.init(null);
            daemon.start();
            Thread.sleep(10000);
            daemon.stop();
        } catch (InterruptedException ex) {
            LOG.catching(ex);
        } catch (DaemonInitException ex) {
            LOG.catching(ex);
        } catch (Exception ex) {
            LOG.catching(ex);
        }
        finally {
            daemon.destroy();            
        }
        LOG.exit();
    }
    
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        LOG.entry();
        Injector injector = Guice.createInjector(new AutologoutModule());
        timerService = injector.getInstance(TimerService.class);
        timerService.setDaemonController(context.getController());
        LOG.exit();
    }

    public void start() throws Exception {
        LOG.entry();
        timerService.start();
        LOG.exit();
    }

    public void stop() throws Exception {
        LOG.entry();
        timerService.stop();
        LOG.exit();
    }

    public void destroy() {
        LOG.entry();
        if (null != timerService) {
            timerService.stop();
        }
        timerService = null;
        LOG.exit();
    }
}
