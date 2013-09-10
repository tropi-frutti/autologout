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
    public void calculateActiveTimes();
    public void loadSessions();
    public void saveSessions();
    public void reenableClosedSessions();
    public void handleExceededSessions();
}
