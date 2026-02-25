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
 * 测试用例步骤表 实体类。
 * 用于存储测试用例的多个步骤和对应预期结果
 *
 * @author Administrator
 * @since 2026-02-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_case_steps")
public class TestCaseStep implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 步骤ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 关联的测试用例ID
     */
    private Integer testCaseId;

    /**
     * 步骤序号
     */
    private Integer stepNumber;

    /**
     * 步骤描述
     */
    @Column(value = "step_description")
    private String step;

    /**
     * 步骤预期结果
     */
    private String expectedResult;

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
    @Column(isLogicDelete = true)
    private Long isDeleted;
}
