package com.alxgrk.myownadventcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.alxgrk.myownadventcalendar.views.ZoomableViewGroup;

public class MainActivity extends Activity {

    private ImageView backgroundView;

    private ZoomableViewGroup zoomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zoomView = (ZoomableViewGroup) findViewById(R.id.zoomable_view);

        backgroundView = (ImageView) findViewById(R.id.background);
    }
}
