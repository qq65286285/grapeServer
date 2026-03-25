package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试计划用例快照步骤表 实体类。
 * 用于存储测试计划用例快照的多个步骤和对应预期结果
 *
 * @author Administrator
 * @since 2026-03-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_case_snapshot_step")
public class TestPlanCaseSnapshotStep implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 步骤ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 关联的测试计划用例快照ID
     */
    private Long snapshotId;

    /**
     * 步骤序号
     */
    private Integer stepNumber;

    /**
     * 步骤描述
     */
    private String stepDescription;

    /**
     * 步骤预期结果
     */
    private String expectedResult;

    /**
     * 步骤执行状态: 0-未执行, 1-通过, 2-失败, 3-阻塞, 4-跳过
     */
    private Integer executeStatus;

    /**
     * 步骤备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;

    /**
     * 逻辑删除（存储删除时间戳，null表示未删除）
     */
    private Long isDeleted;
}