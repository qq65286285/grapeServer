package com.grape.grape.service;

import com.grape.grape.entity.TestPlanMember;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划成员表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanMemberService extends MyBaseService<TestPlanMember> {

    /**
     * 根据计划ID查询成员列表
     *
     * @param planId 计划ID
     * @return 成员列表
     */
    List<TestPlanMember> listByPlanId(Long planId);

    /**
     * 根据计划ID和角色类型查询成员列表
     *
     * @param planId 计划ID
     * @param roleType 角色类型
     * @return 成员列表
     */
    List<TestPlanMember> listByPlanIdAndRoleType(Long planId, Integer roleType);

    /**
     * 根据用户ID查询参与的计划列表
     *
     * @param userId 用户ID
     * @return 成员列表
     */
    List<TestPlanMember> listByUserId(Long userId);

    /**
     * 根据计划ID和用户ID查询成员信息
     *
     * @param planId 计划ID
     * @param userId 用户ID
     * @return 成员信息
     */
    TestPlanMember getByPlanIdAndUserId(Long planId, Long userId);

    /**
     * 分页查询成员
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param userId 用户ID（可选）
     * @param roleType 角色类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<TestPlanMember> page(Page<TestPlanMember> page, Long planId, Long userId, Integer roleType, Integer status);

    /**
     * 审批成员
     *
     * @param id 成员ID
     * @param approveStatus 审批状态
     * @param approveBy 审批人ID
     * @param approveRemark 审批意见
     * @return 是否操作成功
     */
    boolean approveMember(Long id, Integer approveStatus, Long approveBy, String approveRemark);

    /**
     * 移除成员
     *
     * @param id 成员ID
     * @return 是否操作成功
     */
    boolean removeMember(Long id);

    /**
     * 更新成员的用例执行统计
     *
     * @param id 成员ID
     * @param executedCaseCount 已执行用例数
     * @return 是否操作成功
     */
    boolean updateExecutedCaseCount(Long id, Integer executedCaseCount);

    /**
     * 获取计划的审批人列表（按审批顺序）
     *
     * @param planId 计划ID
     * @return 审批人列表
     */
    List<TestPlanMember> listApproversByPlanId(Long planId);
}
