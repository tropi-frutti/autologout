/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

/**
 *
 * @author bertel
 */
public class UserConfiguration {
    User user;

    public User getUser() {
        return user;
    }
    long onlineLimit;
    @Override
    public int hashCode() {
        return this.user.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SessionSummary other = (SessionSummary) obj;
        if (this.user.hashCode() != other.user.hashCode()) {
            return false;
        }
        if (this.user.equals(other.user) == false) {
            return false;
        }
        return true;
    }
}
