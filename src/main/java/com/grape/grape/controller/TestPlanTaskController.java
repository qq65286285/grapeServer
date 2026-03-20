package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanTask;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanTaskService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划任务表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanTask")
public class TestPlanTaskController {

    @Autowired
    private TestPlanTaskService testPlanTaskService;

    /**
     * 新增测试计划任务
     *
     * @param testPlanTask 测试计划任务
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanTask testPlanTask) {
        boolean saved = testPlanTaskService.save(testPlanTask);
        if (saved) {
            return Resp.ok(testPlanTask);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划任务
     *
     * @param id 测试计划任务ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanTaskService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划任务
     *
     * @param id 测试计划任务ID
     * @return 测试计划任务
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanTask testPlanTask = testPlanTaskService.getById(id);
        if (testPlanTask != null) {
            return Resp.ok(testPlanTask);
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新测试计划任务
     *
     * @param testPlanTask 测试计划任务
     * @return 更新结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanTask testPlanTask) {
        boolean updated = testPlanTaskService.update(testPlanTask);
        if (updated) {
            return Resp.ok(testPlanTask);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划任务
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param taskType 任务类型（可选）
     * @param status 状态（可选）
     * @param ownerId 负责人ID（可选）
     * @param priority 优先级（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer taskType,
                    @RequestParam(required = false) Integer status, @RequestParam(required = false) Long ownerId,
                    @RequestParam(required = false) Integer priority) {
        Page<TestPlanTask> result = testPlanTaskService.page(Page.of(page, size), planId, taskType, status, ownerId, priority);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询任务列表
     *
     * @param planId 计划ID
     * @return 任务列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanTask> list = testPlanTaskService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据任务类型查询任务列表
     *
     * @param taskType 任务类型
     * @return 任务列表
     */
    @GetMapping("/listByTaskType/{taskType}")
    public Resp listByTaskType(@PathVariable Integer taskType) {
        List<TestPlanTask> list = testPlanTaskService.listByTaskType(taskType);
        return Resp.ok(list);
    }

    /**
     * 根据状态查询任务列表
     *
     * @param status 状态
     * @return 任务列表
     */
    @GetMapping("/listByStatus/{status}")
    public Resp listByStatus(@PathVariable Integer status) {
        List<TestPlanTask> list = testPlanTaskService.listByStatus(status);
        return Resp.ok(list);
    }

    /**
     * 根据负责人ID查询任务列表
     *
     * @param ownerId 负责人ID
     * @return 任务列表
     */
    @GetMapping("/listByOwnerId/{ownerId}")
    public Resp listByOwnerId(@PathVariable Long ownerId) {
        List<TestPlanTask> list = testPlanTaskService.listByOwnerId(ownerId);
        return Resp.ok(list);
    }

    /**
     * 根据任务编号查询任务
     *
     * @param taskNo 任务编号
     * @return 任务
     */
    @GetMapping("/getByTaskNo/{taskNo}")
    public Resp getByTaskNo(@PathVariable String taskNo) {
        TestPlanTask task = testPlanTaskService.getByTaskNo(taskNo);
        if (task != null) {
            return Resp.ok(task);
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新任务状态
     *
     * @param id 任务ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/updateStatus/{id}")
    public Resp updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean updated = testPlanTaskService.updateStatus(id, status);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新任务进度
     *
     * @param id 任务ID
     * @param progress 进度
     * @return 更新结果
     */
    @PutMapping("/updateProgress/{id}")
    public Resp updateProgress(@PathVariable Long id, @RequestParam Double progress) {
        boolean updated = testPlanTaskService.updateProgress(id, progress);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 批量删除任务
     *
     * @param ids 任务ID列表
     * @return 删除成功的数量
     */
    @DeleteMapping("/batchDelete")
    public Resp batchDelete(@RequestParam List<Long> ids) {
        int successCount = testPlanTaskService.batchDelete(ids);
        return Resp.ok(successCount);
    }

    /**
     * 根据计划ID批量删除任务
     *
     * @param planId 计划ID
     * @return 删除成功的数量
     */
    @DeleteMapping("/deleteByPlanId/{planId}")
    public Resp deleteByPlanId(@PathVariable Long planId) {
        int successCount = testPlanTaskService.deleteByPlanId(planId);
        return Resp.ok(successCount);
    }
}