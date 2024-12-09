package com.aaron.hookpractice.utils;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class ListUtils {
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static int getCount(List<?> list) {
        return !isEmpty(list) ? list.size() : 0;
    }

    public static @Nullable <T> T getElement(List<T> list, int index) {
        if (isEmpty(list)) {
            return null;
        }

        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }

        return null;
    }
}
