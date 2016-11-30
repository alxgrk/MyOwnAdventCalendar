package com.alxgrk.myownadventcalendar.util;

import android.content.Context;
import android.support.annotation.IntRange;

import java.util.ArrayList;

/**
 * Created by alex on 23.11.16.
 */
public class MessageKeeper {

    private static final String TAG = MessageKeeper.class.getSimpleName();

    private final ArrayList<String> messages;
    private final Context context;

    public MessageKeeper(Context context) {
        this.context = context;
        messages = Preferences.getInstance(context).getUserMessages();
    }

    public String getMessage(@IntRange(from = 1, to = 24) int doorNumber) {
        if(doorNumber > 24 || doorNumber < 1)
            throw new IllegalArgumentException("Doors are only from 1 to 24!");

        return messages.get(doorNumber-1);
    }

    public void setMessage(@IntRange(from = 1, to = 24) int doorNumber, String message) {
        if (!getMessage(doorNumber).equals(message)) {
            Preferences.getInstance(context).saveUserMessage(doorNumber - 1, message);
        }
    }
}
