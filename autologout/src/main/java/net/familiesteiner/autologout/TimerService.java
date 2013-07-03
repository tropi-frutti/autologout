/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bertel
 */
public class TimerService {
    Timer timer = null;
    private static Logger LOG = LoggerFactory.getLogger(TimerService.class);
    public TimerService() {
        timer = new Timer(true);
    }
    public void start() {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                LOG.info("run");
            }
        }, 0, 1000);
    }
    
    public void stop() {
        LOG.info("stop");
        timer.cancel();
    }
}
