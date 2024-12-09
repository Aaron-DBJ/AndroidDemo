package com.aaron.hookpractice.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * author: dubojun
 * date: 2024/10/15
 * description:
 **/
public class ConsumeView extends View {
    private static final String TAG = "ConsumeView";
    public ConsumeView(Context context) {
        super(context);
    }

    public ConsumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConsumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.d(TAG, "dispatchTouchEvent: : event = " + event.toString());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent: event = " + event.toString());
        boolean consume = super.onTouchEvent(event);
//        Log.d(TAG, "onTouchEvent: consume = " + consume);
        return consume;
    }
}
