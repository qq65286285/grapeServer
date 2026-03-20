package com.grape.grape.service;

import com.grape.grape.entity.TestPlanStatistics;
import com.mybatisflex.core.paginate.Page;
import java.util.Date;
import java.util.List;

/**
 * 测试计划统计表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanStatisticsService extends MyBaseService<TestPlanStatistics> {

    /**
     * 根据计划ID查询统计列表
     *
     * @param planId 计划ID
     * @return 统计列表
     */
    List<TestPlanStatistics> listByPlanId(Long planId);

    /**
     * 根据计划ID和统计类型查询统计列表
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @return 统计列表
     */
    List<TestPlanStatistics> listByPlanIdAndStatType(Long planId, Integer statType);

    /**
     * 根据计划ID和统计日期查询统计
     *
     * @param planId 计划ID
     * @param statDate 统计日期
     * @param statType 统计类型
     * @return 统计信息
     */
    TestPlanStatistics getByPlanIdAndDateAndType(Long planId, Date statDate, Integer statType);

    /**
     * 分页查询统计
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param statType 统计类型（可选）
     * @param qualityLevel 质量等级（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    Page<TestPlanStatistics> page(Page<TestPlanStatistics> page, Long planId, Integer statType, String qualityLevel, Date startDate, Date endDate);

    /**
     * 获取计划的最新统计
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @return 最新统计信息
     */
    TestPlanStatistics getLatestStatistics(Long planId, Integer statType);

    /**
     * 生成每日统计
     *
     * @param planId 计划ID
     * @param statDate 统计日期
     * @return 生成的统计信息
     */
    TestPlanStatistics generateDailyStatistics(Long planId, Date statDate);

    /**
     * 生成实时统计
     *
     * @param planId 计划ID
     * @return 生成的统计信息
     */
    TestPlanStatistics generateRealTimeStatistics(Long planId);

    /**
     * 获取计划的统计趋势
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @param days 天数
     * @return 统计趋势列表
     */
    List<TestPlanStatistics> getStatisticsTrend(Long planId, Integer statType, int days);

    /**
     * 获取计划的质量评分趋势
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @param days 天数
     * @return 质量评分趋势列表
     */
    List<TestPlanStatistics> getQualityScoreTrend(Long planId, Integer statType, int days);

    /**
     * 计算质量评分
     *
     * @param statistics 统计信息
     * @return 质量评分
     */
    double calculateQualityScore(TestPlanStatistics statistics);

    /**
     * 根据质量评分获取质量等级
     *
     * @param qualityScore 质量评分
     * @return 质量等级
     */
    String getQualityLevel(double qualityScore);
}