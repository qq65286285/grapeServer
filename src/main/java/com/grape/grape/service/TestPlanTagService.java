package com.grape.grape.service;

import com.grape.grape.entity.TestPlanTag;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划标签表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanTagService extends MyBaseService<TestPlanTag> {

    /**
     * 根据标签分类查询标签列表
     *
     * @param tagCategory 标签分类
     * @return 标签列表
     */
    List<TestPlanTag> listByTagCategory(Integer tagCategory);

    /**
     * 根据状态查询标签列表
     *
     * @param status 状态
     * @return 标签列表
     */
    List<TestPlanTag> listByStatus(Integer status);

    /**
     * 根据标签名称查询标签
     *
     * @param tagName 标签名称
     * @return 标签
     */
    TestPlanTag getByTagName(String tagName);

    /**
     * 分页查询标签
     *
     * @param page 分页参数
     * @param tagCategory 标签分类（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选，用于搜索标签名称或编码）
     * @return 分页结果
     */
    Page<TestPlanTag> page(Page<TestPlanTag> page, Integer tagCategory, Integer status, String keyword);

    /**
     * 增加使用次数
     *
     * @param id 标签ID
     * @return 是否操作成功
     */
    boolean increaseUseCount(Long id);

    /**
     * 减少使用次数
     *
     * @param id 标签ID
     * @return 是否操作成功
     */
    boolean decreaseUseCount(Long id);

    /**
     * 启用标签
     *
     * @param id 标签ID
     * @return 是否操作成功
     */
    boolean enableTag(Long id);

    /**
     * 禁用标签
     *
     * @param id 标签ID
     * @return 是否操作成功
     */
    boolean disableTag(Long id);

    /**
     * 获取热门标签（按使用次数排序）
     *
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<TestPlanTag> getHotTags(int limit);

    /**
     * 获取所有启用的标签
     *
     * @return 启用的标签列表
     */
    List<TestPlanTag> listAllEnabled();
}