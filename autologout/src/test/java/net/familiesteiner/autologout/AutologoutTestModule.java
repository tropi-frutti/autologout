/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import net.familiesteiner.autologout.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.familiesteiner.autologout.mock.ManagerMock;
import org.freedesktop.ConsoleKit.Manager;

/**
 *
 * @author bertel
 */
public class AutologoutTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SessionProcessorInterface.class).to(SessionProcessor.class);
        bind(DBusAdapterInterface.class).to(DBusAdapter.class);
    }
    
    @Provides
    Manager provideManager() {
        return new ManagerMock();
    }
    
}
