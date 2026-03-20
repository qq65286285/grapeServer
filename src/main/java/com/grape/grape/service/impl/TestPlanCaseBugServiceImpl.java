package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanCaseBug;
import com.grape.grape.mapper.TestPlanCaseBugMapper;
import com.grape.grape.service.TestPlanCaseBugService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划用例缺陷关联表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanCaseBugServiceImpl extends ServiceImpl<TestPlanCaseBugMapper, TestPlanCaseBug> implements TestPlanCaseBugService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCaseBugServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanCaseBug> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("plan_id = ? and is_deleted = 0", planId);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanCaseBug> listBySnapshotId(Long snapshotId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("snapshot_id = ? and is_deleted = 0", snapshotId);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanCaseBug> listByBugId(Long bugId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("bug_id = ? and is_deleted = 0", bugId);
        return list(queryWrapper);
    }

    @Override
    public boolean verifyBug(Long id, Integer verifyStatus, Long verifiedBy, String verifyRemark) {
        TestPlanCaseBug testPlanCaseBug = getById(id);
        if (testPlanCaseBug != null) {
            testPlanCaseBug.setVerifyStatus(verifyStatus);
            testPlanCaseBug.setVerifiedBy(verifiedBy);
            testPlanCaseBug.setVerifiedAt(System.currentTimeMillis());
            testPlanCaseBug.setVerifyRemark(verifyRemark);
            testPlanCaseBug.setUpdatedAt(System.currentTimeMillis());
            return updateById(testPlanCaseBug);
        }
        return false;
    }

    @Override
    public boolean save(TestPlanCaseBug testPlanCaseBug) {
        // 设置默认值
        if (testPlanCaseBug.getVerifyStatus() == null) {
            testPlanCaseBug.setVerifyStatus(0); // 0-待验证
        }
        if (testPlanCaseBug.getIsBlocking() == null) {
            testPlanCaseBug.setIsBlocking(0); // 0-否
        }
        if (testPlanCaseBug.getAffectedCaseCount() == null) {
            testPlanCaseBug.setAffectedCaseCount(0);
        }
        if (testPlanCaseBug.getIsDeleted() == null) {
            testPlanCaseBug.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanCaseBug.getCreatedAt() == null) {
            testPlanCaseBug.setCreatedAt(now);
        }
        if (testPlanCaseBug.getUpdatedAt() == null) {
            testPlanCaseBug.setUpdatedAt(now);
        }
        if (testPlanCaseBug.getFoundTime() == null) {
            testPlanCaseBug.setFoundTime(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanCaseBug.getCreatedBy() == null) {
                    testPlanCaseBug.setCreatedBy(userId);
                }
                if (testPlanCaseBug.getFoundBy() == null) {
                    testPlanCaseBug.setFoundBy(userId);
                }
                testPlanCaseBug.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanCaseBug);
    }

    @Override
    public boolean updateById(TestPlanCaseBug testPlanCaseBug) {
        // 设置更新时间
        testPlanCaseBug.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanCaseBug.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanCaseBug);
    }
}
