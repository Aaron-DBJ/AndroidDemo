package com.aaron.hookpractice.animate;

import android.animation.TypeEvaluator;
import android.util.Log;

public class MyTypeEvaluator implements TypeEvaluator {
    private static final String TAG = "MyTypeEvaluator";

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int start = (int) startValue;
        int end = (int) endValue;

        return start + fraction * (end - start);
    }
}
