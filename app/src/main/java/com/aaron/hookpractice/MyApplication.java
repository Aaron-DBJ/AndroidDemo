package com.aaron.hookpractice;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aaron.hookpractice.view.DragLayout;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private Activity mActivity;

    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated: " + activity.getClass().getSimpleName());
            mActivity = activity;
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            Log.d(TAG, "onActivityStarted: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            showFloatWindow();
            Log.d(TAG, "onActivityResumed: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            Log.d(TAG, "onActivityPaused: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            Log.d(TAG, "onActivityStopped: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            Log.d(TAG, "onActivitySaveInstanceState: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            Log.d(TAG, "onActivityDestroyed: " + activity.getClass().getSimpleName());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    private void showFloatWindow(){
        if (mActivity == null || mActivity.getWindow() == null) {
            return;
        }

        Window window = mActivity.getWindow();
        FrameLayout rootLayout = (FrameLayout) window.getDecorView();
        DragLayout floatView = (DragLayout) LayoutInflater.from(mActivity).inflate(R.layout.view_float_window, null);
        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "点击浮窗", Toast.LENGTH_SHORT).show();
            }
        });
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.RIGHT;

        fl.topMargin = 200;
        fl.rightMargin = 100;
        floatView.setLayoutParams(fl);

        rootLayout.addView(floatView);
    }
}
