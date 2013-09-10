/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.freedesktop.dbus.UInt32;

/**
 *
 * @author bertel
 */
public class User {

    long uid;
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.uid ^ (this.uid >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.uid != other.uid) {
            return false;
        }
        return true;
    }

    public long getUid() {
        return uid;
    }

   public User(UInt32 userId) {
        this(userId.longValue());
    }
   
    public User(long userId) {
        uid = userId;
    }
    
    @Override
    public String toString() {
         return ReflectionToStringBuilder.toString(this);
    }
}   
 
