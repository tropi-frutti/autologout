package net.familiesteiner.autologout;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.net.URI;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;
import org.freedesktop.dbus.exceptions.DBusException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
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
    HttpServer server;
    static DaemonController daemonController = null;
    
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/autologout/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("net.familiesteiner.autologout");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
    
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
        
        this.server = startServer();
        
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
        
        if (null != this.server) {
            this.server.stop();
        }
        
        LOG.exit();
    }
    
    public static DaemonController getDaemonController() {
        return daemonController;
    }
}
