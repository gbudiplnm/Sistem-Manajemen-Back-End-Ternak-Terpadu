/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.util;

import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.helper.HBaseCustomClient;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.TableName;

/**
 *
 * @author MSI MODERN 15 B13M
 */
public class AppUtility {

    public static List<String> getAttributeNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName).collect(Collectors.toList());
    }

    public static String safeString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "";
    }

    private static boolean isValidGetter(Method method) {
        return method.getName().startsWith("get")
                && method.getParameterCount() == 0
                && !method.getName().equals("getClass")
                && Modifier.isPublic(method.getModifiers());
    }

    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Boolean.class
                || clazz == Short.class
                || clazz == Byte.class
                || clazz == Character.class;
    }

    private static String getFieldNameFromGetter(String getterName) {
        return Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);
    }

    public static void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    public static void insertNonNullFields(Object obj, TableName tableName, String rowKey, HBaseCustomClient client) {
        Method[] methods = obj.getClass().getMethods();

        for (Method method : methods) {
            String methodName = method.getName();

            if (isValidGetter(method)) {
                try {
                    Object value = method.invoke(obj);
                    if (value != null) {
                        String fieldName = getFieldNameFromGetter(methodName);

                        if (isSimpleType(value.getClass())) {
                            // Direct simple field, insert
                            client.insertRecord(tableName, rowKey, "main", fieldName, safeString(value.toString()));

                        } else {
                            // It's an object — go one level deeper
                            Method[] subMethods = value.getClass().getMethods();
                            for (Method subMethod : subMethods) {
                                if (isValidGetter(subMethod)) {
                                    Object subValue = subMethod.invoke(value);
                                    if (subValue != null && isSimpleType(subValue.getClass())) {
                                        String subFieldName = fieldName + "_" + getFieldNameFromGetter(subMethod.getName());
                                        client.insertRecord(tableName, rowKey, "main", subFieldName, safeString(subValue.toString()));
                                    }
                                }
                            }
                        }
                    }

                } catch (IllegalAccessException | SecurityException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
