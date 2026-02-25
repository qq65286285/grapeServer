package com.grape.grape.config.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Supplier;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/9/5  14:43
 * @Version
 */
@Data
@AllArgsConstructor
public class StrictFill<T, E extends T> {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private Class<T> fieldType;
    /**
     * 获取字段值的函数
     */
    private Supplier<E> fieldVal;

    public static <T, E extends T> StrictFill<T, E> of(String fieldName, Class<T> fieldType, E fieldVal) {
        return new StrictFill<>(fieldName, fieldType, () -> fieldVal);
    }

    public static <T, E extends T> StrictFill<T, E> of(String fieldName, Supplier<E> fieldVal, Class<T> fieldType) {
        return new StrictFill<>(fieldName, fieldType, fieldVal);
    }
}
