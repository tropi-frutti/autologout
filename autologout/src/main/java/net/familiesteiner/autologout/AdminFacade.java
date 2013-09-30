package net.familiesteiner.autologout;

import java.util.Timer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.daemon.DaemonController;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("adminfacade")
public class AdminFacade {
    private static XLogger LOG = XLoggerFactory.getXLogger(AdminFacade.class);
    static final String RESULT = "Autologout gestoppt";

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String stopDaemonString() {
        LOG.entry();
        String result = RESULT;
        stopDaemon();
        LOG.exit(result);
        return result;
    }
    
    private void stopDaemon() {
        LOG.entry();
        Timer timer = TimerService.getTimer();
        if (null != timer) {
            LOG.debug("cancelling timer");
            timer.cancel();
        }
        LOG.exit();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String stopDaemonHtml() {
        String result = "<html> " + "<title>" + "Autologout ausschalten" + "</title>"
          + "<body><h1>" + RESULT + "</body></h1>" + "</html> ";
        stopDaemon();
        LOG.exit(result);
        return result;
    }
}
