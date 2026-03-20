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
 * 测试计划报告表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_report")
public class TestPlanReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 报告编号(唯一，格式: RPT-{planId}-{timestamp})
     */
    private String reportNo;

    /**
     * 报告名称
     */
    private String reportName;

    /**
     * 报告类型: 1-阶段报告, 2-完成报告, 3-总结报告, 4-自定义报告
     */
    private Integer reportType;

    /**
     * 报告版本
     */
    private String reportVersion;

    /**
     * 统计开始时间(毫秒级时间戳)
     */
    private Long startTime;

    /**
     * 统计结束时间(毫秒级时间戳)
     */
    private Long endTime;

    /**
     * 生成时间(毫秒级时间戳)
     */
    private Long generateTime;

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
    private Integer passCaseCount;

    /**
     * 失败用例数
     */
    private Integer failCaseCount;

    /**
     * 阻塞用例数
     */
    private Integer blockCaseCount;

    /**
     * 跳过用例数
     */
    private Integer skipCaseCount;

    /**
     * 执行率(%)
     */
    private BigDecimal executeRate;

    /**
     * 通过率(%)
     */
    private BigDecimal passRate;

    /**
     * 总缺陷数
     */
    private Integer totalBugCount;

    /**
     * 致命缺陷数
     */
    private Integer fatalBugCount;

    /**
     * 严重缺陷数
     */
    private Integer seriousBugCount;

    /**
     * 一般缺陷数
     */
    private Integer normalBugCount;

    /**
     * 轻微缺陷数
     */
    private Integer minorBugCount;

    /**
     * 已修复缺陷数
     */
    private Integer fixedBugCount;

    /**
     * 参与执行人数
     */
    private Integer totalExecutorCount;

    /**
     * 执行人统计: [{"user_id": 102, "executed": 50, "pass": 45, "fail": 5}]
     */
    private String executorStats;

    /**
     * 模块统计: [{"module_id": 1, "total": 100, "executed": 80, "pass": 70}]
     */
    private String moduleStats;

    /**
     * 总执行耗时(秒)
     */
    private Integer totalDuration;

    /**
     * 平均耗时(秒)
     */
    private Integer avgDuration;

    /**
     * 时间分布: {"by_day": {...}, "by_hour": {...}}
     */
    private String timeDistribution;

    /**
     * 趋势数据: [{"date": "2024-01-01", "executed": 10, "pass": 8}]
     */
    private String trendData;

    /**
     * 测试总结
     */
    private String summary;

    /**
     * 风险分析
     */
    private String riskAnalysis;

    /**
     * 测试结论
     */
    private String conclusion;

    /**
     * 建议
     */
    private String suggestion;

    /**
     * 附件数量
     */
    private Integer attachmentCount;

    /**
     * 附件: [{"name": "详细数据.xlsx", "url": "xxx"}]
     */
    private String attachments;

    /**
     * 审批状态: 0-待审批, 1-已通过, 2-已拒绝
     */
    private Integer approveStatus;

    /**
     * 审批人ID
     */
    private Long approvedBy;

    /**
     * 审批时间(毫秒级时间戳)
     */
    private Long approvedAt;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 是否发布: 0-否, 1-是
     */
    private Integer isPublished;

    /**
     * 发布人ID
     */
    private Long publishedBy;

    /**
     * 发布时间(毫秒级时间戳)
     */
    private Long publishedAt;

    /**
     * 状态: 1-草稿, 2-待审批, 3-已发布, 4-已归档
     */
    private Integer status;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间(毫秒级时间戳)
     */
    private Long updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}