package com.grape.grape.service.biz;

import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestPlan;
import com.grape.grape.entity.TestPlanExecuteRecord;
import com.grape.grape.model.vo.dashboard.DashboardOverviewVO;
import com.grape.grape.model.vo.dashboard.OverduePlanVO;
import com.grape.grape.model.vo.dashboard.TestPlanProgressVO;
import com.grape.grape.model.vo.dashboard.TrendDayVO;
import com.grape.grape.model.vo.dashboard.WeeklyStatsVO;
import com.grape.grape.service.CasesService;
import com.grape.grape.service.TestPlanExecuteRecordService;
import com.grape.grape.service.TestPlanService;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 驾驶舱看板业务服务实现类
 */
@Service
public class DashboardBizServiceImpl implements DashboardBizService {

    @Resource
    private CasesService casesService;

    @Resource
    private TestPlanService testPlanService;

    @Resource
    private TestPlanExecuteRecordService testPlanExecuteRecordService;

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO overview = new DashboardOverviewVO();

        // 获取测试用例总数
        Long totalTestCaseCount = casesService.count();
        overview.setTotalTestCaseCount(totalTestCaseCount);

        // 获取测试计划总数
        Long totalTestPlanCount = testPlanService.count();
        overview.setTotalTestPlanCount(totalTestPlanCount);

        // 获取已执行的测试用例总数
        Long executedTestCaseCount = testPlanExecuteRecordService.count();
        overview.setExecutedTestCaseCount(executedTestCaseCount);

