/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.familiesteiner.autologout;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 *
 * @author steinorb
 */
public class DateFactory {
    private static DateFactory instance = new DateFactory();
    private DateTime now;
    private boolean testMode = false;
    
    private DateFactory() {}
    
    public static DateFactory getInstance() {
        return instance;
    }

    public DateTime now() {
        return getNow();
    }

    private DateTime getNow() {
        DateTime nowResult;
        if (testMode) {
            nowResult = this.now.toDateTime();
        }
        else {
            nowResult = new DateTime();            
        }
        return nowResult;
    }

    public void setNow(DateTime now) {
        this.now = now;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    public DateTime getStartOfWeek() {
        return getNow().withTimeAtStartOfDay().withDayOfWeek(DateTimeConstants.MONDAY);
    }
    
}
