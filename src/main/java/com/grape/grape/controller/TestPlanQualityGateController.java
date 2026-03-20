package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanQualityGate;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanQualityGateService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试计划质量门禁表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanQualityGate")
public class TestPlanQualityGateController {

    @Autowired
    private TestPlanQualityGateService testPlanQualityGateService;

    /**
     * 新增测试计划质量门禁
     *
     * @param testPlanQualityGate 测试计划质量门禁
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanQualityGate testPlanQualityGate) {
        boolean saved = testPlanQualityGateService.save(testPlanQualityGate);
        if (saved) {
            return Resp.ok(testPlanQualityGate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划质量门禁
     *
     * @param testPlanQualityGate 测试计划质量门禁
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanQualityGate testPlanQualityGate) {
        boolean updated = testPlanQualityGateService.updateById(testPlanQualityGate);
        if (updated) {
            return Resp.ok(testPlanQualityGate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划质量门禁
     *
     * @param id 测试计划质量门禁ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanQualityGateService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划质量门禁
     *
     * @param id 测试计划质量门禁ID
     * @return 测试计划质量门禁
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanQualityGate testPlanQualityGate = testPlanQualityGateService.getById(id);
        if (testPlanQualityGate != null) {
            return Resp.ok(testPlanQualityGate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划质量门禁
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param gateType 门禁类型（可选）
     * @param gateStatus 门禁状态（可选）
     * @param isMandatory 是否强制（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer gateType,
                    @RequestParam(required = false) Integer gateStatus, @RequestParam(required = false) Integer isMandatory) {
        Page<TestPlanQualityGate> result = testPlanQualityGateService.page(Page.of(page, size), planId, gateType, gateStatus, isMandatory);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询质量门禁列表
     *
     * @param planId 计划ID
     * @return 质量门禁列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanQualityGate> list = testPlanQualityGateService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据门禁状态查询质量门禁列表
     *
     * @param gateStatus 门禁状态
     * @return 质量门禁列表
     */
    @GetMapping("/listByGateStatus/{gateStatus}")
    public Resp listByGateStatus(@PathVariable Integer gateStatus) {
        List<TestPlanQualityGate> list = testPlanQualityGateService.listByGateStatus(gateStatus);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和门禁类型查询质量门禁
     *
     * @param planId 计划ID
     * @param gateType 门禁类型
     * @return 质量门禁
     */
    @GetMapping("/getByPlanIdAndGateType")
    public Resp getByPlanIdAndGateType(@RequestParam Long planId, @RequestParam Integer gateType) {
        TestPlanQualityGate gate = testPlanQualityGateService.getByPlanIdAndGateType(planId, gateType);
        if (gate != null) {
            return Resp.ok(gate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 检查质量门禁
     *
     * @param id 门禁ID
     * @param currentValue 当前值
     * @return 检查结果
     */
    @PostMapping("/checkQualityGate/{id}")
    public Resp checkQualityGate(@PathVariable Long id, @RequestParam BigDecimal currentValue) {
        boolean passed = testPlanQualityGateService.checkQualityGate(id, currentValue);
        return Resp.ok(passed);
    }

    /**
     * 批量检查计划的所有质量门禁
     *
     * @param planId 计划ID
     * @return 检查结果（true-所有门禁通过，false-存在未通过门禁）
     */
    @PostMapping("/checkAllQualityGates/{planId}")
    public Resp checkAllQualityGates(@PathVariable Long planId) {
        boolean allPassed = testPlanQualityGateService.checkAllQualityGates(planId);
        return Resp.ok(allPassed);
    }

    /**
     * 更新门禁状态
     *
     * @param id 门禁ID
     * @param gateStatus 门禁状态
     * @param currentValue 当前值
     * @return 操作结果
     */
    @PutMapping("/updateGateStatus/{id}")
    public Resp updateGateStatus(@PathVariable Long id, @RequestParam Integer gateStatus, @RequestParam(required = false) BigDecimal currentValue) {
        boolean updated = testPlanQualityGateService.updateGateStatus(id, gateStatus, currentValue);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的质量门禁状态统计
     *
     * @param planId 计划ID
     * @return 状态统计信息
     */
    @GetMapping("/getQualityGateStatusStats/{planId}")
    public Resp getQualityGateStatusStats(@PathVariable Long planId) {
        TestPlanQualityGateService.QualityGateStatusStats stats = testPlanQualityGateService.getQualityGateStatusStats(planId);
        return Resp.ok(stats);
    }
}