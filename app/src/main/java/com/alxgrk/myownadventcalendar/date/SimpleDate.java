package com.alxgrk.myownadventcalendar.date;

import android.support.annotation.VisibleForTesting;

import java.util.Calendar;

/**
 * Created by alex on 19.11.16.
 *
 * A simple date only representing day, month and year.
 * NOTE: There will not be any check, whether the dates are even possible!
 * Value range for <code>day</code> should be 1 to 31.
 * Value range for <code>month</code> should be 1 to 12.
 * Value range for <code>year</code> should be 1 to 9999.
 */
public class SimpleDate {

    public static SimpleDate from(Calendar calendar) {
        return new SimpleDate(calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    public void applyOn(Calendar calendar) {
        calendar.set(Calendar.DATE, getDay());
        calendar.set(Calendar.MONTH, getMonth() - 1);
        calendar.set(Calendar.YEAR, getYear());

        // necessary to achieve re-assurance of lenient dates
        calendar.getTime();
    }

    public void setFrom(Calendar calendar) {
        this.day = calendar.get(Calendar.DATE);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.year = calendar.get(Calendar.YEAR);
    }

    private int day;

    private int month;

    private int year;

    public SimpleDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    @VisibleForTesting
    protected void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    @VisibleForTesting
    protected void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    @VisibleForTesting
    protected void setYear(int year) {
        this.year = year;
    }

    public int differenceInDays(SimpleDate another) {
        int count = 0;
        while (!this.same(another)) {
            Calendar calendar = Calendar.getInstance();
            if (this.before(another)) {
                this.applyOn(calendar);
                addDayTo(calendar);
                this.setFrom(calendar);
            } else {
                another.applyOn(calendar);
                addDayTo(calendar);
                another.setFrom(calendar);
            }
            count++;
        }
        return count;
    }

    private void addDayTo(Calendar calendar) {
        int monthBefore = calendar.get(Calendar.MONTH);
        calendar.roll(Calendar.DAY_OF_YEAR, true);
        int monthAfter = calendar.get(Calendar.MONTH);

        if((Calendar.DECEMBER == monthBefore) && (Calendar.JANUARY == monthAfter)) {
            calendar.roll(Calendar.YEAR, true);
            // necessary to achieve re-assurance of lenient dates
            calendar.getTime();
        }
    }

    public boolean same(SimpleDate another) {
        return this.getDay() == another.getDay()
                && this.getMonth() == another.getMonth()
                && this.getYear() == another.getYear();
    }

    public boolean before(SimpleDate another) {
        int yearComp = Integer.compare(this.getYear(), another.getYear());
        if(0 == yearComp) {
            int monthComp = Integer.compare(this.getMonth(), another.getMonth());
            if(0 == monthComp) {
                int dayComp = Integer.compare(this.getDay(), another.getDay());
                return dayComp < 0;
            } else {
                return monthComp < 0;
            }
        } else {
            return yearComp < 0;
        }
    }

    public boolean after(SimpleDate another) {
        int yearComp = Integer.compare(this.getYear(), another.getYear());
        if(0 == yearComp) {
            int monthComp = Integer.compare(this.getMonth(), another.getMonth());
            if(0 == monthComp) {
                int dayComp = Integer.compare(this.getDay(), another.getDay());
                return dayComp > 0;
            } else {
                return monthComp > 0;
            }
        } else {
            return yearComp > 0;
        }
    }

    @Override
    public String toString() {
        return "SimpleDate{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
