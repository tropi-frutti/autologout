/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
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
    DataAccessInterface dataAccess = null;

    public DBusAdapterInterface getDbusAdapter() {
        return dbusAdapter;
    }
    
    @Inject
    public SessionProcessor(DBusAdapterInterface dbusAdapter, DataAccessInterface dataAccess) {
        this.dbusAdapter = dbusAdapter;
        this.dataAccess = dataAccess;
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
        XStream xstream = new XStream();

        Set<User> users = this.dbusAdapter.identifyActiveSessions();
        for (User user : users) {
            LOG.info("active User: " + user.getUid());
            LOG.info("active User xml: " + xstream.toXML(user));
        }
        String usersString = xstream.toXML(users);
        LOG.info("all active Users xml: " + usersString);
        Set<User> newUsers = (Set<User>) xstream.fromXML(usersString);
        for (User user : newUsers) {
            LOG.info("active User: " + user.getUid());
            LOG.info("active User xml: " + xstream.toXML(user));
        }
    }
    
}
