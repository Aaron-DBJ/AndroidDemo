package com.aaron.hookpractice.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class MToast {
    public static void toast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
