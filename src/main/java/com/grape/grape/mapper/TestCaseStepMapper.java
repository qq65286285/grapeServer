package com.grape.grape.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.grape.grape.entity.TestCaseStep;

import java.util.List;

/**
 * 测试用例步骤表 Mapper 接口。
 *
 * @author Administrator
 * @since 2026-02-05
 */
public interface TestCaseStepMapper extends BaseMapper<TestCaseStep> {

    /**
     * 根据测试用例ID获取步骤列表
     * @param testCaseId 测试用例ID
     * @return 步骤列表
     */
    default List<TestCaseStep> getByTestCaseId(Integer testCaseId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(TestCaseStep::getTestCaseId, testCaseId);
        queryWrapper.orderBy(TestCaseStep::getStepNumber);
        return selectListByQuery(queryWrapper);
    }

    /**
     * 根据测试用例ID删除所有步骤
     * @param testCaseId 测试用例ID
     * @return 删除的记录数
     */
    default int deleteByTestCaseId(Integer testCaseId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(TestCaseStep::getTestCaseId, testCaseId);
        return deleteByQuery(queryWrapper);
    }
}
