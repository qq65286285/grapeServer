package com.grape.grape.model.enums;

/**
 * 测试用例生成任务状态枚举
 */
public enum TaskStatus {

    PENDING(0, "待处理"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败");

    private final Integer code;
    private final String description;

    TaskStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TaskStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TaskStatus status : TaskStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}