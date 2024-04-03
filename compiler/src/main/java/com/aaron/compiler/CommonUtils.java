package com.aaron.compiler;

import java.util.Collection;

import javax.tools.Diagnostic;

/**
 * @author dbj
 * @date 1/25/24
 * @description
 */
public class CommonUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

}
