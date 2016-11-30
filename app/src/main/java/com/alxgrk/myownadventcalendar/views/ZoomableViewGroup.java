package com.alxgrk.myownadventcalendar.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.alxgrk.myownadventcalendar.R;
import com.alxgrk.myownadventcalendar.animation.ZoomAnimation;
import com.alxgrk.myownadventcalendar.measuring.Bounds;
import com.alxgrk.myownadventcalendar.measuring.Calculations;
import com.alxgrk.myownadventcalendar.measuring.Proportion;

public class ZoomableViewGroup extends RelativeLayout {

    private static final String TAG = ZoomableViewGroup.class.getSimpleName();
    private static final float MIN_ZOOM = 1f;
    private static final float MAX_ZOOM = 3f;
    private static final float ADJUSTING_FACTOR = 25f;
    public static final int ADJUSTING_EXPONENT = 4;

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
    private PointF adjustedZoomCenter = new PointF(0,0);

    private boolean initZoomApplied = false;
    private boolean blockUser = false;

    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];

    private int animTimeToDoor;
    private int animTimeToInit;

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
        animTimeToDoor = context.getResources().getInteger(R.integer.anim_time_zoom_to_door);
        animTimeToInit = context.getResources().getInteger(R.integer.anim_time_zoom_to_init);
    }

    public boolean isUserBlocked() {
        return blockUser;
    }

    public void blockUser() {
        this.blockUser = true;
    }

    public void unblockUser() {
        this.blockUser = false;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthGroup = MeasureSpec.getSize(widthMeasureSpec);
        heightGroup = MeasureSpec.getSize(heightMeasureSpec);

        // Log.d("width", widthMeasureSpec + " translated to " + widthGroup + "");
        // Log.d("height", heightMeasureSpec + " translated to " + heightGroup + "");

        containerProportion = computeContainerProportion();
        bounds = new Bounds(this);

        if(null == initContainerProportion) {
            initContainerProportion = containerProportion;
        }

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

        // Log.d("computeProportion", "tmpContainer " + tmpContainer);
        // Log.d("computeProportion", "initContainer " + initContainerProportion);
        return tmpContainer.withinBoundsOf(initContainerProportion) ? tmpContainer : initContainerProportion;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(blockUser) {
            super.dispatchDraw(canvas);
            return;
        }

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
            // Log.d(TAG, "zoom exceeds MIN_ZOOM, set to fallback " + scalePoint);
        }
        if(scalePoint.x > MAX_ZOOM || scalePoint.y > MAX_ZOOM) {
            Calculations.setMatrixScaleXY(matrixToTest, MAX_ZOOM, MAX_ZOOM);
            if(mode != DRAG) {
                PointF fallbackTrans = Calculations.getMatrixTranslationXY(fallback);
                Calculations.setMatrixTranslationXY(matrixToTest, fallbackTrans);
                // Log.d(TAG, "zoom exceeds MAX_ZOOM, set translation from fallback " + fallbackTrans);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(blockUser) {
            return super.onTouchEvent(event);
        }

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

    public void zoomToInit(ZoomEndListener onFinished) {
        PointF currentScale = Calculations.getMatrixScaleXY(matrix);

        ZoomAnimation zoomAnimation = new ZoomAnimation(
                currentScale.x, MIN_ZOOM,
                currentScale.y, MIN_ZOOM,
                adjustedZoomCenter.x, adjustedZoomCenter.y);
        zoomAnimation.setDuration(animTimeToInit);
        zoomAnimation.setFillAfter(true);
        zoomAnimation.setAnimationListener(onFinished);
        startAnimation(zoomAnimation);
    }

    public void zoomToDoor(final DoorView doorView, ZoomEndListener onFinished) {
        PointF center = doorView.getCenter();
        adjustToCenter(center);
        adjustedZoomCenter = center;
        Log.d(TAG, "zoomTo: " + center.x + "," + center.y);

        ZoomAnimation zoomAnimation = new ZoomAnimation(
                oldDist, MAX_ZOOM,
                oldDist, MAX_ZOOM,
                center.x, center.y);
        zoomAnimation.setDuration(animTimeToDoor);
        zoomAnimation.setFillAfter(true);
        zoomAnimation.setAnimationListener(onFinished);
        startAnimation(zoomAnimation);
    }

    private void adjustToCenter(PointF center) {
        // Log.d(TAG, "original zoom center: " + center.x + "," + center.y);

        int widthGroupCenter = widthGroup / 2;
        int heightGroupCenter = heightGroup / 2;

        float propX = center.x / widthGroupCenter - 1;
        if (propX != 0) {
            float baseX = Math.abs(propX) + 1;
            double dx = ADJUSTING_FACTOR * Math.pow(baseX, ADJUSTING_EXPONENT);
            if (propX < 0) {
                center.x -= dx;
            } else {
                center.x += dx;
            }
        }
        
        float propY = center.y / heightGroupCenter - 1;
        if (propY != 0) {
            float baseY = Math.abs(propY) + 1;
            double dy = ADJUSTING_FACTOR * Math.pow(baseY, ADJUSTING_EXPONENT);
            if (propY < 0) {
                center.y -= dy;
            } else {
                center.y += dy;
            }
        }

        // Log.d(TAG, "trying to adjust center to: " + center.x + "," + center.y);
        
        if(center.x > widthGroup)
            center.x = widthGroup;
        if(center.x < 0)
            center.x = 0;
        if(center.y > heightGroup)
            center.y = heightGroup;
        if(center.y < 0)
            center.y = 0;
    }

    public ZoomEndListener createZoomEndListener(Runnable task) {
        return new ZoomEndListener(task);
    }

    private class ZoomEndListener implements Animation.AnimationListener {

        private Runnable task;

        ZoomEndListener(Runnable task) {
            this.task = task;
        }

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            Matrix tMatrix = ((ZoomAnimation) animation).getTranslationMatrix();
            matrix.set(tMatrix);
            preMatrix.set(tMatrix);
            matrix.invert(matrixInverse);
            computeContainerProportion();

            invalidate();
            new Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                        if (null != task)
                            task.run();
                        }
                    }, 500
            );
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }
}
