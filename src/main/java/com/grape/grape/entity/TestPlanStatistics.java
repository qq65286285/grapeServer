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
 * 测试计划统计表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_statistics")
public class TestPlanStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 统计日期(yyyy-MM-dd)
     */
    private Date statDate;

    /**
     * 统计类型: 1-每日统计, 2-每周统计, 3-每月统计, 4-实时统计
     */
    private Integer statType;

    /**
     * 总用例数
     */
    private Integer totalCaseCount;

    /**
     * 已执行用例数
     */
    private Integer executedCaseCount;

    /**
     * 未执行用例数
     */
    private Integer unexecutedCaseCount;

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
     * 失败率(%)
     */
    private BigDecimal failRate;

    /**
     * 新增缺陷数
     */
    private Integer newBugCount;

    /**
     * 累计缺陷数
     */
    private Integer totalBugCount;

    /**
     * 已修复缺陷数
     */
    private Integer fixedBugCount;

    /**
     * 未解决缺陷数
     */
    private Integer openBugCount;

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
     * 活跃执行人数
     */
    private Integer activeExecutorCount;

    /**
     * 总执行次数
     */
    private Integer totalExecuteTimes;

    /**
     * 总执行耗时(秒)
     */
    private Integer totalDuration;

    /**
     * 平均耗时(秒)
     */
    private Integer avgDuration;

    /**
     * 模块分布: {"module_1": {"total": 50, "executed": 40}, "module_2": {...}}
     */
    private String moduleDistribution;

    /**
     * 优先级分布: {"P0": 10, "P1": 30, "P2": 50, "P3": 20}
     */
    private String priorityDistribution;

    /**
     * 当日新增执行数
     */
    private Integer dailyNewExecute;

    /**
     * 当日新增通过数
     */
    private Integer dailyNewPass;

    /**
     * 当日新增失败数
     */
    private Integer dailyNewFail;

    /**
     * 当日新增缺陷数
     */
    private Integer dailyNewBug;

    /**
     * 执行趋势: up/down/stable
     */
    private String executeTrend;

    /**
     * 通过趋势: up/down/stable
     */
    private String passTrend;

    /**
     * 缺陷趋势: up/down/stable
     */
    private String bugTrend;

    /**
     * 质量评分(0-100)
     */
    private BigDecimal qualityScore;

    /**
     * 质量等级: A/B/C/D
     */
    private String qualityLevel;

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