package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统概览VO
 * 包含测试用例总数、计划总数、已执行用例总数等统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewVO {

    /**
     * 测试用例总数
     */
    private Long totalTestCaseCount;

    /**
     * 测试计划总数
     */
    private Long totalTestPlanCount;

    /**
     * 已执行的测试用例总数（计划所包含的已执行用例）
     */
    private Long executedTestCaseCount;
}