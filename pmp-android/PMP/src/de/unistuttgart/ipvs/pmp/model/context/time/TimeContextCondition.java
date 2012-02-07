package de.unistuttgart.ipvs.pmp.model.context.time;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The parsed condition for a {@link TimeContext}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class TimeContextCondition {
    
    private static Map<String, TimeContextCondition> cache = new HashMap<String, TimeContextCondition>();
    
    private static Pattern CONDITION_PATTERN = Pattern
            .compile("((utc)?)([0-2][0-9]):([0-5][0-9]):([0-5][0-9])-([0-2][0-9]):([0-5][0-9]):([0-5][0-9])-(.)([0-9,]*)");
    
    
    /**
     * Parses a {@link TimeContextCondition} from a string.
     * 
     * @param condition
     * @return
     */
    public static TimeContextCondition parse(String condition) {
        TimeContextCondition result = cache.get(condition);
        
        if (result == null) {
            Matcher match = CONDITION_PATTERN.matcher(condition);
            if (!match.matches()) {
                throw new IllegalArgumentException("TimeContextCondition was not formatted properly: " + condition);
            }
            
            boolean utc = match.group(1).length() > 0;
            int beginHour = Integer.parseInt(match.group(2));
            int beginMin = Integer.parseInt(match.group(3));
            int beginSec = Integer.parseInt(match.group(4));
            int endHour = Integer.parseInt(match.group(5));
            int endMin = Integer.parseInt(match.group(6));
            int endSec = Integer.parseInt(match.group(7));
            TimeContextConditionIntervalType tccit = TimeContextConditionIntervalType.getForIdentifier(match.group(8)
                    .charAt(0));
            List<Integer> tccdList = tccit.makeDays(match.group(9));
            
            result = new TimeContextCondition(utc, new TimeContextConditionTime(beginHour, beginMin, beginSec),
                    new TimeContextConditionTime(endHour, endMin, endSec), tccit, tccdList);
            cache.put(condition, result);
        }
        
        return result;
    }
    
    /**
     * Whether the time is fixed at a point, i.e. e.g. 08:00 always at this time zone,
     * then the time is converted to UTC and the information is in UTC.
     * If this is false the time is always relative to the local time zone of the user.
     */
    private boolean isUTC;
    
    /**
     * Begin and end during a 24-hrs period. May wrap.
     */
    private TimeContextConditionTime begin, end;
    
    /**
     * The interval to repeat the time, i.e. which days
     */
    private TimeContextConditionIntervalType interval;
    
    /**
     * The specific days in the interval
     */
    private List<Integer> days;
    
    
    public TimeContextCondition(boolean isUTC, TimeContextConditionTime begin, TimeContextConditionTime end,
            TimeContextConditionIntervalType interval, List<Integer> days) {
        this.isUTC = isUTC;
        this.begin = begin;
        this.end = end;
        this.interval = interval;
        this.days = days;
    }
    
    
    @Override
    public String toString() {
        return String.format("%s%2d:%2d:%2d-%2d:%2d:%2d-%s%s", this.isUTC ? "utc" : "", this.begin.getHour(),
                this.begin.getMinute(), this.begin.getSecond(), this.end.getHour(), this.end.getMinute(),
                this.end.getSecond(), this.interval.getIdentifier(), this.interval.makeList(this.days));
    }
    
    
    /**
     * Checks whether the condition is satisfied in the state
     * 
     * @param state
     * @return
     */
    public boolean satisfiedIn(long state) {
        Calendar cal;
        if (this.isUTC) {
            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        } else {
            cal = Calendar.getInstance();
        }
        cal.setTimeInMillis(state);
        
        // check day is okay
        switch (this.interval) {
            case REPEAT_DAILY:
                break;
            
            case REPEAT_WEEKLY:
                if (!this.days.contains(cal.get(Calendar.DAY_OF_WEEK))) {
                    return false;
                }
                break;
            
            case REPEAT_MONTHLY:
                if (!this.days.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                    return false;
                }
                break;
            
            case REPEAT_YEARLY:
                if (!this.days.contains(cal.get(Calendar.DAY_OF_YEAR))) {
                    return false;
                }
                break;
        }
        
        // check time
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        
        boolean timeWraps = this.begin.compareTo(this.end) > 0;
        boolean dateBetweenBeginAndEnd = (this.begin.getHour() <= hour) && (hour <= this.end.getHour())
                && (this.begin.getMinute() <= min) && (min <= this.end.getMinute()) && (this.begin.getSecond() <= sec)
                && (sec <= this.end.getSecond());
        
        // either it's NOT wrapping AND     begin <= date <= end
        //     or it's     wrapping AND NOT begin <= date <= end
        return timeWraps ^ dateBetweenBeginAndEnd;
    }
    
    
    /*
     * Getters / Setters for view
     */
    
    protected boolean isUTC() {
        return this.isUTC;
    }
    
    
    protected void setUTC(boolean isUTC) {
        this.isUTC = isUTC;
    }
    
    
    protected TimeContextConditionTime getBegin() {
        return this.begin;
    }
    
    
    protected TimeContextConditionTime getEnd() {
        return this.end;
    }
    
    
    protected TimeContextConditionIntervalType getInterval() {
        return this.interval;
    }
    
    
    protected void setInterval(TimeContextConditionIntervalType interval) {
        this.interval = interval;
    }
    
    
    protected List<Integer> getDays() {
        return this.days;
    }
    
    
    public boolean representsWholeDay() {
        return this.begin.getDifferenceInSeconds(this.end, true) >= TimeContextConditionTime.SECONDS_PER_DAY - 1;
    }
}
