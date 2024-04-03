package com.aaron.hookpractice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aaron.dibinder.EasyBinder;
import com.aaron.dibinder.ServiceLoader;
import com.aaron.diview.DIView;
import com.aaron.hookpractice.animate.MyTypeEvaluator;
import com.aaron.hookpractice.spi.ImageService;
import com.aaron.hookpractice.utils.Utils;
import com.example.ffmpegkit.IRemoteService;

import java.lang.reflect.Method;

public class MainActivity extends BaseActivity {
    private static final String TAG = "【MainActivity】";
    @DIView(R.id.fab)
    Button fab;

    @DIView(R.id.iv_img_start)
    ImageView imgStart;

    Activity activity;

    private final String[] finalStr = {"a", "b"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        EasyBinder.bind(this);
        activity = this;
        Log.d(TAG, "系统版本：" + Build.VERSION.RELEASE);
        Log.d(TAG, "onCreate: 修改前 final string = " + finalStr[0] + ", " + finalStr[1]);
        finalStr[1] = "修改";
        Log.d(TAG, "onCreate: 修改前 final string = " + finalStr[0] + ", " + finalStr[1]);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                Pair<View, String> imagePair = new Pair<>(imgStart, ViewCompat.getTransitionName(imgStart));
//                ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imagePair);
//                startActivity(intent, transitionActivityOptions.toBundle());
////                showFloatView();
//                Utils.toast(activity, "Hook Toast");
                bindService();
                animate();
            }
        });

//        Proxy.newProxyInstance()
        Log.d(TAG, "onCreate: ");
//        toastSystemFontSize();
        Toast.makeText(this, isHarmonyOS() ? "是鸿蒙系统" : "不是鸿蒙系统", Toast.LENGTH_SHORT).show();

//        initSensor();
    }

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRemoteService remoteService = IRemoteService.Stub.asInterface(service);
            try {
                int remoteValue = remoteService.getValue();
                Toast.makeText(MainActivity.this, "remote service value: " + remoteValue, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onServiceConnected: remoteValue = " + remoteValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void bindService() {
        Log.d(TAG,"绑定服务...");
        Intent intent = new Intent("com.example.ffmpegkit.myremoteservice");
        intent.setPackage("com.example.ffmpegkit");
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    public void unBindService() {
        Log.d(TAG,"解除服务...");
        unbindService(connection);
    }

    private void initSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d(TAG, "onSensorChanged: " + event.toString());
                if (event != null) {
                    Log.d(TAG, "onSensorChanged: values[0] = " + event.values[0]);
                    Log.d(TAG, "onSensorChanged: values[1] = " + event.values[1]);
                    Log.d(TAG, "onSensorChanged: values[2] = " + event.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private View floatView;

    public void showFloatView() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams ll = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ll.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        floatView = LayoutInflater.from(this).inflate(R.layout.view_float_window, null);

        windowManager.addView(floatView, ll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void animate() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(fab.getWidth(), 200, 300, 500, 300, 200, fab.getWidth());
        valueAnimator.setDuration(3000);
        valueAnimator.setEvaluator(new MyTypeEvaluator());
        valueAnimator.addUpdateListener(animator -> {
            int curValue = (int) animator.getAnimatedValue();
            fab.getLayoutParams().width = curValue;

            fab.requestLayout();
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Utils.toast(getApplicationContext(), "动画结束");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Utils.toast(getApplicationContext(), "动画开始");
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });

        valueAnimator.start();
    }

    private void toastSystemFontSize() {
        float fontSize = getResources().getConfiguration().fontScale;
        Toast.makeText(this, "当前系统字体大小:" + fontSize, Toast.LENGTH_SHORT).show();

    }

    public boolean isHarmonyOS() {
        try {
            Class clazz = Class.forName("com.huawei.system.BuildEx");
            Method method = clazz.getMethod("getOsBrand");
            // 如果BuildEx为系统提供的，其classloader为BootClassLoader
            // 如果BuildEx为伪造的，其classloader一般为PathClassLoader
            ClassLoader classLoader = clazz.getClassLoader();

            if (classLoader != null && classLoader.getParent() == null) {
                String osName = String.valueOf(method.invoke(clazz));
                Log.d(TAG, "classLoader is " + classLoader.toString() + "; OS name = " + osName);
                return "harmony".equals(osName);
            }
        } catch (Throwable e) {
            Log.e(TAG, "the process of determining whether a system is Harmony OS failed ");
        }
        return false;
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
        unBindService();
    }
}