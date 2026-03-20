package com.grape.grape.service;

import com.grape.grape.entity.TestPlanTask;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划任务表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanTaskService extends MyBaseService<TestPlanTask> {

    /**
     * 根据计划ID查询任务列表
     *
     * @param planId 计划ID
     * @return 任务列表
     */
    List<TestPlanTask> listByPlanId(Long planId);

    /**
     * 根据任务类型查询任务列表
     *
     * @param taskType 任务类型
     * @return 任务列表
     */
    List<TestPlanTask> listByTaskType(Integer taskType);

    /**
     * 根据状态查询任务列表
     *
     * @param status 状态
     * @return 任务列表
     */
    List<TestPlanTask> listByStatus(Integer status);

    /**
     * 根据负责人ID查询任务列表
     *
     * @param ownerId 负责人ID
     * @return 任务列表
     */
    List<TestPlanTask> listByOwnerId(Long ownerId);

    /**
     * 根据任务编号查询任务
     *
     * @param taskNo 任务编号
     * @return 任务
     */
    TestPlanTask getByTaskNo(String taskNo);

    /**
     * 分页查询任务
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param taskType 任务类型（可选）
     * @param status 状态（可选）
     * @param ownerId 负责人ID（可选）
     * @param priority 优先级（可选）
     * @return 分页结果
     */
    Page<TestPlanTask> page(Page<TestPlanTask> page, Long planId, Integer taskType, Integer status, Long ownerId, Integer priority);

    /**
     * 更新任务状态
     *
     * @param id 任务ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 更新任务进度
     *
     * @param id 任务ID
     * @param progress 进度
     * @return 是否更新成功
     */
    boolean updateProgress(Long id, Double progress);

    /**
     * 批量删除任务
     *
     * @param ids 任务ID列表
     * @return 删除成功的数量
     */
    int batchDelete(List<Long> ids);

    /**
     * 根据计划ID批量删除任务
     *
     * @param planId 计划ID
     * @return 删除成功的数量
     */
    int deleteByPlanId(Long planId);

    /**
     * 更新任务
     *
     * @param task 任务对象
     * @return 是否更新成功
     */
    boolean update(TestPlanTask task);
}