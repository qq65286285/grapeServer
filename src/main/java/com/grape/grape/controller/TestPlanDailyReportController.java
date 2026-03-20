package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanDailyReport;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanDailyReportService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 测试计划日报表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanDailyReport")
public class TestPlanDailyReportController {

    @Autowired
    private TestPlanDailyReportService testPlanDailyReportService;

    /**
     * 新增测试计划日报
     *
     * @param testPlanDailyReport 测试计划日报
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanDailyReport testPlanDailyReport) {
        boolean saved = testPlanDailyReportService.save(testPlanDailyReport);
        if (saved) {
            return Resp.ok(testPlanDailyReport);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划日报
     *
     * @param testPlanDailyReport 测试计划日报
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanDailyReport testPlanDailyReport) {
        boolean updated = testPlanDailyReportService.updateById(testPlanDailyReport);
        if (updated) {
            return Resp.ok(testPlanDailyReport);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划日报
     *
     * @param id 测试计划日报ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanDailyReportService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划日报
     *
     * @param id 测试计划日报ID
     * @return 测试计划日报
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanDailyReport testPlanDailyReport = testPlanDailyReportService.getById(id);
        if (testPlanDailyReport != null) {
            return Resp.ok(testPlanDailyReport);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划日报
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId,
                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Page<TestPlanDailyReport> result = testPlanDailyReportService.page(Page.of(page, size), planId, startDate, endDate);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询日报列表
     *
     * @param planId 计划ID
     * @return 日报列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanDailyReport> list = testPlanDailyReportService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和日期范围查询日报列表
     *
     * @param planId 计划ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日报列表
     */
    @GetMapping("/listByPlanIdAndDateRange")
    public Resp listByPlanIdAndDateRange(@RequestParam Long planId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<TestPlanDailyReport> list = testPlanDailyReportService.listByPlanIdAndDateRange(planId, startDate, endDate);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和报告日期查询日报
     *
     * @param planId 计划ID
     * @param reportDate 报告日期
     * @return 日报
     */
    @GetMapping("/getByPlanIdAndReportDate")
    public Resp getByPlanIdAndReportDate(@RequestParam Long planId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date reportDate) {
        TestPlanDailyReport report = testPlanDailyReportService.getByPlanIdAndReportDate(planId, reportDate);
        if (report != null) {
            return Resp.ok(report);
        } else {
            return Resp.error();
        }
    }
}
