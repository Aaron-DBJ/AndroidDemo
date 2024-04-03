package com.aaron.hookpractice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author dbj
 * @date 2/21/24
 * @description
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("" + this.getClass().getSimpleName(), "onCreate: ");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("" + this.getClass().getSimpleName(), "onRestoreInstanceState: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("" + this.getClass().getSimpleName(), "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("" + this.getClass().getSimpleName(), "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("" + this.getClass().getSimpleName(), "onResume: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("" + this.getClass().getSimpleName(), "onNewIntent: ");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("" + this.getClass().getSimpleName(), "onSaveInstanceState2: ");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("" + this.getClass().getSimpleName(), "onSaveInstanceState: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("" + this.getClass().getSimpleName(), "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("" + this.getClass().getSimpleName(), "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("" + this.getClass().getSimpleName(), "onDestroy: ");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("" + this.getClass().getSimpleName(), "onLowMemory: ");
    }
}
