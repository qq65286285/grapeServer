package com.grape.grape.service;

import com.grape.grape.entity.TestPlanCaseSnapshotStep;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 测试计划用例快照步骤表 服务层。
 *
 * @author Administrator
 * @since 2026-03-24
 */
public interface TestPlanCaseSnapshotStepService extends IService<TestPlanCaseSnapshotStep> {

    /**
     * 根据测试计划用例快照ID获取步骤列表
     * @param snapshotId 测试计划用例快照ID
     * @return 步骤列表
     */
    List<TestPlanCaseSnapshotStep> getBySnapshotId(Long snapshotId);

    /**
     * 保存测试计划用例快照步骤列表
     * @param snapshotId 测试计划用例快照ID
     * @param steps 步骤列表
     * @return 保存结果
     */
    boolean saveSteps(Long snapshotId, List<TestPlanCaseSnapshotStep> steps);

    /**
     * 删除测试计划用例快照的所有步骤
     * @param snapshotId 测试计划用例快照ID
     * @return 删除结果
     */
    boolean removeBySnapshotId(Long snapshotId);
}