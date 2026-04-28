package com.grape.grape.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 快逾期测试计划VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverduePlanVO {

    /**
     * 计划ID
     */
    private Long id;

    /**
     * 计划编号
     */
    private String planNo;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 计划结束时间
     */
    private Date planEndDate;

    /**
     * 状态: 1-未开始, 2-进行中, 3-已完成, 4-已暂停, 5-已取消
     */
    private Integer status;

    /**
     * 执行进度百分比(0-100)
     */
    private Double progress;

    /**
     * 负责人ID
     */
    private String ownerId;

    /**
     * 距离逾期天数（负数表示已逾期）
     */
    private Integer daysUntilDue;
}