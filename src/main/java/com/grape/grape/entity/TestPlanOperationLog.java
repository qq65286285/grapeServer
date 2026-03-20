package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划操作日志表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_operation_log")
public class TestPlanOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 操作类型: 1-创建, 2-修改, 3-删除, 4-启动, 5-暂停, 6-完成, 7-归档, 8-添加用例, 9-执行用例, 10-提交报告
     */
    private Integer operationType;

    /**
     * 操作模块: plan/case/execute/report/member
     */
    private String operationModule;

    /**
     * 操作动作: create/update/delete/execute
     */
    private String operationAction;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 操作前数据
     */
    private String beforeData;

    /**
     * 操作后数据
     */
    private String afterData;

    /**
     * 变更字段: [{"field": "status", "before": 1, "after": 2}]
     */
    private String changeFields;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 耗时(毫秒)
     */
    private Integer costTime;

    /**
     * 操作人ID
     */
    private Long createdBy;

    /**
     * 操作时间(毫秒级时间戳)
     */
    private Long createdAt;
}