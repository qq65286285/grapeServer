package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanTag;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanTagService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划标签表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanTag")
public class TestPlanTagController {

    @Autowired
    private TestPlanTagService testPlanTagService;

    /**
     * 新增测试计划标签
     *
     * @param testPlanTag 测试计划标签
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanTag testPlanTag) {
        boolean saved = testPlanTagService.save(testPlanTag);
        if (saved) {
            return Resp.ok(testPlanTag);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划标签
     *
     * @param testPlanTag 测试计划标签
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanTag testPlanTag) {
        boolean updated = testPlanTagService.updateById(testPlanTag);
        if (updated) {
            return Resp.ok(testPlanTag);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划标签
     *
     * @param id 测试计划标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanTag tag = testPlanTagService.getById(id);
        if (tag != null) {
            tag.setIsDeleted(1);
            boolean deleted = testPlanTagService.updateById(tag);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划标签
     *
     * @param id 测试计划标签ID
     * @return 测试计划标签
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanTag testPlanTag = testPlanTagService.getById(id);
        if (testPlanTag != null && testPlanTag.getIsDeleted() == 0) {
            return Resp.ok(testPlanTag);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划标签
     *
     * @param page 页码
     * @param size 每页大小
     * @param tagCategory 标签分类（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选，用于搜索标签名称或编码）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Integer tagCategory, @RequestParam(required = false) Integer status,
                    @RequestParam(required = false) String keyword) {
        Page<TestPlanTag> result = testPlanTagService.page(Page.of(page, size), tagCategory, status, keyword);
        return Resp.ok(result);
    }

    /**
     * 根据标签分类查询标签列表
     *
     * @param tagCategory 标签分类
     * @return 标签列表
     */
    @GetMapping("/listByTagCategory/{tagCategory}")
    public Resp listByTagCategory(@PathVariable Integer tagCategory) {
        List<TestPlanTag> list = testPlanTagService.listByTagCategory(tagCategory);
        return Resp.ok(list);
    }

    /**
     * 根据状态查询标签列表
     *
     * @param status 状态
     * @return 标签列表
     */
    @GetMapping("/listByStatus/{status}")
    public Resp listByStatus(@PathVariable Integer status) {
        List<TestPlanTag> list = testPlanTagService.listByStatus(status);
        return Resp.ok(list);
    }

    /**
     * 根据标签名称查询标签
     *
     * @param tagName 标签名称
     * @return 标签
     */
    @GetMapping("/getByTagName/{tagName}")
    public Resp getByTagName(@PathVariable String tagName) {
        TestPlanTag tag = testPlanTagService.getByTagName(tagName);
        if (tag != null) {
            return Resp.ok(tag);
        } else {
            return Resp.error();
        }
    }

    /**
     * 增加使用次数
     *
     * @param id 标签ID
     * @return 操作结果
     */
    @PutMapping("/increaseUseCount/{id}")
    public Resp increaseUseCount(@PathVariable Long id) {
        boolean updated = testPlanTagService.increaseUseCount(id);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 减少使用次数
     *
     * @param id 标签ID
     * @return 操作结果
     */
    @PutMapping("/decreaseUseCount/{id}")
    public Resp decreaseUseCount(@PathVariable Long id) {
        boolean updated = testPlanTagService.decreaseUseCount(id);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 启用标签
     *
     * @param id 标签ID
     * @return 操作结果
     */
    @PutMapping("/enableTag/{id}")
    public Resp enableTag(@PathVariable Long id) {
        boolean updated = testPlanTagService.enableTag(id);
        if (updated) {
            return Resp.ok("启用成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 禁用标签
     *
     * @param id 标签ID
     * @return 操作结果
     */
    @PutMapping("/disableTag/{id}")
    public Resp disableTag(@PathVariable Long id) {
        boolean updated = testPlanTagService.disableTag(id);
        if (updated) {
            return Resp.ok("禁用成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取热门标签（按使用次数排序）
     *
     * @param limit 限制数量
     * @return 热门标签列表
     */
    @GetMapping("/getHotTags")
    public Resp getHotTags(@RequestParam(defaultValue = "10") int limit) {
        List<TestPlanTag> list = testPlanTagService.getHotTags(limit);
        return Resp.ok(list);
    }

    /**
     * 获取所有启用的标签
     *
     * @return 启用的标签列表
     */
    @GetMapping("/listAllEnabled")
    public Resp listAllEnabled() {
        List<TestPlanTag> list = testPlanTagService.listAllEnabled();
        return Resp.ok(list);
    }
}