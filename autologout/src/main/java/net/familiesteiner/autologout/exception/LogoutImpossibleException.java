/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.exception;

/**
 *
 * @author bertel
 */
public class LogoutImpossibleException extends Exception {

    /**
     * Creates a new instance of
     * <code>LogoutImpossibleException</code> without detail message.
     */
    public LogoutImpossibleException() {
    }

    /**
     * Constructs an instance of
     * <code>LogoutImpossibleException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public LogoutImpossibleException(String msg) {
        super(msg);
    }
}
