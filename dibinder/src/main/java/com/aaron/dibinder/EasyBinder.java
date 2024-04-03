package com.aaron.dibinder;


import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class EasyBinder {
    private static final String TAG = "EasyBinderLog";
    private final static Map<Class<?>, Constructor<? extends ViewBinder>> BINDS = new LinkedHashMap<>();
    private static final String SUFFIX = "Blade";

    public static void bind(Object target) {
        if (target == null) {
            return;
        }

        Log.d(TAG, "target = " + target.toString());
        Constructor<? extends ViewBinder> constructor = BINDS.get(target.getClass());
        try {
            if (constructor != null) {
                Log.d(TAG, "constructor = " + constructor.getName());
                constructor.newInstance(target);
                return;
            }

            Log.d(TAG, "bind: " + target.getClass().getName() + SUFFIX);

            String className = target.getClass().getName() + SUFFIX;
            Class<?> binderClass = target.getClass().getClassLoader().loadClass(className);
            constructor = (Constructor<? extends ViewBinder>) binderClass.getConstructor(target.getClass());
            constructor.newInstance(target);

            if (!BINDS.containsKey(target.getClass())) {
                BINDS.put(target.getClass(), constructor);
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
