package com.grape.grape.service.biz;

import com.grape.grape.entity.TestPlan;
import com.grape.grape.entity.TestPlanCaseSnapshot;
import com.grape.grape.model.vo.cockpit.PlanCockpitVO;
import com.grape.grape.model.vo.cockpit.PlanCockpitVO.*;
import com.grape.grape.service.TestPlanCaseSnapshotService;
import com.grape.grape.service.TestPlanService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计划驾驶舱业务服务实现类
 * 提供计划维度驾驶舱的各项统计数据
 */
@Service
public class PlanCockpitBizServiceImpl implements PlanCockpitBizService {

    @Resource
    private TestPlanService testPlanService;

    @Resource
    private TestPlanCaseSnapshotService testPlanCaseSnapshotService;

    /**
     * 计划状态名称映射
     */
    private static final Map<Integer, String> STATUS_NAMES = new HashMap<>();

    /**
     * 计划类型名称映射
     */
    private static final Map<Integer, String> PLAN_TYPE_NAMES = new HashMap<>();

    static {
        STATUS_NAMES.put(1, "未开始");
        STATUS_NAMES.put(2, "进行中");
        STATUS_NAMES.put(3, "已完成");
        STATUS_NAMES.put(4, "已暂停");
        STATUS_NAMES.put(5, "已取消");

        PLAN_TYPE_NAMES.put(1, "项目测试");
        PLAN_TYPE_NAMES.put(2, "迭代测试");
        PLAN_TYPE_NAMES.put(3, "专项测试");
    }

    /**
     * 获取计划驾驶舱所有数据
     *
     * @return 计划驾驶舱数据VO
     */
    @Override
    public PlanCockpitVO getPlanCockpitData() {
        PlanCockpitVO vo = new PlanCockpitVO();

        // 设置计划总数：当前数据库中所有未删除的计划数量
        vo.setTotalPlanCount(getTotalPlanCount());
        // 设置本周新增计划数：本周内创建的计划数量
        vo.setWeekNewPlanCount(getWeekNewPlanCount());
        // 设置本周开始计划数：计划开始日期在本周的计划数量
        vo.setWeekStartPlanCount(getWeekStartPlanCount());
        // 设置本周完成计划数：实际结束时间在本周的计划数量
        vo.setWeekCompletePlanCount(getWeekCompletePlanCount());
        // 设置逾期未完成计划数：当前时间大于计划结束时间且状态为未开始或进行中的计划数量
        vo.setOverdueUnfinishedPlanCount(getOverdueUnfinishedPlanCount());
        // 设置计划新增趋势：最近6周（含本周）每周新增的计划数
        vo.setPlanAddTrend(getPlanAddTrend());
        // 设置计划状态分布：计划总数按状态分组统计
        vo.setStatusDistribution(getStatusDistribution());
        // 设置TOP 5执行进度：按用例通过率降序排列的前5个计划
        vo.setTopExecutionProgress(getTopExecutionProgress());
        // 设置计划类型分布：计划总数按计划类型分组统计
        vo.setPlanTypeDistribution(getPlanTypeDistribution());
        // 设置执行数TOP 10：按已执行用例数降序排列的前10个计划
        vo.setExecutionSummaryTop10(getExecutionSummaryTop10());

        return vo;
    }

    /**
     * 获取计划总数
     *
     * @return 计划总数
     */
    private Long getTotalPlanCount() {
        return QueryChain.of(TestPlan.class)
                .where(TestPlan::getIsDeleted).eq(0)
                .count();
    }

    /**
     * 获取本周新增计划数
     *
     * @return 本周新增计划数
     */
    private Long getWeekNewPlanCount() {
        long[] weekRange = getWeekRangeTimestamps();
        return QueryChain.of(TestPlan.class)
                .where(TestPlan::getCreatedAt).between(weekRange[0], weekRange[1])
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
    }

