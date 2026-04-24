package com.grape.grape.utils.ai;

import java.util.Map;

public class MapUtils {
    
    private MapUtils() {
    }
    
    /**
     * 根据value查找map的key
     * @param map 输入的map
     * @param value 要查找的value
     * @return 对应的key，如果找不到返回null
     */
    public static String findKeyByValue(Map<String, String> map, String value) {
        if (map == null || value == null) {
            return null;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}