/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import java.util.Set;
import net.familiesteiner.autologout.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bertel
 */
public class SessionProcessor implements SessionProcessorInterface {
    private static Logger LOG = LoggerFactory.getLogger(TimerService.class);
    DBusAdapterInterface dbusAdapter = null;

    public DBusAdapterInterface getDbusAdapter() {
        return dbusAdapter;
    }
    
    @Inject
    public SessionProcessor(DBusAdapterInterface dbusAdapter) {
        this.dbusAdapter = dbusAdapter;
    }

    /**
     *
     */
    @Override
    public void ping() {
        LOG.info("ping");
    }

    @Override
    public void traceCurrentActiveSessions() {
        Set<User> users = this.dbusAdapter.identifyActiveSessions();
        for (User user : users) {
            LOG.info("active User: " + user.getUid());
        }
    }
    
}
