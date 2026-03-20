package com.grape.grape.service;

import com.grape.grape.entity.TestPlanDailyReport;
import com.mybatisflex.core.paginate.Page;

import java.util.Date;
import java.util.List;

/**
 * 测试计划日报表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanDailyReportService extends MyBaseService<TestPlanDailyReport> {

    /**
     * 根据计划ID查询日报列表
     *
     * @param planId 计划ID
     * @return 日报列表
     */
    List<TestPlanDailyReport> listByPlanId(Long planId);

    /**
     * 根据计划ID和日期范围查询日报列表
     *
     * @param planId 计划ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日报列表
     */
    List<TestPlanDailyReport> listByPlanIdAndDateRange(Long planId, Date startDate, Date endDate);

    /**
     * 根据计划ID和报告日期查询日报
     *
     * @param planId 计划ID
     * @param reportDate 报告日期
     * @return 日报
     */
    TestPlanDailyReport getByPlanIdAndReportDate(Long planId, Date reportDate);

    /**
     * 分页查询日报
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    Page<TestPlanDailyReport> page(Page<TestPlanDailyReport> page, Long planId, Date startDate, Date endDate);
}
