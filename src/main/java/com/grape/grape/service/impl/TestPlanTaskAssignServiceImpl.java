package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanTaskAssign;
import com.grape.grape.mapper.TestPlanTaskAssignMapper;
import com.grape.grape.service.TestPlanTaskAssignService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 测试计划任务分配表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanTaskAssignServiceImpl extends ServiceImpl<TestPlanTaskAssignMapper, TestPlanTaskAssign> implements TestPlanTaskAssignService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanTaskAssignServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanTaskAssign> listByTaskId(Long taskId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("task_id = ?", taskId)
                .orderBy("assigned_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTaskAssign> listByUserId(String userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("user_id = ?", userId)
                .orderBy("assigned_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTaskAssign> listByAssignType(Integer assignType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("assign_type = ?", assignType)
                .orderBy("assigned_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTaskAssign> listByTaskIdAndAssignType(Long taskId, Integer assignType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("task_id = ? and assign_type = ?", taskId, assignType)
                .orderBy("assigned_at desc");
        return list(queryWrapper);
    }

    @Override
    public Page<TestPlanTaskAssign> page(Page<TestPlanTaskAssign> page, Long taskId, String userId, Integer assignType) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (taskId != null) {
            queryWrapper.where("task_id = ?", taskId);
        }

        if (userId != null) {
            queryWrapper.and("user_id = ?", userId);
        }

        if (assignType != null) {
            queryWrapper.and("assign_type = ?", assignType);
        }

        queryWrapper.orderBy("assigned_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public int batchAddAssigns(Long taskId, List<String> userIds, Integer assignType, Double workload, String assignedBy) {
        int successCount = 0;
        Date now = new Date();
        for (String userId : userIds) {
            // 检查是否已存在分配
            QueryWrapper checkWrapper = QueryWrapper.create()
                    .where("task_id = ? and user_id = ? and assign_type = ?", taskId, userId, assignType);
            if (!exists(checkWrapper)) {
                TestPlanTaskAssign assign = new TestPlanTaskAssign();
                assign.setTaskId(taskId);
                assign.setUserId(userId);
                assign.setAssignType(assignType);
                assign.setWorkload(workload);
                assign.setAssignedBy(assignedBy != null ? assignedBy : getCurrentUserId());
                assign.setAssignedAt(now);
                if (save(assign)) {
                    successCount++;
                }
            }
        }
        return successCount;
    }

    @Override
    public int batchDeleteByTaskId(Long taskId) {
        List<TestPlanTaskAssign> assigns = listByTaskId(taskId);
        int successCount = 0;
        for (TestPlanTaskAssign assign : assigns) {
            if (removeById(assign.getId())) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public boolean deleteByTaskIdAndUserId(Long taskId, String userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("task_id = ? and user_id = ?", taskId, userId);
        List<TestPlanTaskAssign> assigns = list(queryWrapper);
        if (!assigns.isEmpty()) {
            TestPlanTaskAssign assign = assigns.get(0);
            return removeById(assign.getId());
        }
        return false;
    }

    @Override
    public int deleteByTaskIdAndAssignType(Long taskId, Integer assignType) {
        List<TestPlanTaskAssign> assigns = listByTaskIdAndAssignType(taskId, assignType);
        int successCount = 0;
        for (TestPlanTaskAssign assign : assigns) {
            if (removeById(assign.getId())) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public boolean updateWorkload(Long id, Double workload) {
        TestPlanTaskAssign assign = getById(id);
        if (assign != null) {
            assign.setWorkload(workload);
            return updateById(assign);
        }
        return false;
    }

    @Override
    public boolean save(TestPlanTaskAssign assign) {
        // 设置分配时间
        if (assign.getAssignedAt() == null) {
            assign.setAssignedAt(new Date());
        }

        // 设置分配人
        if (assign.getAssignedBy() == null) {
            assign.setAssignedBy(getCurrentUserId());
        }

        return super.save(assign);
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     */
    private String getCurrentUserId() {
        return UserUtils.getCurrentLoginUserId(userService);
    }
}