package com.grape.grape.enums;

import lombok.Getter;

/**
 * 测试用例状态枚举
 * 0-未执行，1-已完成，2-已失败
 */
@Getter
public enum CaseStatusEnum {

    /**
     * 未执行
     */
    NOT_EXECUTED(0, "未执行"),
    
    /**
     * 已完成
     */
    COMPLETED(1, "已完成"),
    
    /**
     * 已失败
     */
    FAILED(2, "已失败");
    
    private final int code;
    private final String message;
    
    CaseStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    /**
     * 根据代码获取枚举值
     */
    public static CaseStatusEnum getByCode(int code) {
        for (CaseStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的测试用例状态码: " + code);
    }
    
    /**
     * 根据消息获取枚举值
     */
    public static CaseStatusEnum getByMessage(String message) {
        for (CaseStatusEnum status : values()) {
            if (status.message.equals(message)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的测试用例状态消息: " + message);
    }
}
