package com.grape.grape.model.dict;


import cn.hutool.core.util.StrUtil;

/**
 * @Author:Gin.44.Candy
 * @Version 1.1
 */
public enum ResultEnumI18n {

    BODY_NOT_MATCH(400, "请求的数据格式不符!", "", ""),
    SIGNATURE_NOT_MATCH(401, "请求的数字签名不匹配!", "", ""),
    NOT_FOUND(404, "未找到该资源!", "リソースが見つかりません", "Resource not found"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误!", "", ""),
    SERVER_BUSY(503, "服务器正忙，请稍后再试!", "", ""),
    /**
     * 成功
     */
    SUCCESS(0, "成功", "", ""),
    /**
     * 无法找到资源错误
     */
    NOT_FOUND_RESOURCE(1001, "没有找到相关资源!", "", ""),
    /**
     * 请求参数有误
     */
    PARAMETER_ERROR(1002, "请求参数有误!", "", ""),
    /**
     * 确少必要请求参数异常
     */
    PARAMETER_MISSING_ERROR(1003, "确少必要请求参数!", "必要なパラメータが空です", "Required parameter is empty"),
    /**
     * 确少必要请求参数异常
     */
    REQUEST_MISSING_BODY_ERROR(1004, "缺少请求体!", "", ""),

    NULL_DATA(1005, "查询结果为空", "", ""),
    GET_FILE_DOWNLOAD_URL_FAIL(1006, "获取文件下载地址失败", "", ""),
    HASH_ERROR(1007, "文件HASH对不上", "", ""),
    NOT_FOUND_DATA(1009, "没有找到相关数据!", "関連データが見つかりません", "No related data found"),
    UNIQUE(10010, "资源重复", "", ""),
    ZABBIX_ERROR(1008, "zabbix返回错误", "", ""),
    LOGIN_ERROR(10011, "登录状态有误，请重新登录", "", ""),
    ROLE_ERROR(10012, "无权限操作", "操作権限がありません", "No permission to operate"),
    /**
     * 未知错误
     */
    SYSTEM_ERROR(9998, "未知的错误!", "", ""),
    BUSINESS_ERROR(20001, "业务处理错误", "", ""),
    CODE_ERROR(20002, "验证码错误", "", ""),



    /**
     * 系统错误
     */
    UNKNOWN_ERROR(9999, "未知的错误!", "", "");




    private Integer code;
    private String message;
    private String jpMessage;
    private String enMessage;

    public String getEnMessage() {
        return enMessage;
    }

    public String getJpMessage() {
        return jpMessage;
    }

    ResultEnumI18n(Integer code, String message, String jpMessage, String enMessage) {
        this.code = code;
        this.message = message;
        this.jpMessage = jpMessage;
        this.enMessage = enMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(String type) {
        if (StrUtil.isEmpty(type)) {
            return this.message;
        }
        switch (type) {
            case "zh-cn":
                return this.message;
            case "ja":
                return this.jpMessage;
            case "en":
                return this.enMessage;
        }
        return message;
    }


}
