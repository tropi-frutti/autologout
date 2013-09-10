/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;

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
    int allowedFromHour;
    int allowedFromMinute;
    int allowedUntilHour;
    int allowedUntilMinute;

    public int getAllowedFromHour() {
        return allowedFromHour;
    }

    public void setAllowedFromHour(int allowedFromHour) {
        this.allowedFromHour = allowedFromHour;
    }

    public int getAllowedFromMinute() {
        return allowedFromMinute;
    }

    public void setAllowedFromMinute(int allowedFromMinute) {
        this.allowedFromMinute = allowedFromMinute;
    }

    public int getAllowedUntilHour() {
        return allowedUntilHour;
    }

    public void setAllowedUntilHour(int allowedUntilHour) {
        this.allowedUntilHour = allowedUntilHour;
    }

    public int getAllowedUntilMinute() {
        return allowedUntilMinute;
    }

    public void setAllowedUntilMinute(int allowedUntilMinute) {
        this.allowedUntilMinute = allowedUntilMinute;
    }

    public long getWarningDelay() {
        return warningDelay;
    }

    public void setWarningDelay(long warningDelay) {
        this.warningDelay = warningDelay;
    }

    public long getOnlineLimit() {
        return onlineLimit;
    }
    
    public void setOnlineLimit(long onlineLimit) {
        this.onlineLimit = onlineLimit;
    }
    
    public Interval getAllowedInterval() {
        DateTime from = new LocalTime(this.allowedFromHour, this.allowedFromMinute, 0).toDateTimeToday();
        DateTime until = new LocalTime(this.allowedUntilHour, this.allowedUntilMinute, 0).toDateTimeToday();
        return new Interval(from, until);
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
    
    @Override
    public String toString() {
         return ReflectionToStringBuilder.toString(this);
    }
}
