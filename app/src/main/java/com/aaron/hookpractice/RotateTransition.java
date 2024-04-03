package com.aaron.hookpractice;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.ViewGroup;

public class RotateTransition extends Transition {
    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put("sa", -180.0f);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put("ea", 0f);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(endValues.view, "rotation", 90.0f, 0f);
        animator.setDuration(200);

        return animator;
    }
}
