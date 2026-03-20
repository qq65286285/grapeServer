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
 * 测试计划执行记录表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_execute_record")
public class TestPlanExecuteRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行记录ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 快照用例ID(关联test_plan_case_snapshot.id)
     */
    private Long snapshotId;

    /**
     * 原用例ID(冗余字段，便于追溯)
     */
    private Integer originalCaseId;

    /**
     * 用例编号(冗余字段)
     */
    private String caseNumber;

    /**
     * 执行编号(唯一，格式: EXEC-{planId}-{snapshotId}-{timestamp})
     */
    private String executeNo;

    /**
     * 执行轮次(同一用例可多次执行)
     */
    private Integer executeRound;

    /**
     * 执行人ID
     */
    private Long executorId;

    /**
     * 执行时间(毫秒级时间戳)
     */
    private Long executeTime;

    /**
     * 执行耗时(秒)
     */
    private Integer executeDuration;

    /**
     * 执行状态: 1-通过(Pass), 2-失败(Fail), 3-阻塞(Block), 4-跳过(Skip)
     */
    private Integer executeStatus;

    /**
     * 实际结果描述
     */
    private String actualResult;

    /**
     * 执行环境ID
     */
    private Integer environmentId;

    /**
     * 环境快照: {"os": "Windows 10", "browser": "Chrome 120", "db_version": "MySQL 8.0"}
     */
    private String environmentSnapshot;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 失败类型: 1-功能缺陷, 2-环境问题, 3-数据问题, 4-脚本问题, 5-其他
     */
    private Integer failureType;

    /**
     * 关联的缺陷ID(如果已提交缺陷)
     */
    private Long bugId;

    /**
     * 阻塞原因
     */
    private String blockReason;

    /**
     * 阻塞类型: 1-前置条件未满足, 2-依赖功能未完成, 3-环境不可用, 4-数据不可用
     */
    private Integer blockType;

    /**
     * 跳过原因
     */
    private String skipReason;

    /**
     * 附件数量
     */
    private Integer attachmentCount;

    /**
     * 附件列表: [{"name": "screenshot.png", "url": "xxx", "size": 1024, "type": "image"}]
     */
    private String attachments;

    /**
     * 步骤执行结果: [{"step_no": 1, "description": "xxx", "expected": "xxx", "actual": "xxx", "status": 1}]
     */
    private String stepResults;

    /**
     * 测试数据: {"username": "test001", "password": "xxx"}
     */
    private String testData;

    /**
     * 执行备注
     */
    private String remark;

    /**
     * 是否已审核: 0-否, 1-是
     */
    private Integer isReviewed;

    /**
     * 审核人ID
     */
    private Long reviewedBy;

    /**
     * 审核时间(毫秒级时间戳)
     */
    private Long reviewedAt;

    /**
     * 审核意见
     */
    private String reviewComment;

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
