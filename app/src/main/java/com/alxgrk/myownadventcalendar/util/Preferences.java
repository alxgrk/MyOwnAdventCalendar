package com.alxgrk.myownadventcalendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntRange;

import com.alxgrk.myownadventcalendar.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alex on 22.11.16.
 */
public class Preferences {

    private static final String TAG = "AdventCalendar";

    private static final String KEY_REGISTERED = "registered";
    private static final String KEY_OPENED = "opened";
    private static final String USER_MESSAGE = "message";

    private static Preferences ourInstance;

    private final SharedPreferences preferences;
    private final ArrayList<String> defaultMessages;

    public static Preferences getInstance(Context context) {
        if(null == ourInstance) {
            ourInstance = new Preferences(context);
        }
        return ourInstance;
    }

    private Preferences(Context context) {
        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        defaultMessages = getDefaultMessages(context);
    }

    private ArrayList<String> getDefaultMessages(Context context) {
        ArrayList<String> defaults = new ArrayList<>(24);

        for (int i = 23; i > 0; i--){
            String defaultMessage = context.getResources().getQuantityString(R.plurals.default_others, i, i);
            defaults.add(defaultMessage);
        }
        defaults.add(context.getResources().getString(R.string.default_christmas));
        // Log.d(TAG, "Default messages: " + defaults);

        return defaults;
    }

    ArrayList<String> getUserMessages() {
        ArrayList<String> messages = defaultMessages;

        int first = 0;
        if (preferences.contains(USER_MESSAGE + first)) {
            for (int i = 23; i >= first; i--) {
                String userMessage = preferences.getString(USER_MESSAGE + i, defaultMessages.get(i));
                messages.add(userMessage);
            }
        }
        // Log.d(TAG, "Messages: " + messages);

        return messages;
    }

    void saveUserMessage(@IntRange(from = 0, to = 23) int i, String userMessage) {
        preferences.edit()
                .putString(USER_MESSAGE + i, userMessage)
                .apply();
    }

    public boolean isDoorOpened(int number) {
        return preferences.getBoolean(KEY_OPENED + number, false);
    }

    public void doorOpened(int number, boolean opened) {
        preferences.edit().putBoolean(KEY_OPENED + number, opened).apply();
    }
}
