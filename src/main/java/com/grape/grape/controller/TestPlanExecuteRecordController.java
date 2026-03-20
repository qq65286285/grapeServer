package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanExecuteRecord;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanExecuteRecordService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试计划执行记录表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanExecuteRecord")
public class TestPlanExecuteRecordController {

    @Autowired
    private TestPlanExecuteRecordService testPlanExecuteRecordService;

    /**
     * 新增测试计划执行记录
     *
     * @param testPlanExecuteRecord 测试计划执行记录
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanExecuteRecord testPlanExecuteRecord) {
        boolean saved = testPlanExecuteRecordService.save(testPlanExecuteRecord);
        if (saved) {
            return Resp.ok(testPlanExecuteRecord);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划执行记录
     *
     * @param testPlanExecuteRecord 测试计划执行记录
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanExecuteRecord testPlanExecuteRecord) {
        boolean updated = testPlanExecuteRecordService.updateById(testPlanExecuteRecord);
        if (updated) {
            return Resp.ok(testPlanExecuteRecord);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划执行记录
     *
     * @param id 测试计划执行记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanExecuteRecord record = testPlanExecuteRecordService.getById(id);
        if (record != null) {
            record.setIsDeleted(1);
            boolean deleted = testPlanExecuteRecordService.updateById(record);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划执行记录
     *
     * @param id 测试计划执行记录ID
     * @return 测试计划执行记录
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanExecuteRecord testPlanExecuteRecord = testPlanExecuteRecordService.getById(id);
        if (testPlanExecuteRecord != null && testPlanExecuteRecord.getIsDeleted() == 0) {
            return Resp.ok(testPlanExecuteRecord);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划执行记录
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param snapshotId 快照ID（可选）
     * @param executorId 执行人ID（可选）
     * @param executeStatus 执行状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Long snapshotId,
                    @RequestParam(required = false) Long executorId, @RequestParam(required = false) Integer executeStatus) {
        Page<TestPlanExecuteRecord> result = testPlanExecuteRecordService.page(Page.of(page, size), planId, snapshotId, executorId, executeStatus);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询执行记录列表
     *
     * @param planId 计划ID
     * @return 执行记录列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanExecuteRecord> list = testPlanExecuteRecordService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据快照ID查询执行记录列表
     *
     * @param snapshotId 快照ID
     * @return 执行记录列表
     */
    @GetMapping("/listBySnapshotId/{snapshotId}")
    public Resp listBySnapshotId(@PathVariable Long snapshotId) {
        List<TestPlanExecuteRecord> list = testPlanExecuteRecordService.listBySnapshotId(snapshotId);
        return Resp.ok(list);
    }

    /**
     * 根据执行编号查询执行记录
     *
     * @param executeNo 执行编号
     * @return 执行记录
     */
    @GetMapping("/getByExecuteNo/{executeNo}")
    public Resp getByExecuteNo(@PathVariable String executeNo) {
        TestPlanExecuteRecord record = testPlanExecuteRecordService.getByExecuteNo(executeNo);
        if (record != null) {
            return Resp.ok(record);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据计划ID和执行状态查询执行记录列表
     *
     * @param planId 计划ID
     * @param executeStatus 执行状态
     * @return 执行记录列表
     */
    @GetMapping("/listByPlanIdAndStatus")
    public Resp listByPlanIdAndStatus(@RequestParam Long planId, @RequestParam Integer executeStatus) {
        List<TestPlanExecuteRecord> list = testPlanExecuteRecordService.listByPlanIdAndStatus(planId, executeStatus);
        return Resp.ok(list);
    }

    /**
     * 计算计划的执行统计
     *
     * @param planId 计划ID
     * @return 统计结果
     */
    @GetMapping("/calculateExecutionStats/{planId}")
    public Resp calculateExecutionStats(@PathVariable Long planId) {
        Map<String, Object> stats = testPlanExecuteRecordService.calculateExecutionStats(planId);
        return Resp.ok(stats);
    }

    /**
     * 审核执行记录
     *
     * @param id 执行记录ID
     * @param isReviewed 是否通过审核
     * @param reviewedBy 审核人ID
     * @param reviewComment 审核意见
     * @return 操作结果
     */
    @PostMapping("/reviewRecord/{id}")
    public Resp reviewRecord(@PathVariable Long id, @RequestParam Integer isReviewed,
                            @RequestParam Long reviewedBy, @RequestParam String reviewComment) {
        boolean reviewed = testPlanExecuteRecordService.reviewRecord(id, isReviewed, reviewedBy, reviewComment);
        if (reviewed) {
            return Resp.ok("审核成功");
        } else {
            return Resp.error();
        }
    }
}
