package com.alxgrk.myownadventcalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alxgrk.myownadventcalendar.animation.ZoomToDoorAnimation;
import com.alxgrk.myownadventcalendar.notification.NotificationScheduler;
import com.alxgrk.myownadventcalendar.util.MessageKeeper;
import com.alxgrk.myownadventcalendar.views.DoorView;
import com.alxgrk.myownadventcalendar.views.WelcomeDialog;
import com.alxgrk.myownadventcalendar.views.ZoomableViewGroup;

import java.util.TreeSet;

public class MainActivity extends Activity implements WelcomeDialog.NoticeDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ZoomableViewGroup zoomView;
    private ImageView ivBackground;

    private FragmentManager manager;
    private FrameLayout startFragment;
    private WelcomeDialog welcomeDialog;

    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;

    private MessageKeeper messageKeeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFragment = (FrameLayout) findViewById(R.id.start_fragment);
        startFragment.setBackgroundResource(R.drawable.title_screen);

        // TODO on first execution ask messages from user

        LoadingTask loadingTask = new LoadingTask(this);
        loadingTask.execute();
    }

    private void prepareMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.jingle_bells);
        mediaPlayer.setLooping(true);
    }

    private void hideFragment() {
        int animTime = getResources().getInteger(android.R.integer.config_longAnimTime);
        startFragment.animate()
                .setDuration(animTime)
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startFragment.setVisibility(View.GONE);

                        displayWelcomeDialog();
                    }
                });
    }

    private void displayWelcomeDialog() {
        welcomeDialog.show(manager, WelcomeDialog.class.getSimpleName());
    }

    @Override
    public void onDialogDismissed(DialogFragment dialog) {
        openDoor();
    }

    private void openDoor() {
        TreeSet<DoorView> doorsToOpen = DoorView.getDoorsToOpen();
        if (!doorsToOpen.isEmpty()) {
            ZoomToDoorAnimation animation = new ZoomToDoorAnimation(zoomView);
            Log.d(TAG, "doors to be opened: " + doorsToOpen);
            animation.start(doorsToOpen);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(null != mediaPlayer) {
            prepareMusic();
            mediaPlayer.seekTo(playbackPosition);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(null != mediaPlayer)
            mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(null != mediaPlayer)
            mediaPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(null != mediaPlayer) {
            playbackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    private class LoadingTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        LoadingTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prepareMusic();
            mediaPlayer.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            messageKeeper = new MessageKeeper(context);

            zoomView = (ZoomableViewGroup) findViewById(R.id.zoomable_view);

            ivBackground = (ImageView) findViewById(R.id.background);
            final Drawable background;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background = context.getResources().getDrawable(R.drawable.background_new, null);
            } else {
                background = context.getResources().getDrawable(R.drawable.background_new);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivBackground.setImageDrawable(background);
                    DoorView.prepare(messageKeeper);
                }
            });

            new NotificationScheduler(context).scheduleRequestService();

            manager = MainActivity.this.getFragmentManager();
            welcomeDialog = new WelcomeDialog();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideFragment();
        }
    }
}
