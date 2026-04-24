package com.grape.grape.utils.ai;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.TagMatchMode;

/**
 * 标签工具类
 * <p>
 * 提供标签相关的工具方法
 */
public final class TagUtils {

    private TagUtils() {
    }

    /**
     * 从配置中获取标签匹配模式
     *
     * @param config 字段配置
     * @return 标签匹配模式，默认 EXACT
     */
    public static TagMatchMode getTagMatchMode(FilterFieldConfig config) {
        if (config == null) {
            return TagMatchMode.EXACT;
        }

        // 从配置的扩展属性中读取标签匹配模式
        Object tagMatchModeObj = config.getExtension("tagMatchMode");
        if (tagMatchModeObj != null) {
            try {
                return TagMatchMode.valueOf(tagMatchModeObj.toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                // 如果配置的模式无效，返回默认值
                return TagMatchMode.EXACT;
            }
        }

        // 默认返回 EXACT 模式
        return TagMatchMode.EXACT;
    }
}
