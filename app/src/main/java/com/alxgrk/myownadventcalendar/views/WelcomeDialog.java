package com.alxgrk.myownadventcalendar.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.alxgrk.myownadventcalendar.MainActivity;
import com.alxgrk.myownadventcalendar.R;
import com.alxgrk.myownadventcalendar.date.DateUtils;

/**
 * Created by alex on 24.11.16.
 */

public class WelcomeDialog extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogDismissed(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int textRes;
        int placeholderValue;

        int daysUntilChristmas = new DateUtils().daysUntilChristmas();
        if (daysUntilChristmas > 23) {
            textRes = R.string.welcome_dialog_message;
            placeholderValue = daysUntilChristmas - 23;
        } else {
            textRes = R.string.days_notification_placeholder_text_serious;
            placeholderValue = 24 - daysUntilChristmas;
        }

        String message = getString(textRes, placeholderValue);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.welcome_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (null != mListener) {
                            mListener.onDialogDismissed(WelcomeDialog.this);
                        } else {
                            ((MainActivity) getActivity()).onDialogDismissed(WelcomeDialog.this);
                        }

                        dismiss();
                    }
                });

        return builder.create();
    }


}
