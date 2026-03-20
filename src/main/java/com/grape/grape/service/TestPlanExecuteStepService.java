package com.grape.grape.service;

import com.grape.grape.entity.TestPlanExecuteStep;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划执行步骤记录表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanExecuteStepService extends MyBaseService<TestPlanExecuteStep> {

    /**
     * 根据执行记录ID查询步骤列表
     *
     * @param executeId 执行记录ID
     * @return 步骤列表
     */
    List<TestPlanExecuteStep> listByExecuteId(Long executeId);

    /**
     * 批量保存执行步骤
     *
     * @param steps 步骤列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<TestPlanExecuteStep> steps);

    /**
     * 根据执行记录ID删除步骤
     *
     * @param executeId 执行记录ID
     * @return 是否删除成功
     */
    boolean removeByExecuteId(Long executeId);

    /**
     * 分页查询执行步骤
     *
     * @param page 分页参数
     * @param executeId 执行记录ID（可选）
     * @return 分页结果
     */
    Page<TestPlanExecuteStep> page(Page<TestPlanExecuteStep> page, Long executeId);
}
