/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import org.freedesktop.dbus.UInt32;

/**
 *
 * @author bertel
 */
public class User {
    long uid;
    public User(UInt32 userId) {
        uid = userId.longValue();
    }
}
