package com.alxgrk.myownadventcalendar.date;

import android.support.annotation.VisibleForTesting;

import java.util.Calendar;

public class DateUtils {

    public SimpleDate getToday() {
        Calendar today = Calendar.getInstance();
        return SimpleDate.from(today);
    }

    public int daysUntilChristmas() {
        SimpleDate today = getToday();
        return daysUntilChristmas(today);
    }

    @VisibleForTesting
    protected int daysUntilChristmas(SimpleDate today) {
        int currentYear = today.getYear();
        SimpleDate christmas = new SimpleDate(24, 12, currentYear);

        if(today.after(christmas)) {
            christmas.setYear(currentYear + 1);
        }

        return today.differenceInDays(christmas);
    }

}
