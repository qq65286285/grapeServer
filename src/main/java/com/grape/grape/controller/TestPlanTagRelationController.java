package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanTagRelation;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanTagRelationService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划标签关联表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanTagRelation")
public class TestPlanTagRelationController {

    @Autowired
    private TestPlanTagRelationService testPlanTagRelationService;

    /**
     * 新增测试计划标签关联
     *
     * @param testPlanTagRelation 测试计划标签关联
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanTagRelation testPlanTagRelation) {
        boolean saved = testPlanTagRelationService.save(testPlanTagRelation);
        if (saved) {
            return Resp.ok(testPlanTagRelation);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划标签关联
     *
     * @param id 测试计划标签关联ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanTagRelationService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划标签关联
     *
     * @param id 测试计划标签关联ID
     * @return 测试计划标签关联
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanTagRelation testPlanTagRelation = testPlanTagRelationService.getById(id);
        if (testPlanTagRelation != null) {
            return Resp.ok(testPlanTagRelation);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划标签关联
     *
     * @param page 页码
     * @param size 每页大小
     * @param tagId 标签ID（可选）
     * @param relationType 关联类型（可选）
     * @param planId 计划ID（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long tagId, @RequestParam(required = false) Integer relationType,
                    @RequestParam(required = false) Long planId) {
        Page<TestPlanTagRelation> result = testPlanTagRelationService.page(Page.of(page, size), tagId, relationType, planId);
        return Resp.ok(result);
    }

    /**
     * 根据标签ID查询关联列表
     *
     * @param tagId 标签ID
     * @return 关联列表
     */
    @GetMapping("/listByTagId/{tagId}")
    public Resp listByTagId(@PathVariable Long tagId) {
        List<TestPlanTagRelation> list = testPlanTagRelationService.listByTagId(tagId);
        return Resp.ok(list);
    }

    /**
     * 根据关联类型和关联对象ID查询关联列表
     *
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 关联列表
     */
    @GetMapping("/listByRelation")
    public Resp listByRelation(@RequestParam Integer relationType, @RequestParam Long relationId) {
        List<TestPlanTagRelation> list = testPlanTagRelationService.listByRelation(relationType, relationId);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID查询关联列表
     *
     * @param planId 计划ID
     * @return 关联列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanTagRelation> list = testPlanTagRelationService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 检查标签是否已关联
     *
     * @param tagId 标签ID
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 是否已关联
     */
    @GetMapping("/exists")
    public Resp exists(@RequestParam Long tagId, @RequestParam Integer relationType, @RequestParam Long relationId) {
        boolean exists = testPlanTagRelationService.existsByTagAndRelation(tagId, relationType, relationId);
        return Resp.ok(exists);
    }

    /**
     * 批量添加标签关联
     *
     * @param tagIds 标签ID列表
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @param planId 计划ID
     * @return 添加成功的数量
     */
    @PostMapping("/batchAdd")
    public Resp batchAdd(@RequestParam List<Long> tagIds, @RequestParam Integer relationType, @RequestParam Long relationId, @RequestParam Long planId) {
        int successCount = testPlanTagRelationService.batchAddRelations(tagIds, relationType, relationId, planId);
        return Resp.ok(successCount);
    }

    /**
     * 批量删除标签关联
     *
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 删除成功的数量
     */
    @DeleteMapping("/batchDelete")
    public Resp batchDelete(@RequestParam Integer relationType, @RequestParam Long relationId) {
        int successCount = testPlanTagRelationService.batchDeleteRelations(relationType, relationId);
        return Resp.ok(successCount);
    }

    /**
     * 删除指定标签的关联
     *
     * @param tagId 标签ID
     * @param relationType 关联类型
     * @param relationId 关联对象ID
     * @return 是否删除成功
     */
    @DeleteMapping("/deleteRelation")
    public Resp deleteRelation(@RequestParam Long tagId, @RequestParam Integer relationType, @RequestParam Long relationId) {
        boolean deleted = testPlanTagRelationService.deleteRelation(tagId, relationType, relationId);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据标签ID删除所有关联
     *
     * @param tagId 标签ID
     * @return 删除成功的数量
     */
    @DeleteMapping("/deleteByTagId/{tagId}")
    public Resp deleteByTagId(@PathVariable Long tagId) {
        int successCount = testPlanTagRelationService.deleteByTagId(tagId);
        return Resp.ok(successCount);
    }

    /**
     * 根据计划ID删除所有关联
     *
     * @param planId 计划ID
     * @return 删除成功的数量
     */
    @DeleteMapping("/deleteByPlanId/{planId}")
    public Resp deleteByPlanId(@PathVariable Long planId) {
        int successCount = testPlanTagRelationService.deleteByPlanId(planId);
        return Resp.ok(successCount);
    }
}