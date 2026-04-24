package com.grape.grape.utils.ai;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * 提供反射相关的工具方法
 */
public class ReflectionUtils {
    
    private ReflectionUtils() {
    }
    
    /**
     * 通过反射获取对象字段值，支持嵌套路径
     * <p>
     * 支持两种获取方式：
     * 1. 优先尝试通过getter方法获取（如getFieldName()）
     * 2. 如果getter方法不存在，则尝试直接访问字段（包括私有字段）
     * <p>
     * 支持嵌套路径访问，如 "user.address.city"
     * 
     * @param obj 目标对象
     * @param fieldPath 字段路径，支持嵌套路径
     * @return 字段值，如果获取失败返回null
     */
    public static Object getFieldValue(Object obj, String fieldPath) {
        if (obj == null || fieldPath == null || fieldPath.isEmpty()) {
            return null;
        }

        // 分割嵌套路径
        String[] pathParts = fieldPath.split("\\.");
        Object current = obj;

        // 遍历路径部分，逐层获取值
        for (String part : pathParts) {
            if (current == null) {
                return null;
            }

            // 尝试通过getter方法获取值
            try {
                String getterName = "get" + part.substring(0, 1).toUpperCase() + part.substring(1);
                Method method = current.getClass().getMethod(getterName);
                current = method.invoke(current);
            } catch (Exception e) {
                // 尝试通过字段直接获取
                try {
                    Field field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true); // 设置为可访问，支持私有字段
                    current = field.get(current);
                } catch (Exception ex) {
                    return null;
                }
            }
        }

        return current;
    }
    
    /**
     * 通过反射获取对象字段值，支持嵌套路径，带泛型
     * <p>
     * 支持两种获取方式：
     * 1. 优先尝试通过getter方法获取（如getFieldName()）
     * 2. 如果getter方法不存在，则尝试直接访问字段（包括私有字段）
     * <p>
     * 支持嵌套路径访问，如 "user.address.city"
     * 
     * @param obj 目标对象
     * @param fieldPath 字段路径，支持嵌套路径
     * @param <T> 返回类型
     * @return 字段值，如果获取失败返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueGeneric(Object obj, String fieldPath) {
        Object value = getFieldValue(obj, fieldPath);
        return (T) value;
    }
}