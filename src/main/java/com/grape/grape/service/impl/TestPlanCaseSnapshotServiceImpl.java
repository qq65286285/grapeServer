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

    @Autowired
    private com.grape.grape.service.TestCaseStepService testCaseStepService;

    @Autowired
    private com.grape.grape.service.TestPlanCaseSnapshotStepService testPlanCaseSnapshotStepService;

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
    public List<TestPlanCaseSnapshot> listByExecutorId(Long planId, String executorId) {
        QueryWrapper queryWrapper = QueryWrapper.create().where("plan_id = ? and executor_id = ? and is_deleted = 0", planId, executorId);
        return list(queryWrapper);
    }

    @Override
    public boolean updateExecuteStatus(Long id, Integer executeStatus, String executorId) {
        TestPlanCaseSnapshot testPlanCaseSnapshot = getById(id);
        if (testPlanCaseSnapshot != null) {
            testPlanCaseSnapshot.setExecuteStatus(executeStatus);
            // 只有当 executorId 为 null 时才更新 executorId 字段
            if (executorId != null && testPlanCaseSnapshot.getExecutorId() == null) {
                testPlanCaseSnapshot.setExecutorId(executorId);
            }
            testPlanCaseSnapshot.setExecuteCount(testPlanCaseSnapshot.getExecuteCount() + 1);
            testPlanCaseSnapshot.setLastExecuteTime(System.currentTimeMillis());
            testPlanCaseSnapshot.setUpdatedAt(System.currentTimeMillis());
            return updateById(testPlanCaseSnapshot);
        }
        return false;
    }

    @Override
    public int batchBindCases(Long planId, List<Integer> caseIds, String executorId) {
        int successCount = 0;
        long now = System.currentTimeMillis();
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        Long userId = null;
        if (userIdStr != null) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }
        
        // 1. 查询该测试计划下当前执行人已绑定的所有测试用例
        QueryWrapper existingWrapper = QueryWrapper.create()
                .where("plan_id = ? and executor_id = ? and is_deleted = 0", planId, executorId);
        List<TestPlanCaseSnapshot> existingSnapshots = list(existingWrapper);
        
        // 2. 收集当前执行人已绑定的用例ID
        java.util.Set<Integer> existingCaseIds = new java.util.HashSet<>();
        for (TestPlanCaseSnapshot snapshot : existingSnapshots) {
            existingCaseIds.add(snapshot.getOriginalCaseId());
        }
        
        // 3. 收集新的用例ID
        java.util.Set<Integer> newCaseIds = new java.util.HashSet<>(caseIds);
        
        // 4. 删除当前执行人不在新列表中的用例
        for (TestPlanCaseSnapshot snapshot : existingSnapshots) {
            if (!newCaseIds.contains(snapshot.getOriginalCaseId())) {
                // 软删除
                snapshot.setIsDeleted(1);
                snapshot.setUpdatedAt(now);
                if (userId != null) {
                    snapshot.setUpdatedBy(userId);
                }
                updateById(snapshot);
            }
        }
        
        // 5. 添加新列表中当前执行人没有的用例
        for (Integer caseId : caseIds) {
            // 检查当前执行人是否已经绑定了该用例
            QueryWrapper checkWrapper = QueryWrapper.create()
                    .where("plan_id = ? and original_case_id = ? and executor_id = ? and is_deleted = 0", planId, caseId, executorId);
            if (!exists(checkWrapper)) {
                TestPlanCaseSnapshot snapshot = new TestPlanCaseSnapshot();
                snapshot.setPlanId(planId);
                snapshot.setOriginalCaseId(caseId);
                snapshot.setCaseNumber("TC-" + caseId); // 生成默认用例编号
                snapshot.setTitle("测试用例 " + caseId); // 生成默认标题
                snapshot.setDescription("测试用例描述"); // 默认描述
                snapshot.setPriority(2); // 默认优先级：中等
                snapshot.setCaseStatus(1); // 默认状态：正常
                snapshot.setCaseVersion(1); // 默认版本
                snapshot.setEnvironmentId(1); // 默认环境
                snapshot.setExpectedResult("测试通过"); // 默认预期结果
                snapshot.setModule("未分类"); // 默认模块
                snapshot.setFolderId(1); // 默认文件夹
                snapshot.setCaseRemark(""); // 默认备注
                
                // 设置执行人
                if (executorId != null) {
                    snapshot.setExecutorId(executorId);
                }
                
                snapshot.setExecuteStatus(0); // 未执行
                snapshot.setExecuteCount(0);
                snapshot.setGroupName("默认分组"); // 默认分组
                snapshot.setBatchNo("BATCH-" + System.currentTimeMillis()); // 生成批次数
                snapshot.setSortOrder(caseIds.indexOf(caseId)); // 设置排序
                snapshot.setPlanRemark(""); // 默认计划备注
                
                // 设置时间戳
                snapshot.setSnapshotTime(now);
                snapshot.setCreatedAt(now);
                snapshot.setUpdatedAt(now);
                
                // 设置创建人和更新人
                if (userId != null) {
                    snapshot.setCreatedBy(userId);
                    snapshot.setSnapshotBy(userId);
                    snapshot.setUpdatedBy(userId);
                }
                
                snapshot.setIsDeleted(0); // 未删除
                
                if (save(snapshot)) {
                    // 保存测试用例步骤信息
                    List<com.grape.grape.entity.TestCaseStep> originalSteps = testCaseStepService.getByTestCaseId(caseId);
                    if (originalSteps != null && !originalSteps.isEmpty()) {
                        List<com.grape.grape.entity.TestPlanCaseSnapshotStep> snapshotSteps = new java.util.ArrayList<>();
                        for (com.grape.grape.entity.TestCaseStep originalStep : originalSteps) {
                            com.grape.grape.entity.TestPlanCaseSnapshotStep snapshotStep = new com.grape.grape.entity.TestPlanCaseSnapshotStep();
                            snapshotStep.setStepDescription(originalStep.getStep());
                            snapshotStep.setExpectedResult(originalStep.getExpectedResult());
                            snapshotStep.setExecuteStatus(0); // 未执行
                            snapshotSteps.add(snapshotStep);
                        }
                        testPlanCaseSnapshotStepService.saveSteps(snapshot.getId(), snapshotSteps);
                    }
                    successCount++;
                }
            }
        }
        return successCount;
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
