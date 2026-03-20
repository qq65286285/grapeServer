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
 * 测试计划日报表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_daily_report")
public class TestPlanDailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日报ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 测试计划ID
     */
    private Long planId;

    /**
     * 报告日期
     */
    private Date reportDate;

    /**
     * 当日执行用例数
     */
    private Integer executedCount;

    /**
     * 当日通过数
     */
    private Integer passedCount;

    /**
     * 当日失败数
     */
    private Integer failedCount;

    /**
     * 当日阻塞数
     */
    private Integer blockedCount;

    /**
     * 新增缺陷数
     */
    private Integer newBugCount;

    /**
     * 修复缺陷数
     */
    private Integer fixedBugCount;

    /**
     * 累计进度
     */
    private BigDecimal progress;

    /**
     * 今日总结
     */
    private String summary;

    /**
     * 明日计划
     */
    private String planTomorrow;

    /**
     * 风险问题
     */
    private String riskIssues;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;
}
