package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.dashboard.DashboardOverviewVO;
import com.grape.grape.model.vo.dashboard.OverduePlanVO;
import com.grape.grape.model.vo.dashboard.TestPlanProgressVO;
import com.grape.grape.model.vo.dashboard.TrendDayVO;
import com.grape.grape.model.vo.dashboard.WeeklyStatsVO;
import com.grape.grape.service.biz.DashboardBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 驾驶舱看板控制器
 */
@RestController
@RequestMapping("/spec")
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Resource
    private DashboardBizService dashboardBizService;

    /**
     * 获取系统概览统计信息
     * 
     * @return Resp<DashboardOverviewVO> 包含测试用例总数、计划总数、已执行用例总数
     */
    @GetMapping("/overview")
    public Resp getOverview() {
        log.info("获取系统概览统计信息");
        
        try {
            DashboardOverviewVO overview = dashboardBizService.getOverview();
            return Resp.ok(overview);
        } catch (Exception e) {
            log.error("获取系统概览统计信息失败", e);
            return Resp.error();
        }
    }

    /**
     * 获取快逾期的测试计划列表
     * 
     * @param days 多少天内到期的计划视为快逾期，默认7天
     * @return Resp<List<OverduePlanVO>> 快逾期测试计划列表
     */
    @GetMapping("/overdue-plans")
    public Resp getOverduePlans(@RequestParam(defaultValue = "7") Integer days) {
        log.info("获取快逾期测试计划列表，天数: {}", days);
        
        try {
            List<OverduePlanVO> overduePlans = dashboardBizService.getOverduePlans(days);
            return Resp.ok(overduePlans);
        } catch (Exception e) {
            log.error("获取快逾期测试计划列表失败", e);
            return Resp.error();
        }
    }

    /**
     * 获取测试计划进度统计
     * 
     * @return Resp<TestPlanProgressVO> 测试计划进度统计
     */
    @GetMapping("/plan-progress")
    public Resp getPlanProgress() {
        log.info("获取测试计划进度统计");
        
        try {
            TestPlanProgressVO progress = dashboardBizService.getPlanProgress();
            return Resp.ok(progress);
        } catch (Exception e) {
            log.error("获取测试计划进度统计失败", e);
            return Resp.error();
        }
    }

    /**
     * 获取本周工作动态统计
     * 
     * @return Resp<WeeklyStatsVO> 本周工作动态统计
     */
    @GetMapping("/weekly-stats")
    public Resp getWeeklyStats() {
        log.info("获取本周工作动态统计");
        
        try {
            WeeklyStatsVO weeklyStats = dashboardBizService.getWeeklyStats();
            return Resp.ok(weeklyStats);
        } catch (Exception e) {
            log.error("获取本周工作动态统计失败", e);
            return Resp.error();
        }
    }

    /**
     * 获取15天趋势统计（从今天往前15天，含今天）
     * 
     * @return Resp<List<TrendDayVO>> 15天趋势统计列表
     */
    @GetMapping("/trend-stats")
    public Resp getTrendStats() {
        log.info("获取15天趋势统计");
        
        try {
            List<TrendDayVO> trendStats = dashboardBizService.getTrendStats();
            return Resp.ok(trendStats);
        } catch (Exception e) {
            log.error("获取15天趋势统计失败", e);
            return Resp.error();
        }
    }
}