    /**
     * 获取本周开始的计划数
     *
     * @return 本周开始的计划数
     */
    private Long getWeekStartPlanCount() {
        long[] weekRange = getWeekRangeTimestamps();
        return QueryChain.of(TestPlan.class)
                .where(TestPlan::getPlanStartDate).between(weekRange[0], weekRange[1])
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
    }

    /**
     * 获取本周完成的计划数
     *
     * @return 本周完成的计划数
     */
    private Long getWeekCompletePlanCount() {
        long[] weekRange = getWeekRangeTimestamps();
        return QueryChain.of(TestPlan.class)
                .where(TestPlan::getActualEndDate).between(weekRange[0], weekRange[1])
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
    }

    /**
     * 获取逾期未完成的计划数
     * 逾期未完成定义：当前时间大于计划结束时间且状态是未开始或进行中
     *
     * @return 逾期未完成的计划数
     */
    private Long getOverdueUnfinishedPlanCount() {
        Date now = new Date();
        return QueryChain.of(TestPlan.class)
                .where(TestPlan::getPlanEndDate).lt(now)
                .and(TestPlan::getStatus).in(1, 2)
                .and(TestPlan::getIsDeleted).eq(0)
                .count();
    }

    /**
     * 获取计划新增趋势（最近6周）
     *
     * @return 每周新增计划数列表
     */
    private List<WeekTrendItemVO> getPlanAddTrend() {
        List<WeekTrendItemVO> trendList = new ArrayList<>();
        SimpleDateFormat weekFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 5; i >= 0; i--) {
            Calendar weekStart = (Calendar) calendar.clone();
            weekStart.add(Calendar.WEEK_OF_YEAR, -i);
            Calendar weekEnd = (Calendar) weekStart.clone();
            weekEnd.add(Calendar.DAY_OF_MONTH, 6);
            weekEnd.set(Calendar.HOUR_OF_DAY, 23);
            weekEnd.set(Calendar.MINUTE, 59);
            weekEnd.set(Calendar.SECOND, 59);
            weekEnd.set(Calendar.MILLISECOND, 999);

            Long count = QueryChain.of(TestPlan.class)
                    .where(TestPlan::getCreatedAt).between(weekStart.getTimeInMillis(), weekEnd.getTimeInMillis())
                    .and(TestPlan::getIsDeleted).eq(0)
                    .count();

            WeekTrendItemVO item = WeekTrendItemVO.builder()
                    .week(weekFormat.format(weekStart.getTime()))
                    .count(count)
                    .build();
            trendList.add(item);
        }

