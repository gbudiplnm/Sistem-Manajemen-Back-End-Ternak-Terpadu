/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ternak.sapi.util;

import com.ternak.sapi.exception.BadRequestException;
import com.ternak.sapi.helper.HBaseCustomClient;

import java.lang.reflect.Array;
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
        return Modifier.isPublic(method.getModifiers())
            && method.getParameterCount() == 0
            && method.getName().startsWith("get")
            && !method.getName().equals("getClass");
    }

    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
            || clazz == String.class
            || Number.class.isAssignableFrom(clazz)
            || clazz == Boolean.class
            || clazz == Character.class
            || clazz.isEnum(); // Enum support
    }

    private static String getFieldNameFromGetter(String getterName) {
        String withoutGet = getterName.substring(3); // remove "get"
        return Character.toLowerCase(withoutGet.charAt(0)) + withoutGet.substring(1);
    }

    public static void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    private static String getSimpleValue(Object value) {
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        }
        return value.toString();
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
    
                        if (value.getClass().isArray()) {
                            int length = Array.getLength(value);
                            for (int i = 0; i < length; i++) {
                                Object element = Array.get(value, i);
                                if (element != null && isSimpleType(element.getClass())) {
                                    client.insertRecord(tableName, rowKey, "detail", fieldName + "_" + i, safeString(getSimpleValue(element)));
                                }
                            }
    
                        }
                        else if (isSimpleType(value.getClass())) {
                            // Direct simple field, including enums
                            String stringValue = safeString(value instanceof Enum ? ((Enum<?>) value).name() : value.toString());
                            client.insertRecord(tableName, rowKey, "detail", fieldName, stringValue);
    
                        } else {
                            // It's an object — go one level deeper
                            Method[] subMethods = value.getClass().getMethods();
                            for (Method subMethod : subMethods) {
                                if (isValidGetter(subMethod)) {
                                    Object subValue = subMethod.invoke(value);
                                    if (subValue != null && isSimpleType(subValue.getClass())) {
                                        String subFieldName = getFieldNameFromGetter(subMethod.getName());
                                        String stringSubValue = safeString(subValue instanceof Enum ? ((Enum<?>) subValue).name() : subValue.toString());
                                        client.insertRecord(tableName, rowKey, fieldName, subFieldName, stringSubValue);
                                    }
                                }
                            }
                        }
                    }
    
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
