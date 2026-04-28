package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每日趋势统计VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDayVO {

    /**
     * 日期（格式：yyyy-MM-dd）
     */
    private String date;

    /**
     * 当天新增用例数
     */
    private Long newTestCaseCount;

    /**
     * 当天新增计划数
     */
    private Long newPlanCount;
}