        return trendList;
    }

    /**
     * 获取计划状态分布
     *
     * @return 按状态分组的统计列表
     */
    private List<StatusDistributionVO> getStatusDistribution() {
        List<TestPlan> plans = QueryChain.of(TestPlan.class)
                .where(TestPlan::getIsDeleted).eq(0)
                .list();

        Map<Integer, Long> statusCountMap = plans.stream()
                .collect(Collectors.groupingBy(
                        TestPlan::getStatus,
                        Collectors.counting()
                ));

        return statusCountMap.entrySet().stream()
                .map(entry -> StatusDistributionVO.builder()
                        .status(entry.getKey())
                        .statusName(STATUS_NAMES.getOrDefault(entry.getKey(), "未知"))
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取TOP 5计划执行进度（按通过率排序）
     * 通过率计算公式：通过用例数 / 已执行用例数
     * 数据来源：test_plan_case_snapshot 表
     * 已执行：executeStatus != 0（0-未执行, 1-通过, 2-失败, 3-阻塞, 4-跳过）
     * 通过：executeStatus == 1
     *
     * @return TOP 5计划执行进度列表
     */
    private List<PlanExecutionProgressVO> getTopExecutionProgress() {
        List<TestPlan> plans = QueryChain.of(TestPlan.class)
                .where(TestPlan::getIsDeleted).eq(0)
                .list();

        return plans.stream()
                .map(plan -> {
                    double successRate = 0.0;
                    
                    // 使用 TestPlanCaseSnapshot 表统计执行成功率
                    List<TestPlanCaseSnapshot> snapshots = testPlanCaseSnapshotService.listByPlanId(plan.getId());
                    
                    if (snapshots != null && !snapshots.isEmpty()) {
                        // 统计已执行用例数（executeStatus != 0）
                        long executedCount = snapshots.stream()
                                .filter(s -> s.getExecuteStatus() != null && s.getExecuteStatus() != 0)
                                .count();
                        
                        // 统计通过用例数（executeStatus == 1）
                        long passedCount = snapshots.stream()
                                .filter(s -> s.getExecuteStatus() != null && s.getExecuteStatus() == 1)
                                .count();
                        
                        if (executedCount > 0) {
                            successRate = (double) passedCount / executedCount;
                        }
                    }
                    
                    return PlanExecutionProgressVO.builder()
                            .planId(plan.getId())
                            .planName(plan.getPlanName())
                            .progress(successRate)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getProgress(), a.getProgress()))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * 获取计划类型分布
     *
     * @return 按计划类型分组的统计列表
     */
    private List<PlanTypeDistributionVO> getPlanTypeDistribution() {
        List<TestPlan> plans = QueryChain.of(TestPlan.class)
                .where(TestPlan::getIsDeleted).eq(0)
                .list();

        Map<Integer, Long> typeCountMap = plans.stream()
                .collect(Collectors.groupingBy(
                        TestPlan::getPlanType,
                        Collectors.counting()
                ));

        return typeCountMap.entrySet().stream()
                .map(entry -> PlanTypeDistributionVO.builder()
                        .planType(entry.getKey())
                        .planTypeName(PLAN_TYPE_NAMES.getOrDefault(entry.getKey(), "未知"))
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取执行数TOP 10的计划列表
     * 数据来源：test_plan_case_snapshot 表
     * 已执行用例数统计：executeStatus != 0（0-未执行, 1-通过, 2-失败, 3-阻塞, 4-跳过）
     *
     * @return 执行数TOP 10计划列表
     */
    private List<PlanExecutionSummaryVO> getExecutionSummaryTop10() {
        List<TestPlan> plans = QueryChain.of(TestPlan.class)
                .where(TestPlan::getIsDeleted).eq(0)
                .list();

        return plans.stream()
                .map(plan -> {
                    // 使用 TestPlanCaseSnapshot 表统计用例数
                    List<TestPlanCaseSnapshot> snapshots = testPlanCaseSnapshotService.listByPlanId(plan.getId());
                    int totalCaseCount = 0;
                    int executedCount = 0;
                    
                    if (snapshots != null && !snapshots.isEmpty()) {
                        // 用例总数：该测试计划绑定的所有用例数量
                        totalCaseCount = snapshots.size();
                        
                        // 已执行用例数（executeStatus != 0）
                        executedCount = (int) snapshots.stream()
                                .filter(s -> s.getExecuteStatus() != null && s.getExecuteStatus() != 0)
                                .count();
                    }
                    
                    return PlanExecutionSummaryVO.builder()
                            .planId(plan.getId())
                            .planNo(plan.getPlanNo())
                            .planName(plan.getPlanName())
                            .status(plan.getStatus())
                            .statusName(STATUS_NAMES.getOrDefault(plan.getStatus(), "未知"))
                            .totalCaseCount(totalCaseCount)
                            .executedCount(executedCount)
                            .build();
                })
                .sorted((a, b) -> Integer.compare(b.getExecutedCount(), a.getExecutedCount()))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 获取本周时间范围的时间戳（周一00:00:00 到 周日23:59:59）
     *
     * @return 包含开始时间戳和结束时间戳的数组 [weekStart, weekEnd]
     */
    private long[] getWeekRangeTimestamps() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long weekStart = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long weekEnd = calendar.getTimeInMillis();

        return new long[]{weekStart, weekEnd};
    }
}