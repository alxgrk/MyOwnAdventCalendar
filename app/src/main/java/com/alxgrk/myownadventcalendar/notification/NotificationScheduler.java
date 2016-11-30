/*
 * Created on 22.01.2016
 *
 * author Alex
 */
package com.alxgrk.myownadventcalendar.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.myownadventcalendar.date.SimpleDate;

import java.util.Calendar;

public class NotificationScheduler {

    private static final String TAG = NotificationScheduler.class.getSimpleName();

    private static final int REQUEST_CODE = 84685;

    private Context appContext;

    public NotificationScheduler(@NonNull Context appContext) {
        this.appContext = appContext;
    }

    public void scheduleRequestService() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);

        Log.d(TAG, "notify every day at " + SimpleDate.from(calendar));

        getAlarmManager()
                .setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY,
                        AlarmManager.INTERVAL_DAY, getReceiver());
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void cancelRequestService() {
        getAlarmManager().cancel(getReceiver());
    }

    private PendingIntent getReceiver() {
        Intent intent = new Intent();
        intent.setClass(appContext, AlarmReceiver.class);

        return PendingIntent.getBroadcast(appContext, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
