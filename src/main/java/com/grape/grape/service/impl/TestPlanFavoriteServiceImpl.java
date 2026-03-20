package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanFavorite;
import com.grape.grape.mapper.TestPlanFavoriteMapper;
import com.grape.grape.service.TestPlanFavoriteService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试计划收藏表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanFavoriteServiceImpl extends ServiceImpl<TestPlanFavoriteMapper, TestPlanFavorite> implements TestPlanFavoriteService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanFavoriteServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanFavorite> listByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .orderBy("sort_order asc, created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanFavorite> listByUserIdAndType(Long userId, Integer favoriteType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ? and favorite_type = ?", userId, favoriteType)
                .orderBy("sort_order asc, created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanFavorite> listByUserIdAndGroup(Long userId, String groupName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ? and group_name = ?", userId, groupName)
                .orderBy("sort_order asc, created_at desc");
        return list(queryWrapper);
    }

    @Override
    public boolean isFavorited(Long userId, Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ? and plan_id = ?", userId, planId);
        return count(queryWrapper) > 0;
    }

    @Override
    public Page<TestPlanFavorite> page(Page<TestPlanFavorite> page, Long userId, Integer favoriteType, String groupName) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (userId != null) {
            queryWrapper.where("user_id = ?", userId);
        }

        if (favoriteType != null) {
            queryWrapper.and("favorite_type = ?", favoriteType);
        }

        if (groupName != null) {
            queryWrapper.and("group_name = ?", groupName);
        }

        queryWrapper.orderBy("sort_order asc, created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean removeByUserIdAndPlanId(Long userId, Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ? and plan_id = ?", userId, planId);
        return remove(queryWrapper);
    }

    @Override
    public List<String> getFavoriteGroups(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?")
                .select("distinct group_name");
        List<TestPlanFavorite> list = list(queryWrapper);
        return list.stream()
                .map(TestPlanFavorite::getGroupName)
                .filter(group -> group != null && !group.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(TestPlanFavorite testPlanFavorite) {
        // 设置默认值
        if (testPlanFavorite.getFavoriteType() == null) {
            testPlanFavorite.setFavoriteType(1); // 1-个人收藏
        }
        if (testPlanFavorite.getSortOrder() == null) {
            testPlanFavorite.setSortOrder(0);
        }

        // 设置创建时间
        if (testPlanFavorite.getCreatedAt() == null) {
            testPlanFavorite.setCreatedAt(System.currentTimeMillis());
        }

        // 设置用户ID
        if (testPlanFavorite.getUserId() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    Long userId = Long.parseLong(userIdStr);
                    testPlanFavorite.setUserId(userId);
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }
        }

        // 检查是否已收藏
        if (isFavorited(testPlanFavorite.getUserId(), testPlanFavorite.getPlanId())) {
            return false; // 已收藏，不再重复添加
        }

        return super.save(testPlanFavorite);
    }
}
