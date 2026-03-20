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
 * 测试计划成员表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_member")
public class TestPlanMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色类型: 1-负责人(Owner), 2-参与人(Member), 3-签署人(Approver), 4-抄送人(CC)
     */
    private Integer roleType;

    /**
     * 权限配置: {"can_edit": true, "can_execute": true, "can_delete": false, "can_approve": false}
     */
    private String permissions;

    /**
     * 范围类型: 0-全部, 1-指定模块, 2-指定用例组
     */
    private Integer scopeType;

    /**
     * 范围配置: {"module_ids": [1,2,3], "group_names": ["分组A"]}
     */
    private String scopeConfig;

    /**
     * 审批状态: 0-待审批, 1-已通过, 2-已拒绝, 3-无需审批
     */
    private Integer approveStatus;

    /**
     * 审批时间(毫秒级时间戳)
     */
    private Long approveTime;

    /**
     * 审批意见
     */
    private String approveRemark;

    /**
     * 审批顺序(支持多级审批)
     */
    private Integer approveOrder;

    /**
     * 通知配置: {"email": true, "sms": false, "remind_before_days": 1, "remind_on_delay": true}
     */
    private String notifyConfig;

    /**
     * 状态: 1-正常, 2-已移除
     */
    private Integer status;

    /**
     * 分配的用例数(仅参与人)
     */
    private Integer assignedCaseCount;

    /**
     * 已执行用例数(仅参与人)
     */
    private Integer executedCaseCount;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 创建人ID(谁添加的成员)
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
