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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.familiesteiner.autologout.domain.SessionSummary;
import net.familiesteiner.autologout.domain.UserConfiguration;

/**
 *
 * @author bertel
 */
public class DataAccess implements DataAccessInterface {
    String rootDirectory = null;

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void save(SessionSummary sessionSummary) {
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
            Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if (null != writer) {
                writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (null != fileWriter) {
                fileWriter.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
   }

    @Override
    public Set<SessionSummary> loadAllSessionSummaries() {
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
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = null;
                File file = files[i];
                SessionSummary sessionSummary = (SessionSummary)xstream.fromXML(file);
                result.add(sessionSummary);
            }
        }
        return result;
    }

    public Set<UserConfiguration> loadAllUserConfigurations() {
        XStream xstream = new XStream();
        xstream.alias("userConfiguration", UserConfiguration.class);
        Set<UserConfiguration> result = new HashSet<UserConfiguration>();
        File configFile = new File(this.rootDirectory, "autologout.xml");
        List xstreamResult = (List) xstream.fromXML(configFile);
        for (Object object : xstreamResult) {
            UserConfiguration userConfiguration = (UserConfiguration) object;
            result.add(userConfiguration);
        }
        
        return result;
    }
}
