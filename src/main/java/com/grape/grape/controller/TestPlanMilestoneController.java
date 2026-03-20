package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanMilestone;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanMilestoneService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试计划里程碑表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanMilestone")
public class TestPlanMilestoneController {

    @Autowired
    private TestPlanMilestoneService testPlanMilestoneService;

    /**
     * 新增测试计划里程碑
     *
     * @param testPlanMilestone 测试计划里程碑
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanMilestone testPlanMilestone) {
        boolean saved = testPlanMilestoneService.save(testPlanMilestone);
        if (saved) {
            return Resp.ok(testPlanMilestone);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划里程碑
     *
     * @param testPlanMilestone 测试计划里程碑
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanMilestone testPlanMilestone) {
        boolean updated = testPlanMilestoneService.updateById(testPlanMilestone);
        if (updated) {
            return Resp.ok(testPlanMilestone);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划里程碑
     *
     * @param id 测试计划里程碑ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanMilestone milestone = testPlanMilestoneService.getById(id);
        if (milestone != null) {
            milestone.setIsDeleted(1);
            boolean deleted = testPlanMilestoneService.updateById(milestone);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划里程碑
     *
     * @param id 测试计划里程碑ID
     * @return 测试计划里程碑
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanMilestone testPlanMilestone = testPlanMilestoneService.getById(id);
        if (testPlanMilestone != null && testPlanMilestone.getIsDeleted() == 0) {
            return Resp.ok(testPlanMilestone);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划里程碑
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param milestoneType 里程碑类型（可选）
     * @param status 状态（可选）
     * @param ownerId 负责人ID（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer milestoneType,
                    @RequestParam(required = false) Integer status, @RequestParam(required = false) Long ownerId) {
        Page<TestPlanMilestone> result = testPlanMilestoneService.page(Page.of(page, size), planId, milestoneType, status, ownerId);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询里程碑列表
     *
     * @param planId 计划ID
     * @return 里程碑列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanMilestone> list = testPlanMilestoneService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和里程碑类型查询里程碑列表
     *
     * @param planId 计划ID
     * @param milestoneType 里程碑类型
     * @return 里程碑列表
     */
    @GetMapping("/listByPlanIdAndType")
    public Resp listByPlanIdAndType(@RequestParam Long planId, @RequestParam Integer milestoneType) {
        List<TestPlanMilestone> list = testPlanMilestoneService.listByPlanIdAndType(planId, milestoneType);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和状态查询里程碑列表
     *
     * @param planId 计划ID
     * @param status 状态
     * @return 里程碑列表
     */
    @GetMapping("/listByPlanIdAndStatus")
    public Resp listByPlanIdAndStatus(@RequestParam Long planId, @RequestParam Integer status) {
        List<TestPlanMilestone> list = testPlanMilestoneService.listByPlanIdAndStatus(planId, status);
        return Resp.ok(list);
    }

    /**
     * 根据父里程碑ID查询子里程碑列表
     *
     * @param parentId 父里程碑ID
     * @return 子里程碑列表
     */
    @GetMapping("/listByParentId/{parentId}")
    public Resp listByParentId(@PathVariable Long parentId) {
        List<TestPlanMilestone> list = testPlanMilestoneService.listByParentId(parentId);
        return Resp.ok(list);
    }

    /**
     * 更新里程碑状态
     *
     * @param id 里程碑ID
     * @param status 状态
     * @param actualDate 实际完成日期（可选）
     * @return 操作结果
     */
    @PutMapping("/updateStatus/{id}")
    public Resp updateStatus(@PathVariable Long id, @RequestParam Integer status,
                           @RequestParam(required = false) Long actualDate) {
        boolean updated = testPlanMilestoneService.updateStatus(id, status, actualDate);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新里程碑完成率
     *
     * @param id 里程碑ID
     * @param completeRate 完成率
     * @return 操作结果
     */
    @PutMapping("/updateCompleteRate/{id}")
    public Resp updateCompleteRate(@PathVariable Long id, @RequestParam BigDecimal completeRate) {
        boolean updated = testPlanMilestoneService.updateCompleteRate(id, completeRate);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 完成里程碑
     *
     * @param id 里程碑ID
     * @param actualMetrics 实际指标
     * @return 操作结果
     */
    @PostMapping("/completeMilestone/{id}")
    public Resp completeMilestone(@PathVariable Long id, @RequestParam String actualMetrics) {
        boolean completed = testPlanMilestoneService.completeMilestone(id, actualMetrics);
        if (completed) {
            return Resp.ok("完成成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的里程碑树
     *
     * @param planId 计划ID
     * @return 里程碑树
     */
    @GetMapping("/getMilestoneTree/{planId}")
    public Resp getMilestoneTree(@PathVariable Long planId) {
        List<TestPlanMilestone> tree = testPlanMilestoneService.getMilestoneTree(planId);
        return Resp.ok(tree);
    }
}
