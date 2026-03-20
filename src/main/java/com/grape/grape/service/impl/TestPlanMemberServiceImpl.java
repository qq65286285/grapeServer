package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanMember;
import com.grape.grape.mapper.TestPlanMemberMapper;
import com.grape.grape.service.TestPlanMemberService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划成员表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanMemberServiceImpl extends ServiceImpl<TestPlanMemberMapper, TestPlanMember> implements TestPlanMemberService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanMemberServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanMember> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("role_type asc, id asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanMember> listByPlanIdAndRoleType(Long planId, Integer roleType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and role_type = ? and is_deleted = 0", planId, roleType)
                .orderBy("id asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanMember> listByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ? and is_deleted = 0", userId)
                .orderBy("plan_id asc, role_type asc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanMember getByPlanIdAndUserId(Long planId, Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and user_id = ? and is_deleted = 0", planId, userId);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanMember> page(Page<TestPlanMember> page, Long planId, Long userId, Integer roleType, Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (userId != null) {
            queryWrapper.and("user_id = ?", userId);
        }

        if (roleType != null) {
            queryWrapper.and("role_type = ?", roleType);
        }

        if (status != null) {
            queryWrapper.and("status = ?", status);
        }

        queryWrapper.orderBy("role_type asc, id asc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean approveMember(Long id, Integer approveStatus, Long approveBy, String approveRemark) {
        TestPlanMember member = getById(id);
        if (member != null) {
            member.setApproveStatus(approveStatus);
            member.setApproveTime(System.currentTimeMillis());
            member.setApproveRemark(approveRemark);
            member.setUpdatedAt(System.currentTimeMillis());
            member.setUpdatedBy(approveBy);
            return updateById(member);
        }
        return false;
    }

    @Override
    public boolean removeMember(Long id) {
        TestPlanMember member = getById(id);
        if (member != null) {
            member.setStatus(2); // 2-已移除
            member.setIsDeleted(1); // 1-是
            member.setUpdatedAt(System.currentTimeMillis());
            return updateById(member);
        }
        return false;
    }

    @Override
    public boolean updateExecutedCaseCount(Long id, Integer executedCaseCount) {
        TestPlanMember member = getById(id);
        if (member != null) {
            member.setExecutedCaseCount(executedCaseCount);
            member.setUpdatedAt(System.currentTimeMillis());
            return updateById(member);
        }
        return false;
    }

    @Override
    public List<TestPlanMember> listApproversByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and role_type = 3 and is_deleted = 0", planId)
                .orderBy("approve_order asc");
        return list(queryWrapper);
    }

    @Override
    public boolean save(TestPlanMember testPlanMember) {
        // 设置默认值
        if (testPlanMember.getScopeType() == null) {
            testPlanMember.setScopeType(0); // 0-全部
        }
        if (testPlanMember.getApproveStatus() == null) {
            testPlanMember.setApproveStatus(3); // 3-无需审批
        }
        if (testPlanMember.getStatus() == null) {
            testPlanMember.setStatus(1); // 1-正常
        }
        if (testPlanMember.getAssignedCaseCount() == null) {
            testPlanMember.setAssignedCaseCount(0);
        }
        if (testPlanMember.getExecutedCaseCount() == null) {
            testPlanMember.setExecutedCaseCount(0);
        }
        if (testPlanMember.getIsDeleted() == null) {
            testPlanMember.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanMember.getCreatedAt() == null) {
            testPlanMember.setCreatedAt(now);
        }
        if (testPlanMember.getUpdatedAt() == null) {
            testPlanMember.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanMember.getCreatedBy() == null) {
                    testPlanMember.setCreatedBy(userId);
                }
                testPlanMember.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanMember);
    }

    @Override
    public boolean updateById(TestPlanMember testPlanMember) {
        // 设置更新时间
        testPlanMember.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanMember.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanMember);
    }
}
