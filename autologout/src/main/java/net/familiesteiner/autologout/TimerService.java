/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.daemon.DaemonController;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author bertel
 */
public class TimerService {
    SessionProcessorInterface sessionProcessor = null;
    DaemonController daemonController = null;

    public DaemonController getDaemonController() {
        return daemonController;
    }

    public void setDaemonController(DaemonController daemonController) {
        this.daemonController = daemonController;
    }

    public SessionProcessorInterface getSessionProcessor() {
        return sessionProcessor;
    }
    Timer timer = null;
    private static XLogger LOG = XLoggerFactory.getXLogger(TimerService.class);
    
    @Inject
    public TimerService(SessionProcessorInterface sessionProcessor) {
        this.sessionProcessor = sessionProcessor;
        timer = new Timer(true);
    }
    public void start() {
        LOG.entry();
        this.sessionProcessor.loadSessions();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                LOG.entry();
                try {
                    sessionProcessor.calculateActiveTimes();
                    sessionProcessor.handleExceededSessions();
                    sessionProcessor.reenableClosedSessions();
                    sessionProcessor.saveSessions();
                }
                catch (RuntimeException ex) {
                    // consume exception and continue
                    LOG.catching(ex);
//                    timer.cancel();
//                    daemonController.fail(ex);
                }
                LOG.exit();
            }
        }, 0, 60*1000); // trigger once a minute
        LOG.exit();
    }
    
    public void stop() {
        LOG.entry();
        timer.cancel();
        this.sessionProcessor.saveSessions();
        LOG.exit();
    }
}
