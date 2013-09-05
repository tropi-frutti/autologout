/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.UserConfiguration;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author bertel
 */
public class DataAccess implements DataAccessInterface {
    String rootDirectory = null;
    private static XLogger LOG = XLoggerFactory.getXLogger(SessionProcessor.class);

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void save(SessionSummary sessionSummary) {
        LOG.entry(sessionSummary.getUser().getUid());
        BufferedWriter writer = null;
        File file = null;
        FileWriter fileWriter = null;
            XStream xstream = new XStream();
            xstream.alias("sessionSummary", SessionSummary.class);
            xstream.omitField(SessionSummary.class, "lastActive");
            xstream.omitField(SessionSummary.class, "dirty");
            String content = xstream.toXML(sessionSummary);
            long uid = sessionSummary.getUser().getUid();
            file = new File(this.rootDirectory, String.valueOf(uid)+".xml");
        try {
            fileWriter = new FileWriter(file, false);
            writer = new BufferedWriter(fileWriter);
            writer.write(content);
            sessionSummary.setDirty(false);
        } catch (IOException ex) {
            LOG.catching(ex);
        }
        finally {
            try {
                if (null != writer) {
                writer.close();
                }
            } catch (IOException ex) {
                LOG.catching(ex);
            }
            try {
                if (null != fileWriter) {
                fileWriter.close();
                }
            } catch (IOException ex) {
                LOG.catching(ex);
            }
        }
        LOG.exit();
   }

    @Override
    public Set<SessionSummary> loadAllSessionSummaries() {
        LOG.entry();
        XStream xstream = new XStream();
        xstream.alias("sessionSummary", SessionSummary.class);
        Set<SessionSummary> result = new HashSet<SessionSummary>();
        File rootDirectoryFile = new File(this.rootDirectory);
        File[] files = rootDirectoryFile.listFiles(new FilenameFilter() {

            public boolean accept(File file, String string) {
                if (string.endsWith("_sessionSummary.xml"))
                    return true;
                else
                    return false;
            }
        });
        if (null != files) {
            LOG.info("found " + files.length + " session summaries");
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = null;
                File file = files[i];
                LOG.debug("loading: " + file.getAbsolutePath());
                SessionSummary sessionSummary = (SessionSummary)xstream.fromXML(file);
                result.add(sessionSummary);
            }
        }
        LOG.exit();
        return result;
    }

    public Set<UserConfiguration> loadAllUserConfigurations() {
        LOG.entry();
        XStream xstream = new XStream();
        xstream.alias("userConfiguration", UserConfiguration.class);
        Set<UserConfiguration> result = new HashSet<UserConfiguration>();
        File configFile = new File(this.rootDirectory, "autologout.xml");
        if (configFile.exists()) {
            LOG.debug("loading config from " + configFile.getAbsolutePath());
            List xstreamResult = (List) xstream.fromXML(configFile);
            for (Object object : xstreamResult) {
                UserConfiguration userConfiguration = (UserConfiguration) object;
                LOG.debug("found config for user: " + userConfiguration.getUser().getUid());
                result.add(userConfiguration);
            }
        }
        else {
            LOG.warn("config does not exist: " + configFile.getAbsolutePath());            
        }
        
        LOG.exit();
        return result;
    }
}
