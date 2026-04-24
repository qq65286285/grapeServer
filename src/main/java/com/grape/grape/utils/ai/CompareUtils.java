package com.grape.grape.utils.ai;

import java.util.List;

/**
 * 比较工具类
 * 提供值比较的静态方法
 */
public class CompareUtils {
    
    private CompareUtils() {
    }
    
    /**
     * 值比较方法，支持多种数据类型
     * <p>
     * 根据不同的数据类型执行相应的比较逻辑：
     * 1. 对于List类型的期望值，检查实际值是否在列表中
     * 2. 对于String类型，执行忽略大小写的比较
     * 3. 对于Number类型，使用equals方法比较
     * 4. 对于Enum类型，使用equals方法比较
     * 5. 对于其他类型，使用equals方法比较
     * 
     * @param actual 实际值
     * @param expected 期望值
     * @return 是否匹配
     */
    public static boolean compareValues(Object actual, Object expected) {
        // 处理List类型的期望值：检查实际值是否在列表中
        if (expected instanceof List) {
            List<?> expectedList = (List<?>) expected;
            return expectedList.contains(actual);
        }

        // 处理String类型：忽略大小写，去除首尾空格后比较
        if (actual instanceof String && expected instanceof String) {
            String actualStr = (String) actual;
            String expectedStr = (String) expected;
            return actualStr.trim().equalsIgnoreCase(expectedStr.trim());
        }

        // 处理Number类型：直接使用equals方法比较
        if (actual instanceof Number && expected instanceof Number) {
            return actual.equals(expected);
        }

        // 处理Enum类型：直接使用equals方法比较
        if (actual instanceof Enum && expected instanceof Enum) {
            return actual.equals(expected);
        }

        // 其他类型：使用equals方法比较
        return actual.equals(expected);
    }
}