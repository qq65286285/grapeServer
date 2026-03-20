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
 * 测试计划用例缺陷关联表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_case_bug")
public class TestPlanCaseBug implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 快照用例ID
     */
    private Long snapshotId;

    /**
     * 执行记录ID(可选，如果缺陷是执行中发现的)
     */
    private Long executeRecordId;

    /**
     * 缺陷ID(关联缺陷管理系统)
     */
    private Long bugId;

    /**
     * 缺陷编号
     */
    private String bugNumber;

    /**
     * 缺陷标题
     */
    private String bugTitle;

    /**
     * 严重程度: 1-致命, 2-严重, 3-一般, 4-轻微, 5-建议
     */
    private Integer bugSeverity;

    /**
     * 优先级: 1-紧急, 2-高, 3-中, 4-低
     */
    private Integer bugPriority;

    /**
     * 缺陷状态: 1-新建, 2-已分配, 3-已修复, 4-待验证, 5-已关闭, 6-重开, 7-延期, 8-不修复
     */
    private Integer bugStatus;

    /**
     * 发现人ID
     */
    private Long foundBy;

    /**
     * 发现时间(毫秒级时间戳)
     */
    private Long foundTime;

    /**
     * 验证状态: 0-待验证, 1-验证通过, 2-验证失败
     */
    private Integer verifyStatus;

    /**
     * 验证人ID
     */
    private Long verifiedBy;

    /**
     * 验证时间(毫秒级时间戳)
     */
    private Long verifiedAt;

    /**
     * 验证备注
     */
    private String verifyRemark;

    /**
     * 验证执行记录ID(关联test_plan_execute_record.id)
     */
    private Long verifyRecordId;

    /**
     * 是否阻塞测试: 0-否, 1-是
     */
    private Integer isBlocking;

    /**
     * 影响的用例数量
     */
    private Integer affectedCaseCount;

    /**
     * 备注说明
     */
    private String remark;

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
