/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bertel
 */
public class TimerService {
    SessionProcessorInterface sessionProcessor = null;

    public SessionProcessorInterface getSessionProcessor() {
        return sessionProcessor;
    }
    Timer timer = null;
    private static Logger LOG = LoggerFactory.getLogger(TimerService.class);
    
    @Inject
    public TimerService(SessionProcessorInterface sessionProcessor) {
        this.sessionProcessor = sessionProcessor;
        timer = new Timer(true);
    }
    public void start() {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                LOG.info("run");
                sessionProcessor.ping();
                sessionProcessor.traceCurrentActiveSessions();
            }
        }, 0, 60*1000);
    }
    
    public void stop() {
        LOG.info("stop");
        timer.cancel();
    }
}
