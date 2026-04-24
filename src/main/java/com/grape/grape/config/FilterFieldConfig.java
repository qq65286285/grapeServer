package com.grape.grape.config;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

import com.grape.grape.model.prompt.ai.ReferenceType;

/**
 * 过滤字段配置类
 * 用于配置字段匹配的相关参数
 */
@Data
public class FilterFieldConfig {
    /**
     * 字段名称
     */
    private String name;
    
    /**
     * 字段权重
     */
    private Double weight;
    
    /**
     * 向量键前缀
     */
    private String vectorKeyPrefix;
    
    /**
     * 测试用例字段路径
     * 支持嵌套路径，如 "priority" 或 "product.name"
     */
    private String testCaseField;
    
    /**
     * 分析结果字段路径
     * 支持嵌套路径，如 "test.priorities" 或 "product.platforms"
     */
    private String analysisField;
    
    /**
     * 阈值
     * 用于判断相似度匹配是否通过的临界值
     * 当计算的相似度分数低于此值时，认为匹配失败
     * 例如：threshold = 0.6 表示相似度分数必须达到或超过0.6才被认为是匹配的
     */
    private Double threshold;
    
    /**
     * 扩展属性
     * 用于存储额外的配置信息，如标签匹配模式等
     */
    private Map<String, Object> extensions;
    
    /**
     * 创建默认配置
     * @param name 字段名称
     * @return 默认配置实例
     */
    public static FilterFieldConfig createDefault(String name) {
        FilterFieldConfig config = new FilterFieldConfig();
        config.setName(name);
        config.setWeight(0.2); // 默认权重为20%
        config.setVectorKeyPrefix(name + ":");
        return config;
    }
    
    /**
     * 创建默认的配置映射
     * 为每个 ReferenceType 枚举值创建默认配置
     * @return 默认的配置映射
     */
    public static Map<String, FilterFieldConfig> createDefaultConfigMap() {
        Map<String, FilterFieldConfig> configMap = new HashMap<>();
        
        // 遍历所有 ReferenceType 枚举值
        for (ReferenceType type : ReferenceType.values()) {
            String name = type.getValue();
            FilterFieldConfig config = FilterFieldConfig.createDefault(name);
            configMap.put(name, config);
        }
        
        return configMap;
    }
    
    /**
     * 获取扩展属性
     * @return 扩展属性映射
     */
    public Map<String, Object> getExtensions() {
        if (extensions == null) {
            extensions = new HashMap<>();
        }
        return extensions;
    }
    
    /**
     * 设置扩展属性
     * @param extensions 扩展属性映射
     */
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    /**
     * 获取扩展属性值
     * @param key 属性键
     * @return 属性值
     */
    public Object getExtension(String key) {
        if (extensions == null) {
            return null;
        }
        return extensions.get(key);
    }
    
    /**
     * 设置扩展属性值
     * @param key 属性键
     * @param value 属性值
     */
    public void setExtension(String key, Object value) {
        if (extensions == null) {
            extensions = new HashMap<>();
        }
        extensions.put(key, value);
    }
}