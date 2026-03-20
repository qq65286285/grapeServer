package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanReport;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanReportService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划报告表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanReport")
public class TestPlanReportController {

    @Autowired
    private TestPlanReportService testPlanReportService;

    /**
     * 新增测试计划报告
     *
     * @param testPlanReport 测试计划报告
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanReport testPlanReport) {
        boolean saved = testPlanReportService.save(testPlanReport);
        if (saved) {
            return Resp.ok(testPlanReport);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划报告
     *
     * @param testPlanReport 测试计划报告
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanReport testPlanReport) {
        boolean updated = testPlanReportService.updateById(testPlanReport);
        if (updated) {
            return Resp.ok(testPlanReport);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划报告
     *
     * @param id 测试计划报告ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanReportService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划报告
     *
     * @param id 测试计划报告ID
     * @return 测试计划报告
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanReport testPlanReport = testPlanReportService.getById(id);
        if (testPlanReport != null && testPlanReport.getIsDeleted() == 0) {
            return Resp.ok(testPlanReport);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划报告
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param reportType 报告类型（可选）
     * @param status 状态（可选）
     * @param approveStatus 审批状态（可选）
     * @param isPublished 是否发布（可选）
     * @param startDate 开始时间（可选，毫秒级时间戳）
     * @param endDate 结束时间（可选，毫秒级时间戳）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer reportType,
                    @RequestParam(required = false) Integer status, @RequestParam(required = false) Integer approveStatus,
                    @RequestParam(required = false) Integer isPublished, @RequestParam(required = false) Long startDate,
                    @RequestParam(required = false) Long endDate) {
        Page<TestPlanReport> result = testPlanReportService.page(Page.of(page, size), planId, reportType, status, approveStatus, isPublished, startDate, endDate);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询报告列表
     *
     * @param planId 计划ID
     * @return 报告列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanReport> list = testPlanReportService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据报告类型查询报告列表
     *
     * @param reportType 报告类型
     * @return 报告列表
     */
    @GetMapping("/listByReportType/{reportType}")
    public Resp listByReportType(@PathVariable Integer reportType) {
        List<TestPlanReport> list = testPlanReportService.listByReportType(reportType);
        return Resp.ok(list);
    }

    /**
     * 根据状态查询报告列表
     *
     * @param status 状态
     * @return 报告列表
     */
    @GetMapping("/listByStatus/{status}")
    public Resp listByStatus(@PathVariable Integer status) {
        List<TestPlanReport> list = testPlanReportService.listByStatus(status);
        return Resp.ok(list);
    }

    /**
     * 根据审批状态查询报告列表
     *
     * @param approveStatus 审批状态
     * @return 报告列表
     */
    @GetMapping("/listByApproveStatus/{approveStatus}")
    public Resp listByApproveStatus(@PathVariable Integer approveStatus) {
        List<TestPlanReport> list = testPlanReportService.listByApproveStatus(approveStatus);
        return Resp.ok(list);
    }

    /**
     * 根据报告编号查询报告
     *
     * @param reportNo 报告编号
     * @return 报告
     */
    @GetMapping("/getByReportNo/{reportNo}")
    public Resp getByReportNo(@PathVariable String reportNo) {
        TestPlanReport report = testPlanReportService.getByReportNo(reportNo);
        if (report != null) {
            return Resp.ok(report);
        } else {
            return Resp.error();
        }
    }

    /**
     * 生成报告编号
     *
     * @param planId 计划ID
     * @return 报告编号
     */
    @GetMapping("/generateReportNo")
    public Resp generateReportNo(@RequestParam Long planId) {
        String reportNo = testPlanReportService.generateReportNo(planId);
        return Resp.ok(reportNo);
    }

    /**
     * 提交审批
     *
     * @param id 报告ID
     * @return 操作结果
     */
    @PostMapping("/submitForApproval/{id}")
    public Resp submitForApproval(@PathVariable Long id) {
        boolean submitted = testPlanReportService.submitForApproval(id);
        if (submitted) {
            return Resp.ok("提交成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 审批报告
     *
     * @param id 报告ID
     * @param approveStatus 审批状态
     * @param approveRemark 审批意见
     * @return 操作结果
     */
    @PostMapping("/approveReport/{id}")
    public Resp approveReport(@PathVariable Long id, @RequestParam Integer approveStatus, @RequestParam(required = false) String approveRemark) {
        boolean approved = testPlanReportService.approveReport(id, approveStatus, approveRemark);
        if (approved) {
            return Resp.ok("审批成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 发布报告
     *
     * @param id 报告ID
     * @return 操作结果
     */
    @PostMapping("/publishReport/{id}")
    public Resp publishReport(@PathVariable Long id) {
        boolean published = testPlanReportService.publishReport(id);
        if (published) {
            return Resp.ok("发布成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 归档报告
     *
     * @param id 报告ID
     * @return 操作结果
     */
    @PostMapping("/archiveReport/{id}")
    public Resp archiveReport(@PathVariable Long id) {
        boolean archived = testPlanReportService.archiveReport(id);
        if (archived) {
            return Resp.ok("归档成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的最新报告
     *
     * @param planId 计划ID
     * @return 最新报告
     */
    @GetMapping("/getLatestReport/{planId}")
    public Resp getLatestReport(@PathVariable Long planId) {
        TestPlanReport report = testPlanReportService.getLatestReport(planId);
        if (report != null) {
            return Resp.ok(report);
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的报告统计
     *
     * @param planId 计划ID
     * @return 报告统计信息
     */
    @GetMapping("/getReportStats/{planId}")
    public Resp getReportStats(@PathVariable Long planId) {
        TestPlanReportService.ReportStats stats = testPlanReportService.getReportStats(planId);
        return Resp.ok(stats);
    }
}