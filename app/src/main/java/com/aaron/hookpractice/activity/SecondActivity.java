package com.aaron.hookpractice.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.dibinder.EasyBinder;
import com.aaron.dibinder.IService;
import com.aaron.dibinder.ServiceLoader;
import com.aaron.diview.DIView;
import com.aaron.hookpractice.R;
import com.aaron.hookpractice.RotateTransition;
import com.aaron.hookpractice.service.MyService;
import com.aaron.hookpractice.spi.ImageService;

public class SecondActivity extends BaseActivity {
    private static final String TAG = "【SecondActivity】";
    @DIView(R.id.button)
    Button button;

    @DIView(R.id.textView)
    TextView textView;

    @DIView(R.id.iv_img_end)
    ImageView imgEnd;

    @DIView(R.id.btn_start_service)
    Button btnStartService;

    @DIView(R.id.btn_stop_service)
    Button btnStopService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Transition transition = new Transition() {
            @Override
            public void captureStartValues(TransitionValues transitionValues) {
                transitionValues.values.put("startAngle", -180.0f);
                Log.d(TAG, "captureStartValues: " + transitionValues.values);
            }

            @Override
            public void captureEndValues(TransitionValues transitionValues) {
                transitionValues.values.put("endAngle", 0.0f);
            }

            @Override
            public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
                float startAngle = (float) endValues.values.get("startAngle");
                float endAngle = (float) endValues.values.get("endAngle");
                Log.d(TAG, "createAnimator: " + endValues.view);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(endValues.view,"rotation", startAngle, endAngle);
                animator1.setInterpolator(new AccelerateDecelerateInterpolator());
//                animator1.start();
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(endValues.view, "alpha",0.0f, 1.0f);
                animator2.setInterpolator(new AccelerateDecelerateInterpolator());


                AnimatorSet set = new AnimatorSet();
                set.setDuration(6000);
                set.playTogether(animator1, animator2);
                return set;
            }
        };
//        getWindow().setSharedElementEnterTransition(transition);
//        getWindow().setSharedElementExitTransition(transition);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        EasyBinder.bind(this);

        android.transition.TransitionSet transitionSet = new android.transition.TransitionSet();
        transitionSet.addTransition(new ChangeBounds());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new RotateTransition());
        transitionSet.addTarget(imgEnd);
//        getWindow().setSharedElementEnterTransition(transitionSet);
//        getWindow().setSharedElementExitTransition(transitionSet);
        button.setOnClickListener(v -> {
            textView.setText("APT技术");
//            Intent view = new Intent(SecondActivity.this, ThirdActivity.class);
//            startActivity(view);
        });
        Log.d(TAG, "onCreate: ");
        ImageService imageService = (ImageService) ServiceLoader.load(IService.class, ImageService.TAG);
        if (imageService != null) {
            imageService.image();
        }
        initView();
    }


    private void initView() {
        btnStartService.setOnClickListener(v -> executeService(true));
        btnStopService.setOnClickListener(v -> executeService(false));
    }

    private void executeService(boolean isStart) {
        Intent intent = new Intent(this, MyService.class);
        if (isStart) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

}
