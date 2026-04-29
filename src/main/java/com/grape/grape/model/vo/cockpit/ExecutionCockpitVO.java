package com.grape.grape.model.vo.cockpit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionCockpitVO {

    private Long linkedCaseCount;

    private Long totalExecuteCount;

    private Long weekExecuteCount;

    private Long weekCompletedCount;

    private List<DayTrendItemVO> executeTrend;

    private List<ExecuteResultDistributionVO> weekResultDistribution;

    private List<ExecuteResultDistributionVO> todayResultDistribution;

    private List<RecentExecuteCaseVO> recentExecuteCases;

    private List<PlanExecuteResultVO> planExecuteResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayTrendItemVO {
        private String date;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteResultDistributionVO {
        private Integer status;
        private String statusName;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentExecuteCaseVO {
        private Long id;
        private Long planId;
        private String planName;
        private String caseNumber;
        private Integer executeStatus;
        private String executeStatusName;
        private Long executeTime;
        private String executorId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanExecuteResultVO {
        private Long planId;
        private String planNo;
        private String planName;
        private Long totalCaseCount;
        private Long totalExecuted;
        private Long passedCount;
        private Long failedCount;
        private Long blockedCount;
        private Long skippedCount;
        private Long weekNewExecuted;
    }
}