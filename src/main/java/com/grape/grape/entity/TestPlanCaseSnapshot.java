package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计划用例快照表-用例绑定到计划时的版本快照 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_case_snapshot")
public class TestPlanCaseSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 快照ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 原用例ID(来自test_cases表)
     */
    private Integer originalCaseId;

    /**
     * 用例编号
     */
    private String caseNumber;

    /**
     * 测试用例标题
     */
    private String title;

    /**
     * 测试用例描述
     */
    private String description;

    /**
     * 优先级（1: Low, 2: Medium, 3: High）
     */
    private Integer priority;

    /**
     * 用例状态(快照时的状态)
     */
    private Integer caseStatus;

    /**
     * 快照时的用例版本号
     */
    private Integer caseVersion;

    /**
     * 测试环境ID
     */
    private Integer environmentId;

    /**
     * 预期结果
     */
    private String expectedResult;

    /**
     * 模块
     */
    private String module;

    /**
     * 所属文件夹ID
     */
    private Integer folderId;

    /**
     * 用例备注(快照时的备注)
     */
    private String caseRemark;

    /**
     * 指定执行人ID
     */
    private Long executorId;

    /**
     * 执行状态: 0-未执行, 1-通过, 2-失败, 3-阻塞, 4-跳过
     */
    private Integer executeStatus;

    /**
     * 执行次数
     */
    private Integer executeCount;

    /**
     * 最后执行时间(毫秒级时间戳)
     */
    private Long lastExecuteTime;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 在计划中的备注说明
     */
    private String planRemark;

    /**
     * 快照时间(毫秒级时间戳)
     */
    private Long snapshotTime;

    /**
     * 快照操作人ID
     */
    private Long snapshotBy;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间(毫秒级时间戳)
     */
    private Long updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}
