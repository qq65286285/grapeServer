package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanExecuteStep;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanExecuteStepService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划执行步骤记录表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanExecuteStep")
public class TestPlanExecuteStepController {

    @Autowired
    private TestPlanExecuteStepService testPlanExecuteStepService;

    /**
     * 新增测试计划执行步骤
     *
     * @param testPlanExecuteStep 测试计划执行步骤
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanExecuteStep testPlanExecuteStep) {
        boolean saved = testPlanExecuteStepService.save(testPlanExecuteStep);
        if (saved) {
            return Resp.ok(testPlanExecuteStep);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划执行步骤
     *
     * @param testPlanExecuteStep 测试计划执行步骤
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanExecuteStep testPlanExecuteStep) {
        boolean updated = testPlanExecuteStepService.updateById(testPlanExecuteStep);
        if (updated) {
            return Resp.ok(testPlanExecuteStep);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划执行步骤
     *
     * @param id 测试计划执行步骤ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanExecuteStepService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划执行步骤
     *
     * @param id 测试计划执行步骤ID
     * @return 测试计划执行步骤
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanExecuteStep testPlanExecuteStep = testPlanExecuteStepService.getById(id);
        if (testPlanExecuteStep != null) {
            return Resp.ok(testPlanExecuteStep);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划执行步骤
     *
     * @param page 页码
     * @param size 每页大小
     * @param executeId 执行记录ID（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long executeId) {
        Page<TestPlanExecuteStep> result = testPlanExecuteStepService.page(Page.of(page, size), executeId);
        return Resp.ok(result);
    }

    /**
     * 根据执行记录ID查询步骤列表
     *
     * @param executeId 执行记录ID
     * @return 步骤列表
     */
    @GetMapping("/listByExecuteId/{executeId}")
    public Resp listByExecuteId(@PathVariable Long executeId) {
        List<TestPlanExecuteStep> list = testPlanExecuteStepService.listByExecuteId(executeId);
        return Resp.ok(list);
    }

    /**
     * 批量保存执行步骤
     *
     * @param steps 步骤列表
     * @return 保存结果
     */
    @PostMapping("/saveBatch")
    public Resp saveBatch(@RequestBody List<TestPlanExecuteStep> steps) {
        boolean saved = testPlanExecuteStepService.saveBatch(steps);
        if (saved) {
            return Resp.ok("保存成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据执行记录ID删除步骤
     *
     * @param executeId 执行记录ID
     * @return 删除结果
     */
    @DeleteMapping("/removeByExecuteId/{executeId}")
    public Resp removeByExecuteId(@PathVariable Long executeId) {
        boolean deleted = testPlanExecuteStepService.removeByExecuteId(executeId);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }
}
