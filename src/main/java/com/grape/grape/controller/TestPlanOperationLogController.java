package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanOperationLog;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanOperationLogService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划操作日志表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanOperationLog")
public class TestPlanOperationLogController {

    @Autowired
    private TestPlanOperationLogService testPlanOperationLogService;

    /**
     * 新增测试计划操作日志
     *
     * @param testPlanOperationLog 测试计划操作日志
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanOperationLog testPlanOperationLog) {
        boolean saved = testPlanOperationLogService.save(testPlanOperationLog);
        if (saved) {
            return Resp.ok(testPlanOperationLog);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划操作日志
     *
     * @param id 测试计划操作日志ID
     * @return 测试计划操作日志
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanOperationLog testPlanOperationLog = testPlanOperationLogService.getById(id);
        if (testPlanOperationLog != null) {
            return Resp.ok(testPlanOperationLog);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划操作日志
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param operationType 操作类型（可选）
     * @param operationModule 操作模块（可选）
     * @param createdBy 操作人ID（可选）
     * @param startDate 开始时间（可选，毫秒级时间戳）
     * @param endDate 结束时间（可选，毫秒级时间戳）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer operationType,
                    @RequestParam(required = false) String operationModule, @RequestParam(required = false) Long createdBy,
                    @RequestParam(required = false) Long startDate, @RequestParam(required = false) Long endDate) {
        Page<TestPlanOperationLog> result = testPlanOperationLogService.page(Page.of(page, size), planId, operationType, operationModule, createdBy, startDate, endDate);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询操作日志列表
     *
     * @param planId 计划ID
     * @return 操作日志列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanOperationLog> list = testPlanOperationLogService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据操作类型查询操作日志列表
     *
     * @param operationType 操作类型
     * @return 操作日志列表
     */
    @GetMapping("/listByOperationType/{operationType}")
    public Resp listByOperationType(@PathVariable Integer operationType) {
        List<TestPlanOperationLog> list = testPlanOperationLogService.listByOperationType(operationType);
        return Resp.ok(list);
    }

    /**
     * 根据操作模块查询操作日志列表
     *
     * @param operationModule 操作模块
     * @return 操作日志列表
     */
    @GetMapping("/listByOperationModule/{operationModule}")
    public Resp listByOperationModule(@PathVariable String operationModule) {
        List<TestPlanOperationLog> list = testPlanOperationLogService.listByOperationModule(operationModule);
        return Resp.ok(list);
    }

    /**
     * 根据操作人ID查询操作日志列表
     *
     * @param createdBy 操作人ID
     * @return 操作日志列表
     */
    @GetMapping("/listByCreatedBy/{createdBy}")
    public Resp listByCreatedBy(@PathVariable Long createdBy) {
        List<TestPlanOperationLog> list = testPlanOperationLogService.listByCreatedBy(createdBy);
        return Resp.ok(list);
    }

    /**
     * 记录操作日志
     *
     * @param log 操作日志对象
     * @return 操作结果
     */
    @PostMapping("/recordLog")
    public Resp recordLog(@RequestBody TestPlanOperationLog log) {
        boolean recorded = testPlanOperationLogService.recordLog(log);
        if (recorded) {
            return Resp.ok(log);
        } else {
            return Resp.error();
        }
    }

    /**
     * 批量记录操作日志
     *
     * @param logs 操作日志列表
     * @return 记录成功的数量
     */
    @PostMapping("/recordBatchLogs")
    public Resp recordBatchLogs(@RequestBody List<TestPlanOperationLog> logs) {
        int successCount = testPlanOperationLogService.recordBatchLogs(logs);
        return Resp.ok(successCount);
    }

    /**
     * 根据计划ID和操作类型统计操作次数
     *
     * @param planId 计划ID
     * @param operationType 操作类型
     * @return 操作次数
     */
    @GetMapping("/countByPlanIdAndOperationType")
    public Resp countByPlanIdAndOperationType(@RequestParam Long planId, @RequestParam Integer operationType) {
        long count = testPlanOperationLogService.countByPlanIdAndOperationType(planId, operationType);
        return Resp.ok(count);
    }

    /**
     * 获取计划的最近操作日志
     *
     * @param planId 计划ID
     * @param limit 限制数量
     * @return 最近操作日志列表
     */
    @GetMapping("/getRecentLogs")
    public Resp getRecentLogs(@RequestParam Long planId, @RequestParam(defaultValue = "10") int limit) {
        List<TestPlanOperationLog> list = testPlanOperationLogService.getRecentLogs(planId, limit);
        return Resp.ok(list);
    }
}