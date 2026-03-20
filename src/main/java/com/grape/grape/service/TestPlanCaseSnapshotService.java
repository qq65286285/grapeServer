package com.grape.grape.service;

import com.grape.grape.entity.TestPlanCaseSnapshot;

/**
 * 计划用例快照表-用例绑定到计划时的版本快照 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanCaseSnapshotService extends MyBaseService<TestPlanCaseSnapshot> {

    /**
     * 根据计划ID查询快照列表
     *
     * @param planId 计划ID
     * @return 快照列表
     */
    java.util.List<TestPlanCaseSnapshot> listByPlanId(Long planId);

    /**
     * 根据原用例ID查询快照列表
     *
     * @param originalCaseId 原用例ID
     * @return 快照列表
     */
    java.util.List<TestPlanCaseSnapshot> listByOriginalCaseId(Integer originalCaseId);

    /**
     * 根据执行状态查询快照列表
     *
     * @param planId       计划ID
     * @param executeStatus 执行状态
     * @return 快照列表
     */
    java.util.List<TestPlanCaseSnapshot> listByExecuteStatus(Long planId, Integer executeStatus);

    /**
     * 根据模块查询快照列表
     *
     * @param planId 计划ID
     * @param module 模块
     * @return 快照列表
     */
    java.util.List<TestPlanCaseSnapshot> listByModule(Long planId, String module);

    /**
     * 根据执行人查询快照列表
     *
     * @param planId     计划ID
     * @param executorId 执行人ID
     * @return 快照列表
     */
    java.util.List<TestPlanCaseSnapshot> listByExecutorId(Long planId, Long executorId);

    /**
     * 更新执行状态
     *
     * @param id           快照ID
     * @param executeStatus 执行状态
     * @param executorId   执行人ID
     * @return 是否更新成功
     */
    boolean updateExecuteStatus(Long id, Integer executeStatus, Long executorId);
}
