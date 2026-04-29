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
public class PlanCockpitVO {

    private Long totalPlanCount;

    private Long weekNewPlanCount;

    private Long weekStartPlanCount;

    private Long weekCompletePlanCount;

    private Long overdueUnfinishedPlanCount;

    private List<WeekTrendItemVO> planAddTrend;

    private List<StatusDistributionVO> statusDistribution;

    private List<PlanExecutionProgressVO> topExecutionProgress;

    private List<PlanTypeDistributionVO> planTypeDistribution;

    private List<PlanExecutionSummaryVO> executionSummaryTop10;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeekTrendItemVO {
        private String week;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusDistributionVO {
        private Integer status;
        private String statusName;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanExecutionProgressVO {
        private Long planId;
        private String planName;
        private Double progress;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanTypeDistributionVO {
        private Integer planType;
        private String planTypeName;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanExecutionSummaryVO {
        private Long planId;
        private String planNo;
        private String planName;
        private Integer status;
        private String statusName;
        private Integer totalCaseCount;
        private Integer executedCount;
    }
}