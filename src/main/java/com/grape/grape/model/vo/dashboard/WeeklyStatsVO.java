package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本周工作动态统计VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStatsVO {

    /**
     * 本周新增用例数
     */
    private Long newTestCaseCount;

    /**
     * 本周执行用例数
     */
    private Long executedTestCaseCount;

    /**
     * 本周完成计划数
     */
    private Long completedPlanCount;

    /**
     * 本周成功用例数
     */
    private Long passedTestCaseCount;

    /**
     * 本周失败用例数
     */
    private Long failedTestCaseCount;

    /**
     * 本周新绑定用例数
     */
    private Long newlyBoundTestCaseCount;

    /**
     * 本周新增计划数
     */
    private Long newPlanCount;
}