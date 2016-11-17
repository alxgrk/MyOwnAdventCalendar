package com.alxgrk.myownadventcalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alxgrk.myownadventcalendar.views.ZoomableViewGroup;

public class MainActivity extends Activity {

    private ImageView backgroundView;

    private ZoomableViewGroup zoomView;

    private FrameLayout startFragment;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zoomView = (ZoomableViewGroup) findViewById(R.id.zoomable_view);

        backgroundView = (ImageView) findViewById(R.id.background);

        mediaPlayer = MediaPlayer.create(this, R.raw.jingle_bells);
        mediaPlayer.start();

        startFragment = (FrameLayout) findViewById(R.id.start_fragment);
        startFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFragment();
            }
        });
    }

    void hideFragment() {
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
                    }
                });
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
