package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanCaseSnapshotStep;
import com.grape.grape.mapper.TestPlanCaseSnapshotStepMapper;
import com.grape.grape.service.TestPlanCaseSnapshotStepService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划用例快照步骤表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-24
 */
@Service
public class TestPlanCaseSnapshotStepServiceImpl extends ServiceImpl<TestPlanCaseSnapshotStepMapper, TestPlanCaseSnapshotStep> implements TestPlanCaseSnapshotStepService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCaseSnapshotStepServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanCaseSnapshotStep> getBySnapshotId(Long snapshotId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("snapshot_id = ?", snapshotId)
                .and("is_deleted = 0")
                .orderBy("step_number asc");
        return list(queryWrapper);
    }

    @Override
    public boolean saveSteps(Long snapshotId, List<TestPlanCaseSnapshotStep> steps) {
        if (snapshotId == null || steps == null || steps.isEmpty()) {
            return false;
        }

        // 先删除旧的步骤
        removeBySnapshotId(snapshotId);

        // 保存新的步骤
        long now = System.currentTimeMillis();
        String userId = UserUtils.getCurrentLoginUserId(userService);

        for (int i = 0; i < steps.size(); i++) {
            TestPlanCaseSnapshotStep step = steps.get(i);
            step.setSnapshotId(snapshotId);
            step.setStepNumber(i + 1);
            step.setCreatedAt(now);
            step.setUpdatedAt(now);
            step.setCreatedBy(userId);
            step.setUpdatedBy(userId);
            step.setIsDeleted(0L);

            if (!save(step)) {
                log.error("保存测试计划用例快照步骤失败: snapshotId={}, stepNumber={}", snapshotId, step.getStepNumber());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean removeBySnapshotId(Long snapshotId) {
        if (snapshotId == null) {
            return false;
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("snapshot_id = ?", snapshotId);
        return remove(queryWrapper);
    }
}