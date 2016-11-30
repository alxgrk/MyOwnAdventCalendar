/*
 * Created on 27.12.2015
 *
 * author Alex
 */
package com.alxgrk.myownadventcalendar.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.alxgrk.myownadventcalendar.date.DateUtils;

public class NotificationIntentService extends IntentService {

    private Context appContext;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int daysUntilChristmas = new DateUtils().daysUntilChristmas();
        DaysNotification.notify(appContext, daysUntilChristmas);
    }
}
