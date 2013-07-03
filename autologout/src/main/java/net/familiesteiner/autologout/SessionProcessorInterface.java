/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

/**
 *
 * @author bertel
 */
public interface SessionProcessorInterface {
    public void ping();
    public void traceCurrentActiveSessions();
}
