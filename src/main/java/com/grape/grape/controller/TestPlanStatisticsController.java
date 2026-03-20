package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanStatistics;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanStatisticsService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 测试计划统计表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanStatistics")
public class TestPlanStatisticsController {

    @Autowired
    private TestPlanStatisticsService testPlanStatisticsService;

    /**
     * 新增测试计划统计
     *
     * @param testPlanStatistics 测试计划统计
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanStatistics testPlanStatistics) {
        boolean saved = testPlanStatisticsService.save(testPlanStatistics);
        if (saved) {
            return Resp.ok(testPlanStatistics);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划统计
     *
     * @param testPlanStatistics 测试计划统计
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanStatistics testPlanStatistics) {
        boolean updated = testPlanStatisticsService.updateById(testPlanStatistics);
        if (updated) {
            return Resp.ok(testPlanStatistics);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划统计
     *
     * @param id 测试计划统计ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanStatistics statistics = testPlanStatisticsService.getById(id);
        if (statistics != null) {
            statistics.setIsDeleted(1);
            boolean deleted = testPlanStatisticsService.updateById(statistics);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划统计
     *
     * @param id 测试计划统计ID
     * @return 测试计划统计
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanStatistics testPlanStatistics = testPlanStatisticsService.getById(id);
        if (testPlanStatistics != null && testPlanStatistics.getIsDeleted() == 0) {
            return Resp.ok(testPlanStatistics);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划统计
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param statType 统计类型（可选）
     * @param qualityLevel 质量等级（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer statType,
                    @RequestParam(required = false) String qualityLevel, @RequestParam(required = false) Date startDate,
                    @RequestParam(required = false) Date endDate) {
        Page<TestPlanStatistics> result = testPlanStatisticsService.page(Page.of(page, size), planId, statType, qualityLevel, startDate, endDate);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询统计列表
     *
     * @param planId 计划ID
     * @return 统计列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanStatistics> list = testPlanStatisticsService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和统计类型查询统计列表
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @return 统计列表
     */
    @GetMapping("/listByPlanIdAndStatType")
    public Resp listByPlanIdAndStatType(@RequestParam Long planId, @RequestParam Integer statType) {
        List<TestPlanStatistics> list = testPlanStatisticsService.listByPlanIdAndStatType(planId, statType);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和统计日期查询统计
     *
     * @param planId 计划ID
     * @param statDate 统计日期
     * @param statType 统计类型
     * @return 统计信息
     */
    @GetMapping("/getByPlanIdAndDateAndType")
    public Resp getByPlanIdAndDateAndType(@RequestParam Long planId, @RequestParam Date statDate, @RequestParam Integer statType) {
        TestPlanStatistics statistics = testPlanStatisticsService.getByPlanIdAndDateAndType(planId, statDate, statType);
        if (statistics != null) {
            return Resp.ok(statistics);
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的最新统计
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @return 最新统计信息
     */
    @GetMapping("/getLatestStatistics")
    public Resp getLatestStatistics(@RequestParam Long planId, @RequestParam Integer statType) {
        TestPlanStatistics statistics = testPlanStatisticsService.getLatestStatistics(planId, statType);
        if (statistics != null) {
            return Resp.ok(statistics);
        } else {
            return Resp.error();
        }
    }

    /**
     * 生成每日统计
     *
     * @param planId 计划ID
     * @param statDate 统计日期
     * @return 生成的统计信息
     */
    @PostMapping("/generateDailyStatistics")
    public Resp generateDailyStatistics(@RequestParam Long planId, @RequestParam Date statDate) {
        TestPlanStatistics statistics = testPlanStatisticsService.generateDailyStatistics(planId, statDate);
        return Resp.ok(statistics);
    }

    /**
     * 生成实时统计
     *
     * @param planId 计划ID
     * @return 生成的统计信息
     */
    @PostMapping("/generateRealTimeStatistics/{planId}")
    public Resp generateRealTimeStatistics(@PathVariable Long planId) {
        TestPlanStatistics statistics = testPlanStatisticsService.generateRealTimeStatistics(planId);
        return Resp.ok(statistics);
    }

    /**
     * 获取计划的统计趋势
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @param days 天数
     * @return 统计趋势列表
     */
    @GetMapping("/getStatisticsTrend")
    public Resp getStatisticsTrend(@RequestParam Long planId, @RequestParam Integer statType, @RequestParam(defaultValue = "30") int days) {
        List<TestPlanStatistics> list = testPlanStatisticsService.getStatisticsTrend(planId, statType, days);
        return Resp.ok(list);
    }

    /**
     * 获取计划的质量评分趋势
     *
     * @param planId 计划ID
     * @param statType 统计类型
     * @param days 天数
     * @return 质量评分趋势列表
     */
    @GetMapping("/getQualityScoreTrend")
    public Resp getQualityScoreTrend(@RequestParam Long planId, @RequestParam Integer statType, @RequestParam(defaultValue = "30") int days) {
        List<TestPlanStatistics> list = testPlanStatisticsService.getQualityScoreTrend(planId, statType, days);
        return Resp.ok(list);
    }

    /**
     * 计算质量评分
     *
     * @param statistics 统计信息
     * @return 质量评分
     */
    @PostMapping("/calculateQualityScore")
    public Resp calculateQualityScore(@RequestBody TestPlanStatistics statistics) {
        double score = testPlanStatisticsService.calculateQualityScore(statistics);
        return Resp.ok(score);
    }

    /**
     * 根据质量评分获取质量等级
     *
     * @param qualityScore 质量评分
     * @return 质量等级
     */
    @GetMapping("/getQualityLevel")
    public Resp getQualityLevel(@RequestParam double qualityScore) {
        String level = testPlanStatisticsService.getQualityLevel(qualityScore);
        return Resp.ok(level);
    }
}