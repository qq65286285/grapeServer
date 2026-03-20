package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划里程碑表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_milestone")
public class TestPlanMilestone implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 里程碑ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 里程碑名称
     */
    private String milestoneName;

    /**
     * 里程碑编码
     */
    private String milestoneCode;

    /**
     * 类型: 1-计划里程碑, 2-执行里程碑, 3-质量里程碑, 4-交付里程碑
     */
    private Integer milestoneType;

    /**
     * 描述
     */
    private String description;

    /**
     * 目标日期(毫秒级时间戳)
     */
    private Long targetDate;

    /**
     * 实际完成日期(毫秒级时间戳)
     */
    private Long actualDate;

    /**
     * 目标指标: {"execute_rate": 100, "pass_rate": 95, "bug_count": {"fatal": 0, "serious": 0}}
     */
    private String targetMetrics;

    /**
     * 实际指标
     */
    private String actualMetrics;

    /**
     * 状态: 1-未开始, 2-进行中, 3-已完成, 4-已延期, 5-已取消
     */
    private Integer status;

    /**
     * 完成率(%)
     */
    private BigDecimal completeRate;

    /**
     * 风险等级: 1-高, 2-中, 3-低
     */
    private Integer riskLevel;

    /**
     * 风险描述
     */
    private String riskDescription;

    /**
     * 负责人ID
     */
    private String ownerId;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 父里程碑ID
     */
    private Long parentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;

    /**
     * 更新人ID
     */
    private String updatedBy;

    /**
     * 更新时间(毫秒级时间戳)
     */
    private Long updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}
