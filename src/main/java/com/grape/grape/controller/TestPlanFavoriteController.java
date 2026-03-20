package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanFavorite;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanFavoriteService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划收藏表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanFavorite")
public class TestPlanFavoriteController {

    @Autowired
    private TestPlanFavoriteService testPlanFavoriteService;

    /**
     * 新增测试计划收藏
     *
     * @param testPlanFavorite 测试计划收藏
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanFavorite testPlanFavorite) {
        boolean saved = testPlanFavoriteService.save(testPlanFavorite);
        if (saved) {
            return Resp.ok(testPlanFavorite);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划收藏
     *
     * @param testPlanFavorite 测试计划收藏
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanFavorite testPlanFavorite) {
        boolean updated = testPlanFavoriteService.updateById(testPlanFavorite);
        if (updated) {
            return Resp.ok(testPlanFavorite);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划收藏
     *
     * @param id 测试计划收藏ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanFavoriteService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划收藏
     *
     * @param id 测试计划收藏ID
     * @return 测试计划收藏
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanFavorite testPlanFavorite = testPlanFavoriteService.getById(id);
        if (testPlanFavorite != null) {
            return Resp.ok(testPlanFavorite);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划收藏
     *
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID（可选）
     * @param favoriteType 收藏类型（可选）
     * @param groupName 分组名称（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long userId, @RequestParam(required = false) Integer favoriteType,
                    @RequestParam(required = false) String groupName) {
        Page<TestPlanFavorite> result = testPlanFavoriteService.page(Page.of(page, size), userId, favoriteType, groupName);
        return Resp.ok(result);
    }

    /**
     * 根据用户ID查询收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    @GetMapping("/listByUserId/{userId}")
    public Resp listByUserId(@PathVariable Long userId) {
        List<TestPlanFavorite> list = testPlanFavoriteService.listByUserId(userId);
        return Resp.ok(list);
    }

    /**
     * 根据用户ID和收藏类型查询收藏列表
     *
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @return 收藏列表
     */
    @GetMapping("/listByUserIdAndType")
    public Resp listByUserIdAndType(@RequestParam Long userId, @RequestParam Integer favoriteType) {
        List<TestPlanFavorite> list = testPlanFavoriteService.listByUserIdAndType(userId, favoriteType);
        return Resp.ok(list);
    }

    /**
     * 根据用户ID和分组名称查询收藏列表
     *
     * @param userId 用户ID
     * @param groupName 分组名称
     * @return 收藏列表
     */
    @GetMapping("/listByUserIdAndGroup")
    public Resp listByUserIdAndGroup(@RequestParam Long userId, @RequestParam String groupName) {
        List<TestPlanFavorite> list = testPlanFavoriteService.listByUserIdAndGroup(userId, groupName);
        return Resp.ok(list);
    }

    /**
     * 检查用户是否已收藏计划
     *
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 是否已收藏
     */
    @GetMapping("/isFavorited")
    public Resp isFavorited(@RequestParam Long userId, @RequestParam Long planId) {
        boolean favorited = testPlanFavoriteService.isFavorited(userId, planId);
        return Resp.ok(favorited);
    }

    /**
     * 取消收藏
     *
     * @param userId 用户ID
     * @param planId 计划ID
     * @return 操作结果
     */
    @DeleteMapping("/removeByUserIdAndPlanId")
    public Resp removeByUserIdAndPlanId(@RequestParam Long userId, @RequestParam Long planId) {
        boolean removed = testPlanFavoriteService.removeByUserIdAndPlanId(userId, planId);
        if (removed) {
            return Resp.ok("取消收藏成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取用户的收藏分组列表
     *
     * @param userId 用户ID
     * @return 分组列表
     */
    @GetMapping("/getFavoriteGroups/{userId}")
    public Resp getFavoriteGroups(@PathVariable Long userId) {
        List<String> groups = testPlanFavoriteService.getFavoriteGroups(userId);
        return Resp.ok(groups);
    }
}
