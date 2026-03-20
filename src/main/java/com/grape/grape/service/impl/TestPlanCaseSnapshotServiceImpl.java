package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanCaseSnapshot;
import com.grape.grape.mapper.TestPlanCaseSnapshotMapper;
import com.grape.grape.service.TestPlanCaseSnapshotService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 计划用例快照表-用例绑定到计划时的版本快照 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanCaseSnapshotServiceImpl extends ServiceImpl<TestPlanCaseSnapshotMapper, TestPlanCaseSnapshot> implements TestPlanCaseSnapshotService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCaseSnapshotServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanCaseSnapshot> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("plan_id = ? and is_deleted = 0", planId);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanCaseSnapshot> listByOriginalCaseId(Integer originalCaseId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("original_case_id = ? and is_deleted = 0", originalCaseId);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanCaseSnapshot> listByExecuteStatus(Long planId, Integer executeStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("plan_id = ? and execute_status = ? and is_deleted = 0", planId, executeStatus);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanCaseSnapshot> listByModule(Long planId, String module) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("plan_id = ? and module = ? and is_deleted = 0", planId, module);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanCaseSnapshot> listByExecutorId(Long planId, Long executorId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("plan_id = ? and executor_id = ? and is_deleted = 0", planId, executorId);
        return list(queryWrapper);
    }

    @Override
    public boolean updateExecuteStatus(Long id, Integer executeStatus, Long executorId) {
        TestPlanCaseSnapshot testPlanCaseSnapshot = getById(id);
        if (testPlanCaseSnapshot != null) {
            testPlanCaseSnapshot.setExecuteStatus(executeStatus);
            testPlanCaseSnapshot.setExecutorId(executorId);
            testPlanCaseSnapshot.setExecuteCount(testPlanCaseSnapshot.getExecuteCount() + 1);
            testPlanCaseSnapshot.setLastExecuteTime(System.currentTimeMillis());
            testPlanCaseSnapshot.setUpdatedAt(System.currentTimeMillis());
            return updateById(testPlanCaseSnapshot);
        }
        return false;
    }

    @Override
    public boolean save(TestPlanCaseSnapshot testPlanCaseSnapshot) {
        // 设置默认值
        if (testPlanCaseSnapshot.getExecuteStatus() == null) {
            testPlanCaseSnapshot.setExecuteStatus(0); // 0-未执行
        }
        if (testPlanCaseSnapshot.getExecuteCount() == null) {
            testPlanCaseSnapshot.setExecuteCount(0);
        }
        if (testPlanCaseSnapshot.getSortOrder() == null) {
            testPlanCaseSnapshot.setSortOrder(0);
        }
        if (testPlanCaseSnapshot.getIsDeleted() == null) {
            testPlanCaseSnapshot.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanCaseSnapshot.getCreatedAt() == null) {
            testPlanCaseSnapshot.setCreatedAt(now);
        }
        if (testPlanCaseSnapshot.getUpdatedAt() == null) {
            testPlanCaseSnapshot.setUpdatedAt(now);
        }
        if (testPlanCaseSnapshot.getSnapshotTime() == null) {
            testPlanCaseSnapshot.setSnapshotTime(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanCaseSnapshot.getCreatedBy() == null) {
                    testPlanCaseSnapshot.setCreatedBy(userId);
                }
                if (testPlanCaseSnapshot.getSnapshotBy() == null) {
                    testPlanCaseSnapshot.setSnapshotBy(userId);
                }
                testPlanCaseSnapshot.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanCaseSnapshot);
    }

    @Override
    public boolean updateById(TestPlanCaseSnapshot testPlanCaseSnapshot) {
        // 设置更新时间
        testPlanCaseSnapshot.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanCaseSnapshot.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanCaseSnapshot);
    }
}
