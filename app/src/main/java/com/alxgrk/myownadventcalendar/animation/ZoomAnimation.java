package com.alxgrk.myownadventcalendar.animation;

import android.content.Context;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

/**
 * Created by alex on 22.11.16.
 */

public class ZoomAnimation extends ScaleAnimation {

    private Matrix transformationMatrix;

    public ZoomAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomAnimation(float fromX, float toX, float fromY, float toY) {
        super(fromX, toX, fromY, toY);
    }

    public ZoomAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
        super(fromX, toX, fromY, toY, pivotX, pivotY);
    }

    public ZoomAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        transformationMatrix = t.getMatrix();
    }

    public @Nullable Matrix getTranslationMatrix() {
        return transformationMatrix;
    }
}
