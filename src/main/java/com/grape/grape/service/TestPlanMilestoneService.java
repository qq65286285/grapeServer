package com.grape.grape.service;

import com.grape.grape.entity.TestPlanMilestone;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划里程碑表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanMilestoneService extends MyBaseService<TestPlanMilestone> {

    /**
     * 根据计划ID查询里程碑列表
     *
     * @param planId 计划ID
     * @return 里程碑列表
     */
    List<TestPlanMilestone> listByPlanId(Long planId);

    /**
     * 根据计划ID和里程碑类型查询里程碑列表
     *
     * @param planId 计划ID
     * @param milestoneType 里程碑类型
     * @return 里程碑列表
     */
    List<TestPlanMilestone> listByPlanIdAndType(Long planId, Integer milestoneType);

    /**
     * 根据计划ID和状态查询里程碑列表
     *
     * @param planId 计划ID
     * @param status 状态
     * @return 里程碑列表
     */
    List<TestPlanMilestone> listByPlanIdAndStatus(Long planId, Integer status);

    /**
     * 根据父里程碑ID查询子里程碑列表
     *
     * @param parentId 父里程碑ID
     * @return 子里程碑列表
     */
    List<TestPlanMilestone> listByParentId(Long parentId);

    /**
     * 分页查询里程碑
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param milestoneType 里程碑类型（可选）
     * @param status 状态（可选）
     * @param ownerId 负责人ID（可选）
     * @return 分页结果
     */
    Page<TestPlanMilestone> page(Page<TestPlanMilestone> page, Long planId, Integer milestoneType, Integer status, Long ownerId);

    /**
     * 更新里程碑状态
     *
     * @param id 里程碑ID
     * @param status 状态
     * @param actualDate 实际完成日期（可选）
     * @return 是否操作成功
     */
    boolean updateStatus(Long id, Integer status, Long actualDate);

    /**
     * 更新里程碑完成率
     *
     * @param id 里程碑ID
     * @param completeRate 完成率
     * @return 是否操作成功
     */
    boolean updateCompleteRate(Long id, java.math.BigDecimal completeRate);

    /**
     * 完成里程碑
     *
     * @param id 里程碑ID
     * @param actualMetrics 实际指标
     * @return 是否操作成功
     */
    boolean completeMilestone(Long id, String actualMetrics);

    /**
     * 获取计划的里程碑树
     *
     * @param planId 计划ID
     * @return 里程碑树
     */
    List<TestPlanMilestone> getMilestoneTree(Long planId);
}
