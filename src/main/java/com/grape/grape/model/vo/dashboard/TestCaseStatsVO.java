package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用例统计VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseStatsVO {

    /**
     * 用例总数
     */
    private Long totalCaseCount;

    /**
     * 本周新增用例数
     */
    private Long weeklyNewCaseCount;

    /**
     * 用例新增趋势（最近6周）
     */
    private List<WeekTrendVO> weeklyTrend;

    /**
     * 用例模块分布
     */
    private List<DistributionVO> moduleDistribution;

    /**
     * 用例优先级分布
     */
    private List<DistributionVO> priorityDistribution;

    /**
     * 用例文件夹分布
     */
    private List<DistributionVO> folderDistribution;

    /**
     * 用例创建TOP-10人员
     */
    private List<TopCreatorVO> topCreators;
}