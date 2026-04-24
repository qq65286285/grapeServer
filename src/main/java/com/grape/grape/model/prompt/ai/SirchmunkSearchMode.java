package com.grape.grape.model.prompt.ai;

/**
 * Sirchmunk 搜索模式枚举
 */
public enum SirchmunkSearchMode {
    DEEP("DEEP", "深度搜索模式"),
    FILENAME_ONLY("FILENAME_ONLY", "仅文件名搜索模式");

    private final String value;
    private final String description;

    SirchmunkSearchMode(String value, String description) {
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
     * @return 对应的枚举实例，如果未找到返回 null
     */
    public static SirchmunkSearchMode fromValue(String value) {
        for (SirchmunkSearchMode mode : values()) {
            if (mode.value.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
