package com.alxgrk.myownadventcalendar.measuring;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * Created by alex on 14.11.16.
 */

public class Bounds {

    private final RectF visiblePart;
    private final RectF bounds;
    
    private final float originalWidth;
    private final float originalHeight;

    public Bounds(View view) {
        int[] l = new int[2];
        view.getLocationOnScreen(l);

        visiblePart = new RectF(l[0], l[1], view.getWidth(), view.getHeight());
        bounds = new RectF(visiblePart);

        originalWidth = view.getWidth();
        originalHeight = view.getHeight();
    }

    /**
     * This method considers all possible condition breaches and tries to mend them.
     * @param matrix
     * @param preMatrix
     * @return
     */
    public void ensureMatrixWithinTranslationBounds(Matrix matrix, Matrix preMatrix) {
        assertNoTranslationsPositive(matrix);

        // Log.d("Bounds", "matrix " + matrix);

        adjustByScale(matrix);
        applyAdjustingDifference(matrix, preMatrix);

        performValidTranslations(matrix, preMatrix);
    }

    private void assertNoTranslationsPositive(Matrix matrix) {
        PointF xy = Calculations.getMatrixTranslationXY(matrix);
        PointF xyWithoutPositives = new PointF(xy.x, xy.y);

        if(xy.x > 0)
            xyWithoutPositives.x = -0f;
        if(xy.y > 0)
            xyWithoutPositives.y = -0f;

        Calculations.setMatrixTranslationXY(matrix, xyWithoutPositives);
    }

    private void adjustByScale(Matrix matrix) {
        PointF scaleXY = Calculations.getMatrixScaleXY(matrix);

        bounds.right = originalWidth * scaleXY.x;
        bounds.bottom = originalHeight * scaleXY.y;

        // Log.d("Bounds", "adjusted bounds: " + toString());
    }

    private void applyAdjustingDifference(Matrix matrix, Matrix preMatrix) {
        if(!bounds.contains(visiblePart)) {
            float dx = bounds.right - visiblePart.right;
            float dy = bounds.bottom - visiblePart.bottom;
            // Log.d("Bounds", "zooming out difference: " + dx + ", " + dy);

            PointF translationDiffAlreadyPlanned = Calculations.getMatrixTranslationDiff(matrix, preMatrix);
            // Log.d("Bounds", "matrix difference: " + translationDiffAlreadyPlanned.x + ", " + translationDiffAlreadyPlanned.y);

            float[] f = new float[9];
            matrix.getValues(f);

            // checks for each direction whether the planned translation by moving the view,
            // is enough to guarantee that visiblePart will not exceed bounds
            if (-1 == Math.signum(dx)) {
                float totalDiffX = dx - translationDiffAlreadyPlanned.x;
                if(totalDiffX < 0) {
                    Log.d("Bounds", "totalDiffX " + totalDiffX);
                    // minus 1 because right/bottom bounds of rect are not included
                    f[Matrix.MTRANS_X] -= totalDiffX - 1;
                }
            }
            if (-1 == Math.signum(dy)) {
                float totalDiffY = dy - translationDiffAlreadyPlanned.y;
                if(totalDiffY < 0) {
                    Log.d("Bounds", "totalDiffY " + totalDiffY);
                    // minus 1 because right/bottom bounds of rect are not included
                    f[Matrix.MTRANS_Y] -= totalDiffY - 1;
                }
            }

            matrix.setValues(f);
            // Log.d("Bounds", "adjusted zoom difference: " + matrix);
        }
    }

    /**
     * If every translation could be performed, it will return true.
     * If at least one translation failed and reset to a former state, it returns false.
     * @param matrix
     * @param preMatrix
     * @return
     */
    private boolean performValidTranslations(Matrix matrix, Matrix preMatrix) {
        PointF xy = Calculations.getMatrixTranslationXY(matrix);
        Calculations.invertPoint(xy);

        PointF xyPre = Calculations.getMatrixTranslationXY(preMatrix);

        if(isTranslationWithinBoundsOfX(xy.x) && isTranslationWithinBoundsOfY(xy.y)) {
            updateViewBounds(xy);
            return true;
        } else {
            PointF newTrans;

            if(isTranslationWithinBoundsOfX(xy.x)) {
                newTrans = new PointF(-xy.x, xyPre.y);
                Log.d("Bounds", "at least x translation fits");
            } else if(isTranslationWithinBoundsOfY(xy.y)) {
                newTrans = new PointF(xyPre.x, -xy.y);
                Log.d("Bounds", "at least y translation fits");
            } else {
                newTrans = new PointF(xyPre.x, xyPre.y);
                Log.d("Bounds", "neither x nor y translation fits");
            }

            Calculations.setMatrixTranslationXY(matrix, newTrans);
            updateViewBounds(newTrans);
            Log.d("Bounds", "matrix trans: " + newTrans);
            return false;
        }
    }

    private boolean isTranslationWithinBoundsOfX(float x) {
        boolean min = bounds.left <= x;
        boolean max = bounds.right >= x + originalWidth;
        // Log.d("Bounds", "new x(" + x + ") in bounds? " + min + max);
        return min && max;
    }

    private boolean isTranslationWithinBoundsOfY(float y) {
        boolean min = bounds.top <= y;
        boolean max = bounds.bottom >= y + originalHeight;
        // Log.d("Bounds", "new y(" + y + ") in bounds? " + min + max);
        return min && max;
    }

    private void updateViewBounds(PointF translationXY) {
        visiblePart.offsetTo(translationXY.x, translationXY.y);
    }

    @Override
    public String toString() {
        return "Bounds{" +
                "visiblePart=" + visiblePart +
                ", bounds=" + bounds +
                ", originalWidth=" + originalWidth +
                ", originalHeight=" + originalHeight +
                '}';
    }
}
