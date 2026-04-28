package com.grape.grape.service.biz;

import com.grape.grape.model.vo.dashboard.DashboardOverviewVO;
import com.grape.grape.model.vo.dashboard.OverduePlanVO;
import com.grape.grape.model.vo.dashboard.TestPlanProgressVO;
import com.grape.grape.model.vo.dashboard.TrendDayVO;
import com.grape.grape.model.vo.dashboard.WeeklyStatsVO;

import java.util.List;

/**
 * 驾驶舱看板业务服务接口
 */
public interface DashboardBizService {

    /**
     * 获取系统概览统计信息
     * 
     * @return 系统概览VO，包含测试用例总数、计划总数、已执行用例总数
     */
    DashboardOverviewVO getOverview();

    /**
     * 获取快逾期的测试计划列表
     * 
     * @param days 多少天内到期的计划视为快逾期，默认7天
     * @return 快逾期测试计划列表
     */
    List<OverduePlanVO> getOverduePlans(Integer days);

    /**
     * 获取测试计划进度统计
     * 
     * @return 测试计划进度统计VO
     */
    TestPlanProgressVO getPlanProgress();

    /**
     * 获取本周工作动态统计
     * 
     * @return 本周工作动态统计VO
     */
    WeeklyStatsVO getWeeklyStats();

    /**
     * 获取15天趋势统计（从今天往前15天，含今天）
     * 
     * @return 15天趋势统计列表
     */
    List<TrendDayVO> getTrendStats();
}