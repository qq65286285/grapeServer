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
 * 测试计划执行步骤记录表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_execute_step")
public class TestPlanExecuteStep implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 步骤记录ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 执行记录ID
     */
    private Long executeId;

    /**
     * 步骤序号
     */
    private Integer stepNo;

    /**
     * 步骤描述
     */
    private String stepDescription;

    /**
     * 预期结果
     */
    private String expectedResult;

    /**
     * 实际结果
     */
    private String actualResult;

    /**
     * 执行状态: 1-通过, 2-失败, 3-阻塞, 4-跳过
     */
    private Integer executeStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
