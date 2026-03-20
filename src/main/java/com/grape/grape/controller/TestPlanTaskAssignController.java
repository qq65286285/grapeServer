package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanTaskAssign;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanTaskAssignService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划任务分配表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanTaskAssign")
public class TestPlanTaskAssignController {

    @Autowired
    private TestPlanTaskAssignService testPlanTaskAssignService;

    /**
     * 新增测试计划任务分配
     *
     * @param testPlanTaskAssign 测试计划任务分配
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanTaskAssign testPlanTaskAssign) {
        boolean saved = testPlanTaskAssignService.save(testPlanTaskAssign);
        if (saved) {
            return Resp.ok(testPlanTaskAssign);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划任务分配
     *
     * @param id 测试计划任务分配ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanTaskAssignService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划任务分配
     *
     * @param id 测试计划任务分配ID
     * @return 测试计划任务分配
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanTaskAssign testPlanTaskAssign = testPlanTaskAssignService.getById(id);
        if (testPlanTaskAssign != null) {
            return Resp.ok(testPlanTaskAssign);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划任务分配
     *
     * @param page 页码
     * @param size 每页大小
     * @param taskId 任务ID（可选）
     * @param userId 用户ID（可选）
     * @param assignType 分配类型（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long taskId, @RequestParam(required = false) Long userId,
                    @RequestParam(required = false) Integer assignType) {
        Page<TestPlanTaskAssign> result = testPlanTaskAssignService.page(Page.of(page, size), taskId, userId, assignType);
        return Resp.ok(result);
    }

    /**
     * 根据任务ID查询分配列表
     *
     * @param taskId 任务ID
     * @return 分配列表
     */
    @GetMapping("/listByTaskId/{taskId}")
    public Resp listByTaskId(@PathVariable Long taskId) {
        List<TestPlanTaskAssign> list = testPlanTaskAssignService.listByTaskId(taskId);
        return Resp.ok(list);
    }

    /**
     * 根据用户ID查询分配列表
     *
     * @param userId 用户ID
     * @return 分配列表
     */
    @GetMapping("/listByUserId/{userId}")
    public Resp listByUserId(@PathVariable Long userId) {
        List<TestPlanTaskAssign> list = testPlanTaskAssignService.listByUserId(userId);
        return Resp.ok(list);
    }

    /**
     * 根据分配类型查询分配列表
     *
     * @param assignType 分配类型
     * @return 分配列表
     */
    @GetMapping("/listByAssignType/{assignType}")
    public Resp listByAssignType(@PathVariable Integer assignType) {
        List<TestPlanTaskAssign> list = testPlanTaskAssignService.listByAssignType(assignType);
        return Resp.ok(list);
    }

    /**
     * 根据任务ID和分配类型查询分配列表
     *
     * @param taskId 任务ID
     * @param assignType 分配类型
     * @return 分配列表
     */
    @GetMapping("/listByTaskIdAndAssignType")
    public Resp listByTaskIdAndAssignType(@RequestParam Long taskId, @RequestParam Integer assignType) {
        List<TestPlanTaskAssign> list = testPlanTaskAssignService.listByTaskIdAndAssignType(taskId, assignType);
        return Resp.ok(list);
    }

    /**
     * 批量添加任务分配
     *
     * @param taskId 任务ID
     * @param userIds 用户ID列表
     * @param assignType 分配类型
     * @param workload 工作量
     * @param assignedBy 分配人ID（可选）
     * @return 添加成功的数量
     */
    @PostMapping("/batchAdd")
    public Resp batchAdd(@RequestParam Long taskId, @RequestParam List<Long> userIds, @RequestParam Integer assignType,
                        @RequestParam(required = false) Double workload, @RequestParam(required = false) Long assignedBy) {
        int successCount = testPlanTaskAssignService.batchAddAssigns(taskId, userIds, assignType, workload, assignedBy);
        return Resp.ok(successCount);
    }

    /**
     * 批量删除任务分配
     *
     * @param taskId 任务ID
     * @return 删除成功的数量
     */
    @DeleteMapping("/batchDeleteByTaskId/{taskId}")
    public Resp batchDeleteByTaskId(@PathVariable Long taskId) {
        int successCount = testPlanTaskAssignService.batchDeleteByTaskId(taskId);
        return Resp.ok(successCount);
    }

    /**
     * 根据任务ID和用户ID删除分配
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @DeleteMapping("/deleteByTaskIdAndUserId")
    public Resp deleteByTaskIdAndUserId(@RequestParam Long taskId, @RequestParam Long userId) {
        boolean deleted = testPlanTaskAssignService.deleteByTaskIdAndUserId(taskId, userId);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据任务ID和分配类型删除分配
     *
     * @param taskId 任务ID
     * @param assignType 分配类型
     * @return 删除成功的数量
     */
    @DeleteMapping("/deleteByTaskIdAndAssignType")
    public Resp deleteByTaskIdAndAssignType(@RequestParam Long taskId, @RequestParam Integer assignType) {
        int successCount = testPlanTaskAssignService.deleteByTaskIdAndAssignType(taskId, assignType);
        return Resp.ok(successCount);
    }

    /**
     * 更新工作量
     *
     * @param id 分配ID
     * @param workload 工作量
     * @return 是否更新成功
     */
    @PutMapping("/updateWorkload/{id}")
    public Resp updateWorkload(@PathVariable Long id, @RequestParam Double workload) {
        boolean updated = testPlanTaskAssignService.updateWorkload(id, workload);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }
}