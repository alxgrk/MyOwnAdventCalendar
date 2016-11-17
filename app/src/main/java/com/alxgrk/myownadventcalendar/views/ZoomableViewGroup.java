package com.alxgrk.myownadventcalendar.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;

import com.alxgrk.myownadventcalendar.measuring.Bounds;
import com.alxgrk.myownadventcalendar.measuring.Calculations;
import com.alxgrk.myownadventcalendar.measuring.Proportion;

public class ZoomableViewGroup extends RelativeLayout {

    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 3f;
    
    private Matrix preMatrix = new Matrix();
    private Matrix matrix = new Matrix();
    private Matrix matrixInverse = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();

    private Proportion containerProportion;
    private Proportion initContainerProportion;
    
    private Bounds bounds;

    private int widthGroup;
    private int heightGroup;

    private float oldDist = 1f;

    private boolean initZoomApplied = false;

    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(ev);
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        matrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a) {
        matrixInverse.mapPoints(a);
        return a;
    }

    public ZoomableViewGroup(Context context) {
        super(context);
        init(context);
    }

    public ZoomableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableViewGroup(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthGroup = MeasureSpec.getSize(widthMeasureSpec);
        heightGroup = MeasureSpec.getSize(heightMeasureSpec);

        // Log.d("width", widthMeasureSpec + " translated to " + widthGroup + "");
        // Log.d("height", heightMeasureSpec + " translated to " + heightGroup + "");

        initContainerProportion = containerProportion = computeContainerProportion();
        bounds = new Bounds(this);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);

                if (i == 0 && !initZoomApplied && child.getWidth() > 0) {
                    Proportion firstChild = new Proportion(child.getWidth(), child.getHeight());
                    zoomToFit(firstChild, containerProportion);
                }
            }
        }
    }

    private void zoomToFit(Proportion firstChild, Proportion container) {;
        float initZoom;
        float translationX;
        float translationY;

        if (container.getProportion() < firstChild.getProportion()) {
            initZoom = container.getHeight() / firstChild.getHeight();
            translationX = -1 * (firstChild.getWidth() * initZoom - container.getWidth()) / 2;
            translationY = 0;
        } else {
            initZoom = container.getWidth() / firstChild.getWidth();
            translationX = 0;
            translationY = -1 * (firstChild.getHeight() * initZoom - container.getHeight()) / 2;
        }
        //Log.d("zoomToFit", "adjust height with initZoom: " + initZoom);

        matrix.postScale(initZoom, initZoom);
        matrix.postTranslate(translationX, translationY);
        preMatrix.set(matrix);
        matrix.invert(matrixInverse);

        initZoomApplied = true;
        invalidate();
    }

    private Proportion computeContainerProportion() {
        PointF scalePoint = Calculations.getMatrixScaleXY(matrix);
        Proportion tmpContainer = new Proportion(scalePoint.x * widthGroup, scalePoint.y * heightGroup);

        //Log.d("computeProportion", "tmpContainer " + tmpContainer.toString());
        //Log.d("computeProportion", "initContainer " + initContainerProportion);
        return tmpContainer.withinBoundsOf(initContainerProportion) ? tmpContainer : initContainerProportion;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        // Log.d("dispatchDraw", "preMatrix " + preMatrix);

        ensureMatrixWithinScaleBounds(matrix, preMatrix);
        bounds.ensureMatrixWithinTranslationBounds(matrix, preMatrix);

        // Log.d("dispatchDraw", "matrix " + matrix);
        canvas.setMatrix(matrix);

        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private void ensureMatrixWithinScaleBounds(Matrix matrixToTest, Matrix fallback) {
        PointF scalePoint = Calculations.getMatrixScaleXY(matrixToTest);
        if(!(scalePoint.x >= MIN_ZOOM && scalePoint.y >= MIN_ZOOM)) {
            fallback.setScale(MIN_ZOOM, MIN_ZOOM);
            fallback.setTranslate(0f, 0f);
            matrixToTest.set(fallback);
        }
        if(scalePoint.x > MAX_ZOOM || scalePoint.y > MAX_ZOOM) {
            Calculations.setMatrixScaleXY(matrixToTest, MAX_ZOOM, MAX_ZOOM);
            if(mode == ZOOM) {
                PointF fallbackTrans = Calculations.getMatrixTranslationXY(fallback);
                Calculations.setMatrixTranslationXY(matrixToTest, fallbackTrans);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // handle touch events here
        mOnTouchEventWorkingArray[0] = event.getX();
        mOnTouchEventWorkingArray[1] = event.getY();

        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        event.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = Calculations.spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    Calculations.midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    preMatrix.set(matrix);
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                    matrix.invert(matrixInverse);
                } else if (mode == ZOOM) {
                    Proportion zoomedContainer = computeContainerProportion();

                    if(zoomedContainer != initContainerProportion) {
                        float newDist = Calculations.spacing(event);
                        if (newDist > 10f) {
                            float scale = (newDist / oldDist);

                            preMatrix.set(matrix);
                            matrix.set(savedMatrix);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            matrix.invert(matrixInverse);

                            containerProportion = zoomedContainer;
                        }
                    }
                }
                break;
        }

        invalidate();
        return true;
    }
}
