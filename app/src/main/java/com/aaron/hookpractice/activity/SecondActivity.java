package com.aaron.hookpractice.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.SpannableStringBuilder;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.dibinder.EasyBinder;
import com.aaron.dibinder.IService;
import com.aaron.dibinder.ServiceLoader;
import com.aaron.diview.DIView;
import com.aaron.hookpractice.R;
import com.aaron.hookpractice.RotateTransition;
import com.aaron.hookpractice.dialog.MyBottomDialogFragment;
import com.aaron.hookpractice.dialog.MyBottomSheetDialog;
import com.aaron.hookpractice.service.MyService;
import com.aaron.hookpractice.spi.ImageService;
import com.aaron.hookpractice.view.AutoScrollTextView;
import com.aaron.hookpractice.view.CollapseView;
import com.aaron.hookpractice.view.TextFlipper;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends BaseActivity {
    private static final String TAG = "【SecondActivity】";
    @DIView(R.id.button)
    Button button;

    @DIView(R.id.textView)
    CollapseView textView;

    @DIView(R.id.iv_img_end)
    ImageView imgEnd;

    @DIView(R.id.btn_start_service)
    Button btnStartService;

    @DIView(R.id.btn_stop_service)
    Button btnStopService;

    @DIView(R.id.btn_bottom_sheet_dialog)
    Button btnBottomSheetDialog;

    @DIView(R.id.text_flipper)
    AutoScrollTextView textFlipper;

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
        startFlipper();
    }

    private void initView() {
        btnStartService.setOnClickListener(v -> executeService(true));
        btnStopService.setOnClickListener(v -> executeService(false));
        textView.setContent(new SpannableStringBuilder("文字测量上课好地方海口市束带结发会计师礼金格蛇口街道家里的事"));
        MyBottomDialogFragment bottomSheetDialogFragment = new MyBottomDialogFragment();

//        MyBottomSheetDialog bottomSheetDialog = new MyBottomSheetDialog(this, R.style.leak_canary_Theme_Transparent);
//        bottomSheetDialog.setContentView(R.layout.view_bottom_sheet_dialog);
        btnBottomSheetDialog.setOnClickListener(v -> bottomSheetDialogFragment.show(SecondActivity.this.getSupportFragmentManager(),"one") );
    }

    private List<String> contents = new ArrayList<>();

    private void startFlipper() {
//        contents.add("的手机号发给技术");
//        contents.add("束带结发寄给你漱口水登记卡放哪");
//        contents.add("山东高速");
//        contents.add("看上你放假时光山莨菪碱归纳");
//        textFlipper.removeAllViews();
//
//        for (String content : contents) {
//            TextView textView = new TextView(this);
//            textView.setTextSize(18);
//            textView.setTextColor(Color.MAGENTA);
//            textView.setText(content);
//            textFlipper.addView(textView);
//        }
//
//        textFlipper.startFlipping();
        textFlipper.setTexts(new String[]{"世界多极化施工方计划", "伤筋动骨还是个好哦搜啥搭嘎你把", "东莞啥都好说", "四大金刚两三个的"});
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
