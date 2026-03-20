package com.grape.grape.service;

import com.grape.grape.entity.TestPlanCaseBug;

/**
 * 测试计划用例缺陷关联表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanCaseBugService extends MyBaseService<TestPlanCaseBug> {

    /**
     * 根据计划ID查询缺陷列表
     *
     * @param planId 计划ID
     * @return 缺陷列表
     */
    java.util.List<TestPlanCaseBug> listByPlanId(Long planId);

    /**
     * 根据快照ID查询缺陷列表
     *
     * @param snapshotId 快照ID
     * @return 缺陷列表
     */
    java.util.List<TestPlanCaseBug> listBySnapshotId(Long snapshotId);

    /**
     * 根据缺陷ID查询关联列表
     *
     * @param bugId 缺陷ID
     * @return 关联列表
     */
    java.util.List<TestPlanCaseBug> listByBugId(Long bugId);

    /**
     * 验证缺陷
     *
     * @param id           关联ID
     * @param verifyStatus 验证状态
     * @param verifiedBy   验证人ID
     * @param verifyRemark 验证备注
     * @return 是否验证成功
     */
    boolean verifyBug(Long id, Integer verifyStatus, Long verifiedBy, String verifyRemark);
}
