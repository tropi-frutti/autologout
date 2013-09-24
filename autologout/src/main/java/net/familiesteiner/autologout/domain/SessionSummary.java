/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import net.familiesteiner.autologout.DateFactory;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 *
 * @author bertel
 */
public class SessionSummary {

    private static XLogger LOG = XLoggerFactory.getXLogger(SessionSummary.class);
    User user;
    boolean dirty = false;
    Date warnTime;
    Date lockTime;

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }
    TreeSet<Date> activeTimes;


    public Date getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(Date warnTime) {
        this.warnTime = warnTime;
    }

    public void markAsWarned() {
        this.warnTime = DateFactory.getInstance().now().toDate();
    }

    public void markAsLocked() {
        this.lockTime = DateFactory.getInstance().now().toDate();
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
    
    public void addActiveTime(DateTime activeTime) {
        LOG.entry(activeTime);
        this.activeTimes.add(activeTime.toDate());
        this.dirty = true;
        LOG.exit();
    }

    public void clearOutdatedActiveTimes(DateTime validUntil) {
        LOG.entry(validUntil);
        List<Date> datesToRemove = new ArrayList<Date>();  
        for (Date date : activeTimes) {
            if (new DateTime(date).isBefore(validUntil)) {
                LOG.debug("remove " + date);
                datesToRemove.add(date);
            }
        }
        this.activeTimes.removeAll(datesToRemove);
        LOG.exit();        
    }
    
    public long countActiveMinutes() {
        return this.activeTimes.size();
    }
    
    public boolean isAlreadyWarnedToday() {
        boolean result = false;
        if (this.warnTime != null) {
            LocalDate nowDate = new LocalDate(DateFactory.getInstance().now());
            LocalDate warnDate = new LocalDate(this.warnTime);
            if (nowDate.isEqual(warnDate)) {
                result = true;
            }
        }
        return result;
    }
    
    public boolean isLockedToday() {
        boolean result = false;
        if (this.lockTime != null) {
            LocalDate nowDate = new LocalDate(DateFactory.getInstance().now());
            LocalDate lockDate = new LocalDate(this.lockTime);
            if (nowDate.isEqual(lockDate)) {
                result = true;
            }
        }
        return result;
    }

    public boolean isWarningDelayTimedOut(long delayInMinutes) {
        boolean result = false;        
        if (this.warnTime != null) {
            DateTime delayTimedOut = DateFactory.getInstance().now();
            delayTimedOut = delayTimedOut.minusMinutes((int) delayInMinutes);
            DateTime warnDateTime = new DateTime(this.warnTime);
            if (delayTimedOut.isAfter(warnDateTime)) {
                result = true;
            }
        }
        return result;
    }
    
    public String toString() {
        return (new ReflectionToStringBuilder(this) {
            protected Object getValue(Field f) throws IllegalArgumentException, IllegalAccessException {
                if (f.getName().equals("activeTimes")) {
                    return activeTimes==null?null:activeTimes.size();
                }
                else {
                    return super.getValue(f);
                }
            }
        }).toString();
    }
}
