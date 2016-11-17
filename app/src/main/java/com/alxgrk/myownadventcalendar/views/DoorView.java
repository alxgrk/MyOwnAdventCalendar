package com.alxgrk.myownadventcalendar.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alxgrk.myownadventcalendar.R;

public class DoorView extends RelativeLayout {
    private String number;
    private int textColor = Color.RED;
    private float dimension = 0;

    private TextView tvNumber;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    /*public DoorView(Context context, int number) {
        super(context);

        this.number = Integer.toString(number);
        init(context, null, 0);
    }*/

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

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.door_view, this);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DoorView, defStyle, 0);

        number = a.getString( R.styleable.DoorView_number);
        textColor = a.getColor(R.styleable.DoorView_color, textColor);

        a.recycle();

        tvNumber = (TextView) findViewById(R.id.textView);
        tvNumber.setText(number);
        tvNumber.setTextColor(textColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*// TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(number,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }
}
