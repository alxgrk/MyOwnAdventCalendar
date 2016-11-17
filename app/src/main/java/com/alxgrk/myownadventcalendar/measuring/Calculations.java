package com.alxgrk.myownadventcalendar.measuring;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Created by alex on 15.11.16.
 */

public class Calculations {

    /**
     * Determine the space between the first two fingers
     */
    public static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    public static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public static void invertPoint(PointF point) {
        point.x *= -1;
        point.y *= -1;
    }

    public static PointF getMatrixScaleXY(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
    }

    public static void setMatrixScaleXY(Matrix matrix, float x, float y) {
        float[] f = new float[9];
        matrix.getValues(f);
        f[Matrix.MSCALE_X] = x;
        f[Matrix.MSCALE_Y] = y;
        matrix.setValues(f);
    }

    public static PointF getMatrixTranslationXY(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y]);
    }

    public static PointF getMatrixTranslationDiff(Matrix m1, Matrix m2) {
        float[] v1 = new float[9];
        m1.getValues(v1);

        float[] v2 = new float[9];
        m2.getValues(v2);

        float dx = v2[Matrix.MTRANS_X] - v1[Matrix.MTRANS_X];
        float dy = v2[Matrix.MTRANS_Y] - v1[Matrix.MTRANS_Y];

        return new PointF(dx, dy);
    }

    public static void setMatrixTranslationXY(Matrix matrix, PointF xy) {
        float[] f = new float[9];
        matrix.getValues(f);
        f[Matrix.MTRANS_X] = xy.x;
        f[Matrix.MTRANS_Y] = xy.y;
        matrix.setValues(f);
    }
}
