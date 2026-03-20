package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划主表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan")
public class TestPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划编号(自动生成，如TP20240115001)
     */
    private String planNo;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 计划类型: 1-项目测试, 2-迭代测试, 3-专项测试
     */
    private Integer planType;

    /**
     * 关联项目ID
     */
    private Long projectId;

    /**
     * 关联迭代ID
     */
    private Long iterationId;

    /**
     * 计划描述
     */
    private String description;

    /**
     * 测试范围
     */
    private String testScope;

    /**
     * 测试目标
     */
    private String testTarget;

    /**
     * 测试策略
     */
    private String testStrategy;

    /**
     * 验收标准
     */
    private String acceptanceCriteria;

    /**
     * 计划开始时间
     */
    private Date planStartDate;

    /**
     * 计划结束时间
     */
    private Date planEndDate;

    /**
     * 实际开始时间
     */
    private Date actualStartDate;

    /**
     * 实际结束时间
     */
    private Date actualEndDate;

    /**
     * 状态: 1-未开始, 2-进行中, 3-已完成, 4-已暂停, 5-已取消
     */
    private Integer status;

    /**
     * 执行进度百分比(0-100)
     */
    private BigDecimal progress;

    /**
     * 负责人ID
     */
    private String ownerId;

    /**
     * 是否为模板: 0-否, 1-是
     */
    private Integer isTemplate;

    /**
     * 基于的模板ID
     */
    private Long templateId;

    /**
     * 总用例数
     */
    private Integer totalCaseCount;

    /**
     * 已执行用例数
     */
    private Integer executedCaseCount;

    /**
     * 通过用例数
     */
    private Integer passedCaseCount;

    /**
     * 失败用例数
     */
    private Integer failedCaseCount;

    /**
     * 阻塞用例数
     */
    private Integer blockedCaseCount;

    /**
     * 跳过用例数
     */
    private Integer skippedCaseCount;

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

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;

    /**
     * 删除时间(毫秒级时间戳)
     */
    private Long deletedAt;
}
