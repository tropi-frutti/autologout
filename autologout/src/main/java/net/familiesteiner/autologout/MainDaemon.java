package net.familiesteiner.autologout;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class MainDaemon implements Daemon
{
    TimerService timerService;
    
    public static void main( String[] args ) throws DBusException
    {
        Logger logger = LoggerFactory.getLogger(MainDaemon.class);
        logger.info( "Hello World!" );
        MainDaemon daemon = new MainDaemon();
        try {
            daemon.init(null);
            daemon.start();
            Thread.sleep(10000);
            daemon.stop();
        } catch (InterruptedException ex) {
            logger.error(null, ex);
        } catch (DaemonInitException ex) {
            logger.error(null, ex);
        } catch (Exception ex) {
            logger.error(null, ex);
        }
        finally {
            daemon.destroy();            
        }
        logger.info("Goodbye");
    }
    
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        Injector injector = Guice.createInjector(new AutologoutModule());
        timerService = injector.getInstance(TimerService.class);
    }

    public void start() throws Exception {
        timerService.start();
    }

    public void stop() throws Exception {
        timerService.stop();
    }

    public void destroy() {
        if (null != timerService) {
            timerService.stop();
        }
        timerService = null;
    }
}
