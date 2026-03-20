package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanVersion;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanVersionService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划版本表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanVersion")
public class TestPlanVersionController {

    @Autowired
    private TestPlanVersionService testPlanVersionService;

    /**
     * 新增测试计划版本
     *
     * @param testPlanVersion 测试计划版本
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanVersion testPlanVersion) {
        boolean saved = testPlanVersionService.save(testPlanVersion);
        if (saved) {
            return Resp.ok(testPlanVersion);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划版本
     *
     * @param id 测试计划版本ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanVersionService.removeById(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划版本
     *
     * @param id 测试计划版本ID
     * @return 测试计划版本
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanVersion testPlanVersion = testPlanVersionService.getById(id);
        if (testPlanVersion != null) {
            return Resp.ok(testPlanVersion);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划版本
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param versionType 版本类型（可选）
     * @param isBaseline 是否基线版本（可选）
     * @param changeType 变更类型（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer versionType,
                    @RequestParam(required = false) Integer isBaseline, @RequestParam(required = false) Integer changeType) {
        Page<TestPlanVersion> result = testPlanVersionService.page(Page.of(page, size), planId, versionType, isBaseline, changeType);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询版本列表
     *
     * @param planId 计划ID
     * @return 版本列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanVersion> list = testPlanVersionService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据版本类型查询版本列表
     *
     * @param versionType 版本类型
     * @return 版本列表
     */
    @GetMapping("/listByVersionType/{versionType}")
    public Resp listByVersionType(@PathVariable Integer versionType) {
        List<TestPlanVersion> list = testPlanVersionService.listByVersionType(versionType);
        return Resp.ok(list);
    }

    /**
     * 根据是否基线版本查询版本列表
     *
     * @param isBaseline 是否基线版本
     * @return 版本列表
     */
    @GetMapping("/listByIsBaseline/{isBaseline}")
    public Resp listByIsBaseline(@PathVariable Integer isBaseline) {
        List<TestPlanVersion> list = testPlanVersionService.listByIsBaseline(isBaseline);
        return Resp.ok(list);
    }

    /**
     * 根据版本号查询版本
     *
     * @param versionNo 版本号
     * @return 版本
     */
    @GetMapping("/getByVersionNo/{versionNo}")
    public Resp getByVersionNo(@PathVariable String versionNo) {
        TestPlanVersion version = testPlanVersionService.getByVersionNo(versionNo);
        if (version != null) {
            return Resp.ok(version);
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的最新版本
     *
     * @param planId 计划ID
     * @return 最新版本
     */
    @GetMapping("/getLatestVersion/{planId}")
    public Resp getLatestVersion(@PathVariable Long planId) {
        TestPlanVersion version = testPlanVersionService.getLatestVersion(planId);
        if (version != null) {
            return Resp.ok(version);
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的所有基线版本
     *
     * @param planId 计划ID
     * @return 基线版本列表
     */
    @GetMapping("/listBaselineVersions/{planId}")
    public Resp listBaselineVersions(@PathVariable Long planId) {
        List<TestPlanVersion> list = testPlanVersionService.listBaselineVersions(planId);
        return Resp.ok(list);
    }

    /**
     * 将版本设置为基线
     *
     * @param id 版本ID
     * @param baselineName 基线名称
     * @return 设置结果
     */
    @PutMapping("/setAsBaseline/{id}")
    public Resp setAsBaseline(@PathVariable Long id, @RequestParam String baselineName) {
        boolean success = testPlanVersionService.setAsBaseline(id, baselineName);
        if (success) {
            return Resp.ok("设置成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 批量删除版本
     *
     * @param ids 版本ID列表
     * @return 删除成功的数量
     */
    @DeleteMapping("/batchDelete")
    public Resp batchDelete(@RequestParam List<Long> ids) {
        int successCount = testPlanVersionService.batchDelete(ids);
        return Resp.ok(successCount);
    }

    /**
     * 根据计划ID批量删除版本
     *
     * @param planId 计划ID
     * @return 删除成功的数量
     */
    @DeleteMapping("/deleteByPlanId/{planId}")
    public Resp deleteByPlanId(@PathVariable Long planId) {
        int successCount = testPlanVersionService.deleteByPlanId(planId);
        return Resp.ok(successCount);
    }

    /**
     * 对比两个版本
     *
     * @param versionId1 版本ID1
     * @param versionId2 版本ID2
     * @return 对比结果
     */
    @GetMapping("/compareVersions")
    public Resp compareVersions(@RequestParam Long versionId1, @RequestParam Long versionId2) {
        String result = testPlanVersionService.compareVersions(versionId1, versionId2);
        return Resp.ok(result);
    }

    /**
     * 回滚到指定版本
     *
     * @param planId 计划ID
     * @param versionId 版本ID
     * @return 回滚结果
     */
    @PostMapping("/rollbackToVersion")
    public Resp rollbackToVersion(@RequestParam Long planId, @RequestParam Long versionId) {
        boolean success = testPlanVersionService.rollbackToVersion(planId, versionId);
        if (success) {
            return Resp.ok("回滚成功");
        } else {
            return Resp.error();
        }
    }
}