package com.grape.grape.service;

import com.grape.grape.entity.TestPlanOperationLog;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划操作日志表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanOperationLogService extends MyBaseService<TestPlanOperationLog> {

    /**
     * 根据计划ID查询操作日志列表
     *
     * @param planId 计划ID
     * @return 操作日志列表
     */
    List<TestPlanOperationLog> listByPlanId(Long planId);

    /**
     * 根据操作类型查询操作日志列表
     *
     * @param operationType 操作类型
     * @return 操作日志列表
     */
    List<TestPlanOperationLog> listByOperationType(Integer operationType);

    /**
     * 根据操作模块查询操作日志列表
     *
     * @param operationModule 操作模块
     * @return 操作日志列表
     */
    List<TestPlanOperationLog> listByOperationModule(String operationModule);

    /**
     * 根据操作人ID查询操作日志列表
     *
     * @param createdBy 操作人ID
     * @return 操作日志列表
     */
    List<TestPlanOperationLog> listByCreatedBy(Long createdBy);

    /**
     * 分页查询操作日志
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param operationType 操作类型（可选）
     * @param operationModule 操作模块（可选）
     * @param createdBy 操作人ID（可选）
     * @param startDate 开始时间（可选，毫秒级时间戳）
     * @param endDate 结束时间（可选，毫秒级时间戳）
     * @return 分页结果
     */
    Page<TestPlanOperationLog> page(Page<TestPlanOperationLog> page, Long planId, Integer operationType, String operationModule, Long createdBy, Long startDate, Long endDate);

    /**
     * 记录操作日志
     *
     * @param log 操作日志对象
     * @return 是否记录成功
     */
    boolean recordLog(TestPlanOperationLog log);

    /**
     * 批量记录操作日志
     *
     * @param logs 操作日志列表
     * @return 记录成功的数量
     */
    int recordBatchLogs(List<TestPlanOperationLog> logs);

    /**
     * 根据计划ID和操作类型统计操作次数
     *
     * @param planId 计划ID
     * @param operationType 操作类型
     * @return 操作次数
     */
    long countByPlanIdAndOperationType(Long planId, Integer operationType);

    /**
     * 获取计划的最近操作日志
     *
     * @param planId 计划ID
     * @param limit 限制数量
     * @return 最近操作日志列表
     */
    List<TestPlanOperationLog> getRecentLogs(Long planId, int limit);
}