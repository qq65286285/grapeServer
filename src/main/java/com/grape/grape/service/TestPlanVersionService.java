package com.grape.grape.service;

import com.grape.grape.entity.TestPlanVersion;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划版本表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanVersionService extends MyBaseService<TestPlanVersion> {

    /**
     * 根据计划ID查询版本列表
     *
     * @param planId 计划ID
     * @return 版本列表
     */
    List<TestPlanVersion> listByPlanId(Long planId);

    /**
     * 根据版本类型查询版本列表
     *
     * @param versionType 版本类型
     * @return 版本列表
     */
    List<TestPlanVersion> listByVersionType(Integer versionType);

    /**
     * 根据是否基线版本查询版本列表
     *
     * @param isBaseline 是否基线版本
     * @return 版本列表
     */
    List<TestPlanVersion> listByIsBaseline(Integer isBaseline);

    /**
     * 根据版本号查询版本
     *
     * @param versionNo 版本号
     * @return 版本
     */
    TestPlanVersion getByVersionNo(String versionNo);

    /**
     * 分页查询版本
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param versionType 版本类型（可选）
     * @param isBaseline 是否基线版本（可选）
     * @param changeType 变更类型（可选）
     * @return 分页结果
     */
    Page<TestPlanVersion> page(Page<TestPlanVersion> page, Long planId, Integer versionType, Integer isBaseline, Integer changeType);

    /**
     * 获取计划的最新版本
     *
     * @param planId 计划ID
     * @return 最新版本
     */
    TestPlanVersion getLatestVersion(Long planId);

    /**
     * 获取计划的所有基线版本
     *
     * @param planId 计划ID
     * @return 基线版本列表
     */
    List<TestPlanVersion> listBaselineVersions(Long planId);

    /**
     * 将版本设置为基线
     *
     * @param id 版本ID
     * @param baselineName 基线名称
     * @return 是否设置成功
     */
    boolean setAsBaseline(Long id, String baselineName);

    /**
     * 批量删除版本
     *
     * @param ids 版本ID列表
     * @return 删除成功的数量
     */
    int batchDelete(List<Long> ids);

    /**
     * 根据计划ID批量删除版本
     *
     * @param planId 计划ID
     * @return 删除成功的数量
     */
    int deleteByPlanId(Long planId);

    /**
     * 对比两个版本
     *
     * @param versionId1 版本ID1
     * @param versionId2 版本ID2
     * @return 对比结果
     */
    String compareVersions(Long versionId1, Long versionId2);

    /**
     * 回滚到指定版本
     *
     * @param planId 计划ID
     * @param versionId 版本ID
     * @return 回滚是否成功
     */
    boolean rollbackToVersion(Long planId, Long versionId);
}