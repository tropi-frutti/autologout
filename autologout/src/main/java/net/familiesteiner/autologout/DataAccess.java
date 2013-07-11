/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.familiesteiner.autologout.domain.User;

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
    public void save(User user) {
        BufferedWriter writer = null;
        File file = null;
        FileWriter fileWriter = null;
            XStream xstream = new XStream();
            String content = xstream.toXML(user);
            long uid = user.getUid();
            file = new File(this.rootDirectory, String.valueOf(uid)+".xml");
        try {
            fileWriter = new FileWriter(file, false);
            writer = new BufferedWriter(fileWriter);
            writer.write(content);
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
    public Set<User> loadAll() {
        XStream xstream = new XStream();
        Set<User> result = new HashSet<User>();
        File rootDirectoryFile = new File(this.rootDirectory);
        File[] files = rootDirectoryFile.listFiles(new FilenameFilter() {

            public boolean accept(File file, String string) {
                if (string.endsWith(".xml"))
                    return true;
                else
                    return false;
            }
        });
        if (null != files) {
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = null;
                File file = files[i];
                User user = (User)xstream.fromXML(file);
                result.add(user);
            }
        }
        return result;
    }
}
