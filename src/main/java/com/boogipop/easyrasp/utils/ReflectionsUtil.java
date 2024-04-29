package com.boogipop.easyrasp.utils;

import java.lang.reflect.Field;

public class ReflectionsUtil {
    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static String getSource(final Class<?> clazz, final String fieldName) throws IllegalAccessException {
        Field field = getField(clazz, fieldName);
        return (String) field.get(clazz);
    }
}
