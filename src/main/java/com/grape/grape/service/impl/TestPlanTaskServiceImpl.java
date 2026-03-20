package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanTask;
import com.grape.grape.mapper.TestPlanTaskMapper;
import com.grape.grape.service.TestPlanTaskService;
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
 * 测试计划任务表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanTaskServiceImpl extends ServiceImpl<TestPlanTaskMapper, TestPlanTask> implements TestPlanTaskService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanTaskServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanTask> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTask> listByTaskType(Integer taskType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("task_type = ?", taskType)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTask> listByStatus(Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("status = ?", status)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTask> listByOwnerId(Long ownerId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("owner_id = ?", ownerId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanTask getByTaskNo(String taskNo) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("task_no = ?", taskNo);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanTask> page(Page<TestPlanTask> page, Long planId, Integer taskType, Integer status, Long ownerId, Integer priority) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (planId != null) {
            queryWrapper.where("plan_id = ?", planId);
        }

        if (taskType != null) {
            queryWrapper.and("task_type = ?", taskType);
        }

        if (status != null) {
            queryWrapper.and("status = ?", status);
        }

        if (ownerId != null) {
            queryWrapper.and("owner_id = ?", ownerId);
        }

        if (priority != null) {
            queryWrapper.and("priority = ?", priority);
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        TestPlanTask task = getById(id);
        if (task != null) {
            task.setStatus(status);
            // 设置更新人和更新时间
            setUpdateInfo(task);
            return update(task);
        }
        return false;
    }

    @Override
    public boolean updateProgress(Long id, Double progress) {
        TestPlanTask task = getById(id);
        if (task != null) {
            task.setProgress(progress);
            // 设置更新人和更新时间
            setUpdateInfo(task);
            return update(task);
        }
        return false;
    }

    @Override
    public int batchDelete(List<Long> ids) {
        int successCount = 0;
        for (Long id : ids) {
            if (removeById(id)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public int deleteByPlanId(Long planId) {
        List<TestPlanTask> tasks = listByPlanId(planId);
        int successCount = 0;
        for (TestPlanTask task : tasks) {
            if (removeById(task.getId())) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public boolean save(TestPlanTask task) {
        // 设置创建人和创建时间
        if (task.getCreatedBy() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                task.setCreatedBy(userIdStr);
            }
        }
        return super.save(task);
    }

    @Override
    public boolean update(TestPlanTask task) {
        // 设置更新人和更新时间
        setUpdateInfo(task);
        return updateById(task);
    }

    /**
     * 设置更新信息
     *
     * @param task 任务对象
     */
    private void setUpdateInfo(TestPlanTask task) {
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
                task.setUpdatedBy(userIdStr);
            }
    }
}