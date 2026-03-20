package com.grape.grape.service;

import com.grape.grape.entity.TestPlanExecuteRecord;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划执行记录表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanExecuteRecordService extends MyBaseService<TestPlanExecuteRecord> {

    /**
     * 根据计划ID查询执行记录列表
     *
     * @param planId 计划ID
     * @return 执行记录列表
     */
    List<TestPlanExecuteRecord> listByPlanId(Long planId);

    /**
     * 根据快照ID查询执行记录列表
     *
     * @param snapshotId 快照ID
     * @return 执行记录列表
     */
    List<TestPlanExecuteRecord> listBySnapshotId(Long snapshotId);

    /**
     * 根据执行编号查询执行记录
     *
     * @param executeNo 执行编号
     * @return 执行记录
     */
    TestPlanExecuteRecord getByExecuteNo(String executeNo);

    /**
     * 根据计划ID和执行状态查询执行记录列表
     *
     * @param planId 计划ID
     * @param executeStatus 执行状态
     * @return 执行记录列表
     */
    List<TestPlanExecuteRecord> listByPlanIdAndStatus(Long planId, Integer executeStatus);

    /**
     * 计算计划的执行统计
     *
     * @param planId 计划ID
     * @return 统计结果
     */
    java.util.Map<String, Object> calculateExecutionStats(Long planId);

    /**
     * 分页查询执行记录
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param snapshotId 快照ID（可选）
     * @param executorId 执行人ID（可选）
     * @param executeStatus 执行状态（可选）
     * @return 分页结果
     */
    Page<TestPlanExecuteRecord> page(Page<TestPlanExecuteRecord> page, Long planId, Long snapshotId, Long executorId, Integer executeStatus);

    /**
     * 审核执行记录
     *
     * @param id 执行记录ID
     * @param isReviewed 是否通过审核
     * @param reviewedBy 审核人ID
     * @param reviewComment 审核意见
     * @return 是否操作成功
     */
    boolean reviewRecord(Long id, Integer isReviewed, Long reviewedBy, String reviewComment);
}
