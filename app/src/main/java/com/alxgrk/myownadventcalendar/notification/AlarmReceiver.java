/*
 * Created on 22.01.2016
 *
 * author Alex
 */
package com.alxgrk.myownadventcalendar.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean startedBecauseOfReboot = "android.intent.action.BOOT_COMPLETED".equals(intent.getAction());
        if (startedBecauseOfReboot) {
            new NotificationScheduler(context).scheduleRequestService();
        } else {
            context.startService(
                    new Intent().setClass(context, NotificationIntentService.class));
        }
    }

}
