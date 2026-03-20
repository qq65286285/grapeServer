package com.grape.grape.service;

import com.grape.grape.entity.TestPlanTagRelation;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划标签关联表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanTagRelationService extends MyBaseService<TestPlanTagRelation> {

    /**
     * 根据标签ID查询关联列表
     *
     * @param tagId 标签ID
     * @return 关联列表
     */
    List<TestPlanTagRelation> listByTagId(Long tagId);

    /**
     * 根据关联类型和关联对象ID查询关联列表
     *
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 关联列表
     */
    List<TestPlanTagRelation> listByRelation(Integer relationType, Long relationId);

    /**
     * 根据计划ID查询关联列表
     *
     * @param planId 计划ID
     * @return 关联列表
     */
    List<TestPlanTagRelation> listByPlanId(Long planId);

    /**
     * 检查标签是否已关联
     *
     * @param tagId 标签ID
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 是否已关联
     */
    boolean existsByTagAndRelation(Long tagId, Integer relationType, Long relationId);

    /**
     * 分页查询关联
     *
     * @param page 分页参数
     * @param tagId 标签ID（可选）
     * @param relationType 关联类型（可选）
     * @param planId 计划ID（可选）
     * @return 分页结果
     */
    Page<TestPlanTagRelation> page(Page<TestPlanTagRelation> page, Long tagId, Integer relationType, Long planId);

    /**
     * 批量添加标签关联
     *
     * @param tagIds 标签ID列表
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @param planId 计划ID
     * @return 添加成功的数量
     */
    int batchAddRelations(List<Long> tagIds, Integer relationType, Long relationId, Long planId);

    /**
     * 批量删除标签关联
     *
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 删除成功的数量
     */
    int batchDeleteRelations(Integer relationType, Long relationId);

    /**
     * 删除指定标签的关联
     *
     * @param tagId 标签ID
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 是否删除成功
     */
    boolean deleteRelation(Long tagId, Integer relationType, Long relationId);

    /**
     * 根据标签ID删除所有关联
     *
     * @param tagId 标签ID
     * @return 删除成功的数量
     */
    int deleteByTagId(Long tagId);

    /**
     * 根据计划ID删除所有关联
     *
     * @param planId 计划ID
     * @return 删除成功的数量
     */
    int deleteByPlanId(Long planId);
}