package com.grape.grape.service.biz;

import com.grape.grape.entity.TestPlan;
import com.grape.grape.entity.TestPlanCaseSnapshot;
import com.grape.grape.entity.TestPlanExecuteRecord;
import com.grape.grape.model.vo.cockpit.ExecutionCockpitVO;
import com.grape.grape.model.vo.cockpit.ExecutionCockpitVO.*;
import com.grape.grape.service.TestPlanCaseSnapshotService;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExecutionCockpitBizServiceImpl implements ExecutionCockpitBizService {

    @Resource
    private TestPlanCaseSnapshotService testPlanCaseSnapshotService;

    private static final Map<Integer, String> EXECUTE_STATUS_NAMES = new HashMap<>();

    static {
        EXECUTE_STATUS_NAMES.put(1, "通过");
        EXECUTE_STATUS_NAMES.put(2, "失败");
        EXECUTE_STATUS_NAMES.put(3, "阻塞");
        EXECUTE_STATUS_NAMES.put(4, "跳过");
    }

    @Override
    public ExecutionCockpitVO getExecutionCockpitData() {
        ExecutionCockpitVO vo = new ExecutionCockpitVO();

        vo.setLinkedCaseCount(getLinkedCaseCount());
        vo.setTotalExecuteCount(getTotalExecuteCount());
        vo.setWeekExecuteCount(getWeekExecuteCount());
        vo.setWeekCompletedCount(getWeekCompletedCount());
        vo.setExecuteTrend(getExecuteTrend());
        vo.setWeekResultDistribution(getWeekResultDistribution());
        vo.setTodayResultDistribution(getTodayResultDistribution());
        vo.setRecentExecuteCases(getRecentExecuteCases());
        vo.setPlanExecuteResults(getPlanExecuteResults());

        return vo;
    }

    private Long getLinkedCaseCount() {
        // 统计已被关联的用例数，每个计划绑定的用例都单独计算
        // 例如：A关联1,2,3；B关联2,3 → 总数为5条
        return QueryChain.of(TestPlanCaseSnapshot.class)
                .where(TestPlanCaseSnapshot::getIsDeleted).eq(0)
                .count();
    }

    private Long getTotalExecuteCount() {
        // 统计已执行的用例数，按 (planId, snapshotId) 去重
        // 例如：A执行1,3；B执行2,3,3 → 总执行数为4条（A的1,3 和 B的2,3）
        List<TestPlanExecuteRecord> records = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .list();
        
        // 按 (planId, snapshotId) 组合去重
        Set<String> uniquePlanSnapshotPairs = records.stream()
                .filter(r -> r.getPlanId() != null && r.getSnapshotId() != null)
                .map(r -> r.getPlanId() + "_" + r.getSnapshotId())
                .collect(Collectors.toSet());
        
        return (long) uniquePlanSnapshotPairs.size();
    }

    private Long getWeekExecuteCount() {
        long[] weekRange = getWeekRangeTimestamps();
        return QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(weekRange[0], weekRange[1])
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .count();
    }

    private Long getWeekCompletedCount() {
        long[] weekRange = getWeekRangeTimestamps();
        return QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(weekRange[0], weekRange[1])
                .and(TestPlanExecuteRecord::getExecuteStatus).eq(1)
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .count();
    }

    private List<DayTrendItemVO> getExecuteTrend() {
        List<DayTrendItemVO> trendList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 6; i >= 0; i--) {
            Calendar dayStart = (Calendar) calendar.clone();
            dayStart.add(Calendar.DAY_OF_MONTH, -i);
            long dayStartTime = dayStart.getTimeInMillis();

            Calendar dayEnd = (Calendar) dayStart.clone();
            dayEnd.add(Calendar.DAY_OF_MONTH, 1);
            dayEnd.add(Calendar.MILLISECOND, -1);
            long dayEndTime = dayEnd.getTimeInMillis();

            Long count = QueryChain.of(TestPlanExecuteRecord.class)
                    .where(TestPlanExecuteRecord::getExecuteTime).between(dayStartTime, dayEndTime)
                    .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                    .count();

            DayTrendItemVO item = DayTrendItemVO.builder()
                    .date(dateFormat.format(new Date(dayStartTime)))
                    .count(count)
                    .build();
            trendList.add(item);
        }

        return trendList;
    }

    private List<ExecuteResultDistributionVO> getWeekResultDistribution() {
        long[] weekRange = getWeekRangeTimestamps();
        List<TestPlanExecuteRecord> records = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(weekRange[0], weekRange[1])
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .list();

        Map<Integer, Long> statusCountMap = records.stream()
                .collect(Collectors.groupingBy(
                        TestPlanExecuteRecord::getExecuteStatus,
                        Collectors.counting()
                ));

        return statusCountMap.entrySet().stream()
                .map(entry -> ExecuteResultDistributionVO.builder()
                        .status(entry.getKey())
                        .statusName(EXECUTE_STATUS_NAMES.getOrDefault(entry.getKey(), "未知"))
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ExecuteResultDistributionVO> getTodayResultDistribution() {
        long[] todayRange = getTodayRangeTimestamps();
        List<TestPlanExecuteRecord> records = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getExecuteTime).between(todayRange[0], todayRange[1])
                .and(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .list();

        Map<Integer, Long> statusCountMap = records.stream()
                .collect(Collectors.groupingBy(
                        TestPlanExecuteRecord::getExecuteStatus,
                        Collectors.counting()
                ));

        return statusCountMap.entrySet().stream()
                .map(entry -> ExecuteResultDistributionVO.builder()
                        .status(entry.getKey())
                        .statusName(EXECUTE_STATUS_NAMES.getOrDefault(entry.getKey(), "未知"))
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<RecentExecuteCaseVO> getRecentExecuteCases() {
        List<TestPlanExecuteRecord> records = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .orderBy(TestPlanExecuteRecord::getExecuteTime, false)
                .limit(20)
                .list();

        Map<Long, String> planNameMap = getPlanNameMap(records.stream()
                .map(TestPlanExecuteRecord::getPlanId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return records.stream()
                .map(record -> RecentExecuteCaseVO.builder()
                        .id(record.getId())
                        .planId(record.getPlanId())
                        .planName(planNameMap.get(record.getPlanId()))
                        .caseNumber(record.getCaseNumber())
                        .executeStatus(record.getExecuteStatus())
                        .executeStatusName(EXECUTE_STATUS_NAMES.getOrDefault(record.getExecuteStatus(), "未知"))
                        .executeTime(record.getExecuteTime())
                        .executorId(record.getExecutorId())
                        .build())
                .collect(Collectors.toList());
    }

    private List<PlanExecuteResultVO> getPlanExecuteResults() {
        List<TestPlanExecuteRecord> records = QueryChain.of(TestPlanExecuteRecord.class)
                .where(TestPlanExecuteRecord::getIsDeleted).eq(0)
                .list();

        Map<Long, PlanExecuteResultVO> planResultMap = new LinkedHashMap<>();
        long[] weekRange = getWeekRangeTimestamps();

        for (TestPlanExecuteRecord record : records) {
            Long planId = record.getPlanId();
            if (planId == null) continue;

            planResultMap.computeIfAbsent(planId, id -> {
                TestPlan plan = QueryChain.of(TestPlan.class)
                        .where(TestPlan::getId).eq(id)
                        .one();
                
                // 统计该计划绑定的用例总数
                List<TestPlanCaseSnapshot> snapshots = testPlanCaseSnapshotService.listByPlanId(id);
                long totalCaseCount = snapshots != null ? snapshots.size() : 0L;
                
                return PlanExecuteResultVO.builder()
                        .planId(id)
                        .planNo(plan != null ? plan.getPlanNo() : "")
                        .planName(plan != null ? plan.getPlanName() : "")
                        .totalCaseCount(totalCaseCount)
                        .totalExecuted(0L)
                        .passedCount(0L)
                        .failedCount(0L)
                        .blockedCount(0L)
                        .skippedCount(0L)
                        .weekNewExecuted(0L)
                        .build();
            });

            PlanExecuteResultVO result = planResultMap.get(planId);
            result.setTotalExecuted(result.getTotalExecuted() + 1);

            // 统计本周新增执行数
            Long executeTime = record.getExecuteTime();
            if (executeTime != null && executeTime >= weekRange[0] && executeTime <= weekRange[1]) {
                result.setWeekNewExecuted(result.getWeekNewExecuted() + 1);
            }

            Integer status = record.getExecuteStatus();
            if (status != null) {
                switch (status) {
                    case 1:
                        result.setPassedCount(result.getPassedCount() + 1);
                        break;
                    case 2:
                        result.setFailedCount(result.getFailedCount() + 1);
                        break;
                    case 3:
                        result.setBlockedCount(result.getBlockedCount() + 1);
                        break;
                    case 4:
                        result.setSkippedCount(result.getSkippedCount() + 1);
                        break;
                }
            }
        }

        return new ArrayList<>(planResultMap.values());
    }

    private Map<Long, String> getPlanNameMap(List<Long> planIds) {
        if (planIds == null || planIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<TestPlan> plans = QueryChain.of(TestPlan.class)
                .where(TestPlan::getId).in(planIds)
                .and(TestPlan::getIsDeleted).eq(0)
                .list();

        return plans.stream()
                .collect(Collectors.toMap(TestPlan::getId, TestPlan::getPlanName));
    }

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

    private long[] getTodayRangeTimestamps() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStart = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long todayEnd = calendar.getTimeInMillis();

        return new long[]{todayStart, todayEnd};
    }
}