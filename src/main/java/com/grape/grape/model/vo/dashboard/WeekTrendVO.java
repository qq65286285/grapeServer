package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 周趋势VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekTrendVO {

    /**
     * 周起始日期（格式：yyyy-MM-dd）
     */
    private String weekStartDate;

    /**
     * 该周新增用例数
     */
    private Long newCaseCount;
}