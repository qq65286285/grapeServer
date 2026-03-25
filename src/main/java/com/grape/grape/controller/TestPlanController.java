package com.grape.grape.controller;

import com.grape.grape.entity.TestPlan;
import com.grape.grape.entity.TestPlanMember;
import com.grape.grape.entity.TestPlanTask;
import com.grape.grape.entity.TestPlanTaskAssign;
import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanMemberService;
import com.grape.grape.service.TestPlanService;
import com.grape.grape.service.TestPlanTaskAssignService;
import com.grape.grape.service.TestPlanTaskService;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 测试计划主表 控制层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlan")
public class TestPlanController {

    private static final Logger log = LoggerFactory.getLogger(TestPlanController.class);

    @Autowired
    private TestPlanService testPlanService;
    
    @Autowired
    private TestPlanMemberService testPlanMemberService;
    
    @Autowired
    private TestPlanTaskService testPlanTaskService;
    
    @Autowired
    private TestPlanTaskAssignService testPlanTaskAssignService;

    /**
     * 添加测试计划。
     *
     * @param testPlan 测试计划
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public Resp save(@RequestBody TestPlan testPlan) {
        boolean result = testPlanService.save(testPlan);
        if (result) {
            return Resp.ok("添加成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键删除测试计划。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean result = testPlanService.removeById(id);
        if (result) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键更新测试计划。
     *
     * @param testPlan 测试计划
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public Resp update(@RequestBody TestPlan testPlan) {
        boolean result = testPlanService.updateById(testPlan);
        if (result) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键查询测试计划。
     *
     * @param id 主键
     * @return 测试计划
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Long id) {
        TestPlan testPlan = testPlanService.getById(id);
        if (testPlan != null) {
            return Resp.ok(testPlan);
        } else {
            return Resp.info(404, "测试计划不存在");
        }
    }

    /**
     * 查询所有测试计划。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        List<TestPlan> testPlans = testPlanService.list();
        return Resp.ok(testPlans);
    }

    /**
     * 分页查询
     */
    @GetMapping("page")
    public Resp page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");
        com.mybatisflex.core.paginate.Page<TestPlan> page = testPlanService.page(com.mybatisflex.core.paginate.Page.of(pageNum, pageSize), queryWrapper);
        return Resp.ok(page);
    }

    /**
     * 分页查询（带条件）
     */
    @PostMapping("page")
    public Resp page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");

        // 构建查询条件
        if (params != null) {
            if (params.containsKey("planName") && params.get("planName") != null && !params.get("planName").toString().isEmpty()) {
                queryWrapper.and("plan_name like ?", "%" + params.get("planName") + "%");
            }
            if (params.containsKey("planNo") && params.get("planNo") != null && !params.get("planNo").toString().isEmpty()) {
                queryWrapper.and("plan_no like ?", "%" + params.get("planNo") + "%");
            }
            if (params.containsKey("planType") && params.get("planType") != null && !params.get("planType").toString().isEmpty()) {
                queryWrapper.and("plan_type = ?", params.get("planType"));
            }
            if (params.containsKey("projectId") && params.get("projectId") != null && !params.get("projectId").toString().isEmpty()) {
                queryWrapper.and("project_id = ?", params.get("projectId"));
            }
            if (params.containsKey("iterationId") && params.get("iterationId") != null && !params.get("iterationId").toString().isEmpty()) {
                queryWrapper.and("iteration_id = ?", params.get("iterationId"));
            }
            if (params.containsKey("status") && params.get("status") != null && !params.get("status").toString().isEmpty()) {
                queryWrapper.and("status = ?", params.get("status"));
            }
            if (params.containsKey("ownerId") && params.get("ownerId") != null && !params.get("ownerId").toString().isEmpty()) {
                queryWrapper.and("owner_id = ?", params.get("ownerId"));
            }
            if (params.containsKey("isTemplate") && params.get("isTemplate") != null && !params.get("isTemplate").toString().isEmpty()) {
                queryWrapper.and("is_template = ?", params.get("isTemplate"));
            }
        }

        com.mybatisflex.core.paginate.Page<TestPlan> page = testPlanService.page(com.mybatisflex.core.paginate.Page.of(pageNum, pageSize), queryWrapper);
        return Resp.ok(page);
    }

    /**
     * 批量删除测试计划
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Long> ids) {
        boolean result = testPlanService.removeByIds(ids);
        if (result) {
            return Resp.ok("批量删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据条件查询测试计划
     */
    @PostMapping("listByCondition")
    public Resp listByCondition(@RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");

        // 构建查询条件
        if (params != null) {
            if (params.containsKey("planName") && params.get("planName") != null && !params.get("planName").toString().isEmpty()) {
                queryWrapper.and("plan_name like ?", "%" + params.get("planName") + "%");
            }
            if (params.containsKey("planNo") && params.get("planNo") != null && !params.get("planNo").toString().isEmpty()) {
                queryWrapper.and("plan_no like ?", "%" + params.get("planNo") + "%");
            }
            if (params.containsKey("planType") && params.get("planType") != null && !params.get("planType").toString().isEmpty()) {
                queryWrapper.and("plan_type = ?", params.get("planType"));
            }
            if (params.containsKey("projectId") && params.get("projectId") != null && !params.get("projectId").toString().isEmpty()) {
                queryWrapper.and("project_id = ?", params.get("projectId"));
            }
            if (params.containsKey("iterationId") && params.get("iterationId") != null && !params.get("iterationId").toString().isEmpty()) {
                queryWrapper.and("iteration_id = ?", params.get("iterationId"));
            }
            if (params.containsKey("status") && params.get("status") != null && !params.get("status").toString().isEmpty()) {
                queryWrapper.and("status = ?", params.get("status"));
            }
            if (params.containsKey("ownerId") && params.get("ownerId") != null && !params.get("ownerId").toString().isEmpty()) {
                queryWrapper.and("owner_id = ?", params.get("ownerId"));
            }
            if (params.containsKey("isTemplate") && params.get("isTemplate") != null && !params.get("isTemplate").toString().isEmpty()) {
                queryWrapper.and("is_template = ?", params.get("isTemplate"));
            }
        }

        List<TestPlan> testPlans = testPlanService.list(queryWrapper);
        return Resp.ok(testPlans);
    }

    /**
     * 分配测试计划给成员
     */
    @PostMapping("assign")
    public Resp assign(@RequestBody Map<String, Object> params) {
        try {
            Long planId = Long.parseLong(params.get("planId").toString());
            List<String> userIds = (List<String>) params.get("userIds");
            String assignedBy = params.get("assignedBy").toString();

            // 验证测试计划是否存在
            TestPlan testPlan = testPlanService.getById(planId);
            if (testPlan == null) {
                return Resp.info(404, "测试计划不存在");
            }

            // 为每个成员添加到测试计划成员表
            for (String userId : userIds) {
                // 检查是否已经是成员
                TestPlanMember existingMember = testPlanMemberService.getByPlanIdAndUserId(planId, userId);
                if (existingMember == null) {
                    TestPlanMember member = new TestPlanMember();
                    member.setPlanId(planId);
                    member.setUserId(userId);
                    member.setRoleType(2); // 默认角色类型
                    member.setStatus(1); // 默认状态
                    member.setCreatedBy(assignedBy);
                    testPlanMemberService.save(member);
                }
            }

            // 获取测试计划下的所有任务
            List<TestPlanTask> tasks = testPlanTaskService.listByPlanId(planId);

            // 为每个任务分配给成员
            for (TestPlanTask task : tasks) {
                for (String userId : userIds) {
                    // 检查是否已经分配
                    List<TestPlanTaskAssign> existingAssigns = testPlanTaskAssignService.listByTaskIdAndAssignType(task.getId(), 1);
                    boolean alreadyAssigned = false;
                    for (TestPlanTaskAssign assign : existingAssigns) {
                        if (assign.getUserId().equals(userId)) {
                            alreadyAssigned = true;
                            break;
                        }
                    }
                    if (!alreadyAssigned) {
                        testPlanTaskAssignService.batchAddAssigns(task.getId(), userIds, 1, 0.0, assignedBy);
                        break; // 每个任务只分配一次
                    }
                }
            }

            return Resp.ok("分配成功");
        } catch (Exception e) {
            log.error("分配测试计划失败", e);
            return Resp.error();
        }
    }
}
