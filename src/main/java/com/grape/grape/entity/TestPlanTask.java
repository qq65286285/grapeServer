package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划任务表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_task")
public class TestPlanTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 测试计划ID
     */
    private Long planId;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型: 1-测试执行, 2-缺陷修复, 3-环境准备, 4-其他
     */
    private Integer taskType;

    /**
     * 优先级: 1-高, 2-中, 3-低
     */
    private Integer priority;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 状态: 1-未开始, 2-进行中, 3-已完成, 4-已取消
     */
    private Integer status;

    /**
     * 进度百分比(0-100)
     */
    private Double progress;

    /**
     * 负责人ID
     */
    private String ownerId;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新人ID
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private Date updatedAt;
}