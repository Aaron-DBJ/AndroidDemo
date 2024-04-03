package com.aaron.dibinder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dbj
 * @date 1/25/24
 * @description
 */
public class ServiceLoader {
    private static volatile Map<String, Map<String, String>> servicesMap = new HashMap<>();

    public static <T> T load(Class<T> service, String key) {
        if (isEmpty(servicesMap)) {
            getServicesMap();
        }

        Map<String, String> map = servicesMap.get(service.getName());
        if (isEmpty(map)) {
            return null;
        }

        String implClassName = map.get(key);
        try {
            Class<T> implClass = (Class<T>) Class.forName(implClassName);
            return implClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 编译时填入字节码
     */
    private static void getServicesMap() {

    }

    private static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
