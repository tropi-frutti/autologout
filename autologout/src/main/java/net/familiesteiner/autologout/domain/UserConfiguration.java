/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import java.util.Date;

/**
 *
 * @author bertel
 */
public class UserConfiguration {
    User user;

    public UserConfiguration(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }
    long onlineLimit;
    long warningDelay;
    Date userWarned;

    public Date getUserWarned() {
        return userWarned;
    }

    public void setUserWarned(Date userWarned) {
        this.userWarned = userWarned;
    }

    public long getOnlineLimit() {
        return onlineLimit;
    }

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
        final UserConfiguration other = (UserConfiguration) obj;
        if (this.user.hashCode() != other.user.hashCode()) {
            return false;
        }
        if (this.user.equals(other.user) == false) {
            return false;
        }
        return true;
    }
}
