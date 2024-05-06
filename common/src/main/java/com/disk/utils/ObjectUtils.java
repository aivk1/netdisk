package com.disk.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ObjectUtils {
    public static <T> T copyProperties(T source, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T target = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(target, value);
            }
        }
        return target;
    }
}
