package com.alxgrk.myownadventcalendar;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;

public class LauncherActivity extends Activity {

    private MediaPlayer mediaPlayer;

    private boolean keepPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        mediaPlayer = MediaPlayer.create(this, R.raw.jingle_bells);
        mediaPlayer.start();

        // FIXME REMOVE!!!
        CountDownTimer cdt = new CountDownTimer(5000, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                keepPlaying = true;
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        cdt.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mediaPlayer.release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mediaPlayer  = MediaPlayer.create(this, R.raw.jingle_bells);
        mediaPlayer.start();
    }
}
