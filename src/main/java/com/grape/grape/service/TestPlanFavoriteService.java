package com.grape.grape.service;

import com.grape.grape.entity.TestPlanFavorite;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划收藏表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanFavoriteService extends MyBaseService<TestPlanFavorite> {

    /**
     * 根据用户ID查询收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<TestPlanFavorite> listByUserId(Long userId);

    /**
     * 根据用户ID和收藏类型查询收藏列表
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @return 收藏列表
     */
    List<TestPlanFavorite> listByUserIdAndType(Long userId, Integer favoriteType);

    /**
     * 根据用户ID和分组名称查询收藏列表
     *
     * @param userId 用户ID
     * @param groupName 分组名称
     * @return 收藏列表
     */
    List<TestPlanFavorite> listByUserIdAndGroup(Long userId, String groupName);

    /**
     * 检查用户是否已收藏计划
     *
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long userId, Long planId);

    /**
     * 分页查询收藏
     *
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param favoriteType 收藏类型（可选）
     * @param groupName 分组名称（可选）
     * @return 分页结果
     */
    Page<TestPlanFavorite> page(Page<TestPlanFavorite> page, Long userId, Integer favoriteType, String groupName);

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 是否操作成功
     */
    boolean removeByUserIdAndPlanId(Long userId, Long planId);

    /**
     * 获取用户的收藏分组列表
     *
     * @param userId 用户ID
     * @return 分组列表
     */
    List<String> getFavoriteGroups(Long userId);
}
