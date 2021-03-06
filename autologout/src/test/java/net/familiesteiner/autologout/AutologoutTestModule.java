/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.io.File;
import org.freedesktop.ConsoleKit.Manager;
import org.mockito.Mockito;

/**
 *
 * @author bertel
 */
public class AutologoutTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SessionProcessorInterface.class).to(SessionProcessor.class);
        bind(DBusAdapterInterface.class).to(DBusAdapter.class);
        DateFactory.getInstance().setTestMode(true);
    }
    
    @Provides
    Manager provideManager() {
        return Mockito.mock(Manager.class);
    }
    
    @Provides
    DataAccessInterface provideDataAccessInterface() {
        DataAccess dataAccess = new DataAccess();
        dataAccess.setRootDirectory("./");
        dataAccess.setConfigDirectory("./");
        return dataAccess;
    }}
