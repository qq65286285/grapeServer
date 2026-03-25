package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划任务分配表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_task_assign")
public class TestPlanTaskAssign implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分配ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 分配类型: 1-负责人, 2-参与人, 3-审核人
     */
    private Integer assignType;

    /**
     * 工作量(小时)
     */
    private Double workload;

    /**
     * 分配人ID
     */
    private String assignedBy;

    /**
     * 分配时间
     */
    private Date assignedAt;
}