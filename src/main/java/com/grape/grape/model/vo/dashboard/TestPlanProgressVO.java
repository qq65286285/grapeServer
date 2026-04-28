package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划进度统计VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPlanProgressVO {

    /**
     * 进行中的计划数量 (status = 2)
     */
    private Long inProgressCount;

    /**
     * 即将到期的计划数量 (当前时间距离结束时间小于7天)
     */
    private Long upcomingDueCount;

    /**
     * 已超期的计划数量 (当前时间大于计划结束时间)
     */
    private Long overdueCount;

    /**
     * 未开始的计划数量 (status = 1)
     */
    private Long notStartedCount;

    /**
     * 已完成的计划数量 (status = 3)
     */
    private Long completedCount;
}