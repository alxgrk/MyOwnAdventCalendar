package com.alxgrk.myownadventcalendar.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alxgrk.myownadventcalendar.R;
import com.alxgrk.myownadventcalendar.date.DateUtils;
import com.alxgrk.myownadventcalendar.util.MessageKeeper;
import com.alxgrk.myownadventcalendar.util.Preferences;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class DoorView extends RelativeLayout {

    private static final String TAG = DoorView.class.getSimpleName();

    public static void prepare(MessageKeeper messageKeeper) {
        for (DoorView door : doors) {
            String message = messageKeeper.getMessage(door.getNumber());
            door.fitMessage(message);

            if(door.isOpen) {
                // Log.d(TAG, "door " + door.getNumber() + " is opened");
                door.openDoor();
            }
        }
    }

    public static TreeSet<DoorView> getDoorsToOpen() {
        Comparator<DoorView> doorViewComparator = new Comparator<DoorView>() {
            @Override
            public int compare(DoorView door1, DoorView door2) {
                return Integer.compare(door1.getNumber(), door2.getNumber());
            }
        };
        TreeSet<DoorView> doorsToOpen = new TreeSet<>(doorViewComparator);
        int doorNumberToday = 24 - new DateUtils().daysUntilChristmas();

        for (DoorView door : doors) {
            if(door.getNumber() <= doorNumberToday && !door.isOpen()) {
                doorsToOpen.add(door);
            }
        }

        return doorsToOpen;
    }

    private static Set<DoorView> doors = new HashSet<>(24);

    private int number;
    private int textColor = Color.RED;
    private int animTime;

    private TextView tvNumber;
    private TextView tvMessage;

    private RelativeLayout doorFront;
    private boolean isOpen;

    private Preferences preferences;

    public DoorView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DoorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DoorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @SuppressLint("SetTextI18n")
    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.door_view, this);
        doors.add(this);

        // TODO put more loading into async task

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DoorView, defStyle, 0);

        number = Integer.parseInt(a.getString(R.styleable.DoorView_number));
        textColor = a.getColor(R.styleable.DoorView_color, textColor);
        animTime = context.getResources().getInteger(R.integer.anim_time_door);

        a.recycle();

        preferences = Preferences.getInstance(context);
        isOpen = preferences.isDoorOpened(getNumber());

        doorFront = (RelativeLayout) findViewById(R.id.front);

        tvNumber = (TextView) findViewById(R.id.tVNumber);
        tvNumber.setText(Integer.toString(number));
        tvNumber.setTextColor(textColor);

        tvMessage = (TextView) findViewById(R.id.tVMessage);
        if (number == 24)
            tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
    }

    private void fitMessage(String message) {
        tvMessage.setText(message); // avg 40 chars
        // TODO check if message exceeds parent and reduce size then
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getNumber() {
        return number;
    }

    public PointF getCenter() {
        int[] l = new int[2];
        this.getLocationOnScreen(l);
        return new PointF((float) (l[0] + getWidth() / 2), (float) (l[1] + getHeight() / 2));
    }

    public void openDoor() {
        doorFront.setVisibility(GONE);;
    }

    public void animateOpenDoor(@Nullable final View parent, final Runnable onFinished) {
        if (null != parent) {
            //parent.
        }

        doorFront.setPivotX(0f);
        doorFront.setPivotY(0.5f);

        doorFront.animate()
                .setDuration(animTime)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .rotationYBy(-90)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (null != parent)
                            parent.invalidate();
                    }
                })
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        DoorView.this.doorFront.setVisibility(GONE);
                        preferences.doorOpened(getNumber(), true);
                        onFinished.run();
                    }
                })
                .start();
    }
}
