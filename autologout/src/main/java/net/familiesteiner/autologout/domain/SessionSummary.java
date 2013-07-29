/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author bertel
 */
public class SessionSummary {

    User user;
    boolean dirty = false;
    Date lastActive;

    public Date getLastActive() {
        return lastActive;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public User getUser() {
        return user;
    }

    TreeSet<Date> activeTimes;

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

    public SessionSummary(User user) {
        activeTimes = new TreeSet<Date>();
        this.user = user;
    }
    
    public void addActiveTime(Date activeTime) {
        this.activeTimes.add(activeTime);
        this.dirty = true;
        this.lastActive = activeTime;
    }
    
    public void clearOutdatedActiveTimes(Date validUntil) {
        List<Date> datesToRemove = new ArrayList<Date>();  
        for (Date date : activeTimes) {
            if (date.before(validUntil)) {
                datesToRemove.add(date);
            }
        }
        this.activeTimes.removeAll(datesToRemove);
    }
    
    public long countActiveMinutes() {
        return this.activeTimes.size();
    }
}
