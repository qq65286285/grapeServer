package com.grape.grape.service;

import com.grape.grape.entity.TestPlanTaskAssign;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划任务分配表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanTaskAssignService extends MyBaseService<TestPlanTaskAssign> {

    /**
     * 根据任务ID查询分配列表
     *
     * @param taskId 任务ID
     * @return 分配列表
     */
    List<TestPlanTaskAssign> listByTaskId(Long taskId);

    /**
     * 根据用户ID查询分配列表
     *
     * @param userId 用户ID
     * @return 分配列表
     */
    List<TestPlanTaskAssign> listByUserId(String userId);

    /**
     * 根据分配类型查询分配列表
     *
     * @param assignType 分配类型
     * @return 分配列表
     */
    List<TestPlanTaskAssign> listByAssignType(Integer assignType);

    /**
     * 根据任务ID和分配类型查询分配列表
     *
     * @param taskId 任务ID
     * @param assignType 分配类型
     * @return 分配列表
     */
    List<TestPlanTaskAssign> listByTaskIdAndAssignType(Long taskId, Integer assignType);

    /**
     * 分页查询分配
     *
     * @param page 分页参数
     * @param taskId 任务ID（可选）
     * @param userId 用户ID（可选）
     * @param assignType 分配类型（可选）
     * @return 分页结果
     */
    Page<TestPlanTaskAssign> page(Page<TestPlanTaskAssign> page, Long taskId, String userId, Integer assignType);

    /**
     * 批量添加任务分配
     *
     * @param taskId 任务ID
     * @param userIds 用户ID列表
     * @param assignType 分配类型
     * @param workload 工作量
     * @param assignedBy 分配人ID
     * @return 添加成功的数量
     */
    int batchAddAssigns(Long taskId, List<String> userIds, Integer assignType, Double workload, String assignedBy);

    /**
     * 批量删除任务分配
     *
     * @param taskId 任务ID
     * @return 删除成功的数量
     */
    int batchDeleteByTaskId(Long taskId);

    /**
     * 根据任务ID和用户ID删除分配
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteByTaskIdAndUserId(Long taskId, String userId);

    /**
     * 根据任务ID和分配类型删除分配
     *
     * @param taskId 任务ID
     * @param assignType 分配类型
     * @return 删除成功的数量
     */
    int deleteByTaskIdAndAssignType(Long taskId, Integer assignType);

    /**
     * 更新工作量
     *
     * @param id 分配ID
     * @param workload 工作量
     * @return 是否更新成功
     */
    boolean updateWorkload(Long id, Double workload);
}