        return overview;
    }

    @Override
    public List<OverduePlanVO> getOverduePlans(Integer days) {
        // 默认7天内到期视为快逾期
        if (days == null || days <= 0) {
            days = 7;
        }

        // 计算截止日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date deadlineDate = calendar.getTime();

        // 查询快逾期的测试计划（状态为未开始或进行中，且计划结束时间在指定天数内）
        List<TestPlan> plans = QueryChain.of(TestPlan.class)
                .where(TestPlan::getPlanEndDate).le(deadlineDate)
                .and(TestPlan::getStatus).in(1, 2)  // 1-未开始, 2-进行中
                .and(TestPlan::getIsDeleted).eq(0)
                .orderBy(TestPlan::getPlanEndDate, true)  // 按到期时间升序
                .list();

        // 转换为VO并计算距离逾期天数
        return plans.stream()
                .map(this::convertToOverduePlanVO)
                .collect(Collectors.toList());
    }

    @Override
    public TestPlanProgressVO getPlanProgress() {
        TestPlanProgressVO progress = new TestPlanProgressVO();
        
        Date now = new Date();
        
        // 进行中的计划: status = 2
        Long inProgressCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getStatus).eq(2)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        progress.setInProgressCount(inProgressCount);

        // 未开始的计划: status = 1
        Long notStartedCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getStatus).eq(1)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        progress.setNotStartedCount(notStartedCount);

        // 已完成的计划: status = 3
        Long completedCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getStatus).eq(3)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        progress.setCompletedCount(completedCount);

        // 即将到期的计划: 当前时间距离结束时间小于7天
        Calendar calendar7Days = Calendar.getInstance();
        calendar7Days.add(Calendar.DAY_OF_MONTH, 7);
        Date deadline7Days = calendar7Days.getTime();
        
        Long upcomingDueCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getPlanEndDate).between(now, deadline7Days)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        progress.setUpcomingDueCount(upcomingDueCount);

        // 已超期的计划: 当前时间大于计划结束时间
        Long overdueCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getPlanEndDate).lt(now)
                .and(TestPlan::getStatus).in(1, 2)  // 只统计未开始和进行中的超期计划
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        progress.setOverdueCount(overdueCount);

        return progress;
    }

    @Override
    public WeeklyStatsVO getWeeklyStats() {
        WeeklyStatsVO weeklyStats = new WeeklyStatsVO();
        
        // 获取本周的开始时间（周一 00:00:00）和结束时间（周日 23:59:59）的时间戳
        long[] weekRange = getWeekRangeTimestamps();
        long weekStart = weekRange[0];
        long weekEnd = weekRange[1];
        
        // 本周新增用例数
        Long newTestCaseCount = QueryChain.of(Cases.class)
                .where(Cases::getCreatedAt).between(weekStart, weekEnd)
                .and(Cases::getIsDeleted).eq(0)
                .count();
        weeklyStats.setNewTestCaseCount(newTestCaseCount);

        // 本周执行用例数
        Long executedTestCaseCount = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(weekStart, weekEnd)
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .count();
        weeklyStats.setExecutedTestCaseCount(executedTestCaseCount);

        // 本周完成计划数（本周状态变为已完成的计划）
        Long completedPlanCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getStatus).eq(3)
                .and(TestPlan::getUpdatedAt).between(weekStart, weekEnd)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        weeklyStats.setCompletedPlanCount(completedPlanCount);

        // 本周成功用例数 (executeStatus = 1 表示通过)
        Long passedTestCaseCount = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(weekStart, weekEnd)
                .and(TestPlanExecuteRecord::getExecuteStatus).eq(1)
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .count();
        weeklyStats.setPassedTestCaseCount(passedTestCaseCount);

        // 本周失败用例数 (executeStatus = 2 表示失败)
        Long failedTestCaseCount = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(weekStart, weekEnd)
                .and(TestPlanExecuteRecord::getExecuteStatus).eq(2)
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .count();
        weeklyStats.setFailedTestCaseCount(failedTestCaseCount);

        // 本周新绑定用例数（从执行记录中统计新执行的用例，排除重复）
        // 这里简化处理，直接统计执行记录数作为新绑定数
        Long newlyBoundTestCaseCount = executedTestCaseCount;
        weeklyStats.setNewlyBoundTestCaseCount(newlyBoundTestCaseCount);

        // 本周新增计划数
        Long newPlanCount = QueryChain.of(TestPlan.class)
                .where(TestPlan::getCreatedAt).between(weekStart, weekEnd)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
        weeklyStats.setNewPlanCount(newPlanCount);

        return weeklyStats;
    }

    @Override
    public List<TrendDayVO> getTrendStats() {
        List<TrendDayVO> trendList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // 从今天开始，往前推15天（含今天）
        for (int i = 14; i >= 0; i--) {
            Calendar dayCalendar = (Calendar) calendar.clone();
            dayCalendar.add(Calendar.DAY_OF_MONTH, -i);
            
            // 计算当天的开始和结束时间戳
            long dayStart = dayCalendar.getTimeInMillis();
            dayCalendar.add(Calendar.DAY_OF_MONTH, 1);
            dayCalendar.add(Calendar.MILLISECOND, -1);
            long dayEnd = dayCalendar.getTimeInMillis();
            
            // 查询当天新增用例数
            Long newTestCaseCount = QueryChain.of(Cases.class)
                    .where(Cases::getCreatedAt).between(dayStart, dayEnd)
                    .and(Cases::getIsDeleted).eq(0)
                    .count();
            
            // 查询当天新增计划数
            Long newPlanCount = QueryChain.of(TestPlan.class)
                    .where(TestPlan::getCreatedAt).between(dayStart, dayEnd)
                    .and(TestPlan::getIsDeleted).eq(0)
                    .count();
            
            // 创建趋势VO
            TrendDayVO trendDay = new TrendDayVO();
            trendDay.setDate(dateFormat.format(new Date(dayStart)));
            trendDay.setNewTestCaseCount(newTestCaseCount);
            trendDay.setNewPlanCount(newPlanCount);
            
            trendList.add(trendDay);
        }
        
        return trendList;
    }

    /**
     * 获取本周的时间范围（周一 00:00:00 到 周日 23:59:59）的毫秒级时间戳
     * @return [weekStart, weekEnd]
     */
    private long[] getWeekRangeTimestamps() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        
        // 设置为周一 00:00:00
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long weekStart = calendar.getTimeInMillis();
        
        // 设置为周日 23:59:59
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long weekEnd = calendar.getTimeInMillis();
        
        return new long[]{weekStart, weekEnd};
    }

    /**
     * 将TestPlan转换为OverduePlanVO
     */
    private OverduePlanVO convertToOverduePlanVO(TestPlan plan) {
        return OverduePlanVO.builder()
                .id(plan.getId())
                .planNo(plan.getPlanNo())
                .planName(plan.getPlanName())
                .planEndDate(plan.getPlanEndDate())
                .status(plan.getStatus())
                .progress(plan.getProgress() != null ? plan.getProgress().doubleValue() : 0.0)
                .ownerId(plan.getOwnerId())
                .daysUntilDue(calculateDaysUntilDue(plan.getPlanEndDate()))
                .build();
    }

    /**
     * 计算距离逾期天数
     * @param planEndDate 计划结束日期
     * @return 距离逾期天数，负数表示已逾期
     */
    private Integer calculateDaysUntilDue(Date planEndDate) {
        if (planEndDate == null) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        long endTime = planEndDate.getTime();
        
        // 计算天数差
        long diffDays = (endTime - currentTime) / (1000 * 60 * 60 * 24);
        
        return (int) diffDays;
    }
}