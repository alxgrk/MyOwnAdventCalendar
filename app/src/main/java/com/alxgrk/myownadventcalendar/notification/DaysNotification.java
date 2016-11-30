package com.alxgrk.myownadventcalendar.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.alxgrk.myownadventcalendar.MainActivity;
import com.alxgrk.myownadventcalendar.R;

/**
 * Helper class for showing and canceling days
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class DaysNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Days";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context, final int daysUntilChristmas) {
        final Resources res = context.getResources();

        final String title = res.getString(R.string.days_notification_title);
        final String text;

        if (daysUntilChristmas > 23) {
            text = res.getString(R.string.days_notification_placeholder_text,
                    daysUntilChristmas);
        } else {
            text = res.getString(R.string.days_notification_placeholder_text_serious,
                    24 - daysUntilChristmas);
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_stat_days)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
