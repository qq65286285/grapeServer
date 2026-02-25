package com.grape.grape.service;

import com.mybatisflex.core.service.IService;
import com.grape.grape.entity.TestCaseStep;

import java.util.List;

/**
 * 测试用例步骤表 服务层。
 *
 * @author Administrator
 * @since 2026-02-05
 */
public interface TestCaseStepService extends IService<TestCaseStep> {

    /**
     * 根据测试用例ID获取步骤列表
     * @param testCaseId 测试用例ID
     * @return 步骤列表
     */
    List<TestCaseStep> getByTestCaseId(Integer testCaseId);

    /**
     * 保存测试用例步骤列表
     * @param testCaseId 测试用例ID
     * @param steps 步骤列表
     * @return 保存结果
     */
    boolean saveSteps(Integer testCaseId, List<TestCaseStep> steps);

    /**
     * 删除测试用例的所有步骤
     * @param testCaseId 测试用例ID
     * @return 删除结果
     */
    boolean removeByTestCaseId(Integer testCaseId);
}
