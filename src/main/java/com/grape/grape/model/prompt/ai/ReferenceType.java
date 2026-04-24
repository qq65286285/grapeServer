package com.grape.grape.model.prompt.ai;

/**
 * 参考文本类型枚举
 */
public enum ReferenceType {
    /**
     * 产品信息
     */
    PRODUCT("product", "产品信息"),
    
    /**
     * 功能信息
     */
    FUNCTION("function", "功能信息"),
    
    /**
     * 测试信息
     */
    TEST("test", "测试信息"),
    
    /**
     * 业务实体
     */
    ENTITIES("entities", "业务实体"),
    
    /**
     * 技术信息
     */
    TECHNICAL("technical", "技术信息"),
    
    /**
     * 关键词
     */
    KEYWORDS("keywords", "关键词"),
    
    /**
     * 场景信息
     */
    SCENARIOS("scenarios", "场景信息"),
    
    /**
     * 元数据
     */
    METADATA("metadata", "元数据");
    
    private final String value;
    private final String description;
    
    ReferenceType(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据字符串值获取枚举实例
     * @param value 字符串值
     * @return 枚举实例
     */
    public static ReferenceType fromValue(String value) {
        for (ReferenceType type : ReferenceType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown reference type: " + value);
    }
}