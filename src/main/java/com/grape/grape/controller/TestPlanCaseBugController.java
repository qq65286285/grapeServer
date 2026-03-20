package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanCaseBug;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanCaseBugService;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试计划用例缺陷关联表 控制层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlanCaseBug")
public class TestPlanCaseBugController {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCaseBugController.class);

    @Autowired
    private TestPlanCaseBugService testPlanCaseBugService;

    /**
     * 添加测试计划用例缺陷关联
     *
     * @param testPlanCaseBug 测试计划用例缺陷关联
     * @return 添加结果
     */
    @PostMapping("save")
    public Resp save(@RequestBody TestPlanCaseBug testPlanCaseBug) {
        boolean result = testPlanCaseBugService.save(testPlanCaseBug);
        if (result) {
            return Resp.ok("添加成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键删除测试计划用例缺陷关联
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean result = testPlanCaseBugService.removeById(id);
        if (result) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键更新测试计划用例缺陷关联
     *
     * @param testPlanCaseBug 测试计划用例缺陷关联
     * @return 更新结果
     */
    @PutMapping("update")
    public Resp update(@RequestBody TestPlanCaseBug testPlanCaseBug) {
        boolean result = testPlanCaseBugService.updateById(testPlanCaseBug);
        if (result) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键查询测试计划用例缺陷关联
     *
     * @param id 主键
     * @return 测试计划用例缺陷关联
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Long id) {
        TestPlanCaseBug testPlanCaseBug = testPlanCaseBugService.getById(id);
        if (testPlanCaseBug != null) {
            return Resp.ok(testPlanCaseBug);
        } else {
            return Resp.info(404, "测试计划用例缺陷关联不存在");
        }
    }

    /**
     * 查询所有测试计划用例缺陷关联
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        List<TestPlanCaseBug> testPlanCaseBugs = testPlanCaseBugService.list();
        return Resp.ok(testPlanCaseBugs);
    }

    /**
     * 批量删除测试计划用例缺陷关联
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Long> ids) {
        boolean result = testPlanCaseBugService.removeByIds(ids);
        if (result) {
            return Resp.ok("批量删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据条件查询测试计划用例缺陷关联
     */
    @PostMapping("listByCondition")
    public Resp listByCondition(@RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");

        // 构建查询条件
        if (params != null) {
            if (params.containsKey("planId")) {
                queryWrapper.and("plan_id = ?", params.get("planId"));
            }
            if (params.containsKey("snapshotId")) {
                queryWrapper.and("snapshot_id = ?", params.get("snapshotId"));
            }
            if (params.containsKey("executeRecordId")) {
                queryWrapper.and("execute_record_id = ?", params.get("executeRecordId"));
            }
            if (params.containsKey("bugId")) {
                queryWrapper.and("bug_id = ?", params.get("bugId"));
            }
            if (params.containsKey("bugNumber")) {
                queryWrapper.and("bug_number like ?", "%" + params.get("bugNumber") + "%");
            }
            if (params.containsKey("bugTitle")) {
                queryWrapper.and("bug_title like ?", "%" + params.get("bugTitle") + "%");
            }
            if (params.containsKey("bugSeverity")) {
                queryWrapper.and("bug_severity = ?", params.get("bugSeverity"));
            }
            if (params.containsKey("bugPriority")) {
                queryWrapper.and("bug_priority = ?", params.get("bugPriority"));
            }
            if (params.containsKey("bugStatus")) {
                queryWrapper.and("bug_status = ?", params.get("bugStatus"));
            }
            if (params.containsKey("verifyStatus")) {
                queryWrapper.and("verify_status = ?", params.get("verifyStatus"));
            }
            if (params.containsKey("isBlocking")) {
                queryWrapper.and("is_blocking = ?", params.get("isBlocking"));
            }
        }

        List<TestPlanCaseBug> testPlanCaseBugs = testPlanCaseBugService.list(queryWrapper);
        return Resp.ok(testPlanCaseBugs);
    }

    /**
     * 根据计划ID查询缺陷列表
     */
    @GetMapping("listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanCaseBug> testPlanCaseBugs = testPlanCaseBugService.listByPlanId(planId);
        return Resp.ok(testPlanCaseBugs);
    }

    /**
     * 根据快照ID查询缺陷列表
     */
    @GetMapping("listBySnapshotId/{snapshotId}")
    public Resp listBySnapshotId(@PathVariable Long snapshotId) {
        List<TestPlanCaseBug> testPlanCaseBugs = testPlanCaseBugService.listBySnapshotId(snapshotId);
        return Resp.ok(testPlanCaseBugs);
    }

    /**
     * 根据缺陷ID查询关联列表
     */
    @GetMapping("listByBugId/{bugId}")
    public Resp listByBugId(@PathVariable Long bugId) {
        List<TestPlanCaseBug> testPlanCaseBugs = testPlanCaseBugService.listByBugId(bugId);
        return Resp.ok(testPlanCaseBugs);
    }

    /**
     * 验证缺陷
     */
    @PostMapping("verify")
    public Resp verify(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("id") && params.containsKey("verifyStatus")) {
            Long id = Long.parseLong(params.get("id").toString());
            Integer verifyStatus = Integer.parseInt(params.get("verifyStatus").toString());
            Long verifiedBy = params.containsKey("verifiedBy") ? Long.parseLong(params.get("verifiedBy").toString()) : null;
            String verifyRemark = params.containsKey("verifyRemark") ? params.get("verifyRemark").toString() : null;

            boolean result = testPlanCaseBugService.verifyBug(id, verifyStatus, verifiedBy, verifyRemark);
            if (result) {
                return Resp.ok("验证成功");
            } else {
                return Resp.error();
            }
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含id和verifyStatus");
        }
    }
}
