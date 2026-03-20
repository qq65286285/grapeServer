package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanCaseSnapshot;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanCaseSnapshotService;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 计划用例快照表-用例绑定到计划时的版本快照 控制层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlanCaseSnapshot")
public class TestPlanCaseSnapshotController {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCaseSnapshotController.class);

    @Autowired
    private TestPlanCaseSnapshotService testPlanCaseSnapshotService;

    /**
     * 添加计划用例快照
     *
     * @param testPlanCaseSnapshot 计划用例快照
     * @return 添加结果
     */
    @PostMapping("save")
    public Resp save(@RequestBody TestPlanCaseSnapshot testPlanCaseSnapshot) {
        boolean result = testPlanCaseSnapshotService.save(testPlanCaseSnapshot);
        if (result) {
            return Resp.ok("添加成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键删除计划用例快照
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean result = testPlanCaseSnapshotService.removeById(id);
        if (result) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键更新计划用例快照
     *
     * @param testPlanCaseSnapshot 计划用例快照
     * @return 更新结果
     */
    @PutMapping("update")
    public Resp update(@RequestBody TestPlanCaseSnapshot testPlanCaseSnapshot) {
        boolean result = testPlanCaseSnapshotService.updateById(testPlanCaseSnapshot);
        if (result) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键查询计划用例快照
     *
     * @param id 主键
     * @return 计划用例快照
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Long id) {
        TestPlanCaseSnapshot testPlanCaseSnapshot = testPlanCaseSnapshotService.getById(id);
        if (testPlanCaseSnapshot != null) {
            return Resp.ok(testPlanCaseSnapshot);
        } else {
            return Resp.info(404, "计划用例快照不存在");
        }
    }

    /**
     * 查询所有计划用例快照
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.list();
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 批量删除计划用例快照
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Long> ids) {
        boolean result = testPlanCaseSnapshotService.removeByIds(ids);
        if (result) {
            return Resp.ok("批量删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据条件查询计划用例快照
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
            if (params.containsKey("originalCaseId")) {
                queryWrapper.and("original_case_id = ?", params.get("originalCaseId"));
            }
            if (params.containsKey("caseNumber")) {
                queryWrapper.and("case_number like ?", "%" + params.get("caseNumber") + "%");
            }
            if (params.containsKey("title")) {
                queryWrapper.and("title like ?", "%" + params.get("title") + "%");
            }
            if (params.containsKey("priority")) {
                queryWrapper.and("priority = ?", params.get("priority"));
            }
            if (params.containsKey("executeStatus")) {
                queryWrapper.and("execute_status = ?", params.get("executeStatus"));
            }
            if (params.containsKey("module")) {
                queryWrapper.and("module like ?", "%" + params.get("module") + "%");
            }
            if (params.containsKey("executorId")) {
                queryWrapper.and("executor_id = ?", params.get("executorId"));
            }
            if (params.containsKey("groupName")) {
                queryWrapper.and("group_name like ?", "%" + params.get("groupName") + "%");
            }
            if (params.containsKey("batchNo")) {
                queryWrapper.and("batch_no = ?", params.get("batchNo"));
            }
        }

        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.list(queryWrapper);
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 根据计划ID查询快照列表
     */
    @GetMapping("listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByPlanId(planId);
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 根据原用例ID查询快照列表
     */
    @GetMapping("listByOriginalCaseId/{originalCaseId}")
    public Resp listByOriginalCaseId(@PathVariable Integer originalCaseId) {
        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByOriginalCaseId(originalCaseId);
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 根据执行状态查询快照列表
     */
    @PostMapping("listByExecuteStatus")
    public Resp listByExecuteStatus(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("executeStatus")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            Integer executeStatus = Integer.parseInt(params.get("executeStatus").toString());
            List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByExecuteStatus(planId, executeStatus);
            return Resp.ok(testPlanCaseSnapshots);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和executeStatus");
        }
    }

    /**
     * 根据模块查询快照列表
     */
    @PostMapping("listByModule")
    public Resp listByModule(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("module")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            String module = params.get("module").toString();
            List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByModule(planId, module);
            return Resp.ok(testPlanCaseSnapshots);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和module");
        }
    }

    /**
     * 根据执行人查询快照列表
     */
    @PostMapping("listByExecutorId")
    public Resp listByExecutorId(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("executorId")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            Long executorId = Long.parseLong(params.get("executorId").toString());
            List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByExecutorId(planId, executorId);
            return Resp.ok(testPlanCaseSnapshots);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和executorId");
        }
    }

    /**
     * 更新执行状态
     */
    @PostMapping("updateExecuteStatus")
    public Resp updateExecuteStatus(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("id") && params.containsKey("executeStatus")) {
            Long id = Long.parseLong(params.get("id").toString());
            Integer executeStatus = Integer.parseInt(params.get("executeStatus").toString());
            Long executorId = params.containsKey("executorId") ? Long.parseLong(params.get("executorId").toString()) : null;

            boolean result = testPlanCaseSnapshotService.updateExecuteStatus(id, executeStatus, executorId);
            if (result) {
                return Resp.ok("更新执行状态成功");
            } else {
                return Resp.error();
            }
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含id和executeStatus");
        }
    }
}
