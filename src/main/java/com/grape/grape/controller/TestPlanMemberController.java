package com.grape.grape.controller;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlan;
import com.grape.grape.entity.TestPlanMember;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanMemberService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划成员表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlanMember")
public class TestPlanMemberController {

    @Autowired
    private TestPlanMemberService testPlanMemberService;

    @Autowired
    private com.grape.grape.service.UserService userService;

    /**
     * 新增测试计划成员
     *
     * @param testPlanMember 测试计划成员
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanMember testPlanMember) {
        boolean saved = testPlanMemberService.save(testPlanMember);
        if (saved) {
            return Resp.ok(testPlanMember);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划成员
     *
     * @param testPlanMember 测试计划成员
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanMember testPlanMember) {
        boolean updated = testPlanMemberService.updateById(testPlanMember);
        if (updated) {
            return Resp.ok(testPlanMember);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划成员
     *
     * @param id 测试计划成员ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean deleted = testPlanMemberService.removeMember(id);
        if (deleted) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID查询测试计划成员
     *
     * @param id 测试计划成员ID
     * @return 测试计划成员
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanMember testPlanMember = testPlanMemberService.getById(id);
        if (testPlanMember != null && testPlanMember.getIsDeleted() == 0) {
            return Resp.ok(testPlanMember);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划成员
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param userId 用户ID（可选）
     * @param roleType 角色类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) String userId,
                    @RequestParam(required = false) Integer roleType, @RequestParam(required = false) Integer status) {
        Page<TestPlanMember> result = testPlanMemberService.page(Page.of(page, size), planId, userId, roleType, status);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询成员列表
     *
     * @param planId 计划ID
     * @return 成员列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanMember> list = testPlanMemberService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和角色类型查询成员列表
     *
     * @param planId 计划ID
     * @param roleType 角色类型
     * @return 成员列表
     */
    @GetMapping("/listByPlanIdAndRoleType")
    public Resp listByPlanIdAndRoleType(@RequestParam Long planId, @RequestParam Integer roleType) {
        List<TestPlanMember> list = testPlanMemberService.listByPlanIdAndRoleType(planId, roleType);
        return Resp.ok(list);
    }

    /**
     * 根据用户ID查询参与的计划列表
     *
     * @param userId 用户ID
     * @return 成员列表
     */
    @GetMapping("/listByUserId/{userId}")
    public Resp listByUserId(@PathVariable String userId) {
        List<TestPlanMember> list = testPlanMemberService.listByUserId(userId);
        return Resp.ok(list);
    }

    /**
     * 根据计划ID和用户ID查询成员信息
     *
     * @param planId 计划ID
     * @param userId 用户ID
     * @return 成员信息
     */
    @GetMapping("/getByPlanIdAndUserId")
    public Resp getByPlanIdAndUserId(@RequestParam Long planId, @RequestParam String userId) {
        TestPlanMember member = testPlanMemberService.getByPlanIdAndUserId(planId, userId);
        if (member != null) {
            return Resp.ok(member);
        } else {
            return Resp.error();
        }
    }

    /**
     * 审批成员
     *
     * @param id 成员ID
     * @param approveStatus 审批状态
     * @param approveBy 审批人ID
     * @param approveRemark 审批意见
     * @return 操作结果
     */
    @PostMapping("/approveMember/{id}")
    public Resp approveMember(@PathVariable Long id, @RequestParam Integer approveStatus,
                            @RequestParam String approveBy, @RequestParam String approveRemark) {
        boolean approved = testPlanMemberService.approveMember(id, approveStatus, approveBy, approveRemark);
        if (approved) {
            return Resp.ok("审批成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新成员的用例执行统计
     *
     * @param id 成员ID
     * @param executedCaseCount 已执行用例数
     * @return 操作结果
     */
    @PutMapping("/updateExecutedCaseCount/{id}")
    public Resp updateExecutedCaseCount(@PathVariable Long id, @RequestParam Integer executedCaseCount) {
        boolean updated = testPlanMemberService.updateExecutedCaseCount(id, executedCaseCount);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取计划的审批人列表（按审批顺序）
     *
     * @param planId 计划ID
     * @return 审批人列表
     */
    @GetMapping("/listApproversByPlanId/{planId}")
    public Resp listApproversByPlanId(@PathVariable Long planId) {
        List<TestPlanMember> list = testPlanMemberService.listApproversByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 查询当前登录用户分配到的测试计划
     *
     * @return 测试计划列表
     */
    @GetMapping("/listMyPlans")
    public Resp listMyPlans() {
        // 获取当前登录用户ID
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        if (currentUserId == null) {
            return Resp.error();
        }

        // 查询当前用户分配到的测试计划
        List<TestPlan> plans = testPlanMemberService.listMyPlans(currentUserId);
        return Resp.ok(plans);
    }
}
