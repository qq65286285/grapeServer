package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanOperationLog;
import com.grape.grape.mapper.TestPlanOperationLogMapper;
import com.grape.grape.service.TestPlanOperationLogService;
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
 * 测试计划操作日志表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanOperationLogServiceImpl extends ServiceImpl<TestPlanOperationLogMapper, TestPlanOperationLog> implements TestPlanOperationLogService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanOperationLogServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanOperationLog> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanOperationLog> listByOperationType(Integer operationType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("operation_type = ?", operationType)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanOperationLog> listByOperationModule(String operationModule) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("operation_module = ?", operationModule)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanOperationLog> listByCreatedBy(Long createdBy) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("created_by = ?", createdBy)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public Page<TestPlanOperationLog> page(Page<TestPlanOperationLog> page, Long planId, Integer operationType, String operationModule, Long createdBy, Long startDate, Long endDate) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (planId != null) {
            queryWrapper.where("plan_id = ?", planId);
        }

        if (operationType != null) {
            queryWrapper.and("operation_type = ?", operationType);
        }

        if (operationModule != null) {
            queryWrapper.and("operation_module = ?", operationModule);
        }

        if (createdBy != null) {
            queryWrapper.and("created_by = ?", createdBy);
        }

        if (startDate != null) {
            queryWrapper.and("created_at >= ?", startDate);
        }

        if (endDate != null) {
            queryWrapper.and("created_at <= ?", endDate);
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean recordLog(TestPlanOperationLog operationLog) {
        // 设置默认值
        if (operationLog.getCreatedAt() == null) {
            operationLog.setCreatedAt(System.currentTimeMillis());
        }

        // 设置操作人
        if (operationLog.getCreatedBy() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    operationLog.setCreatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }
        }

        // 保存日志
        return save(operationLog);
    }

    @Override
    public int recordBatchLogs(List<TestPlanOperationLog> logs) {
        int successCount = 0;
        for (TestPlanOperationLog operationLog : logs) {
            if (recordLog(operationLog)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public long countByPlanIdAndOperationType(Long planId, Integer operationType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and operation_type = ?", planId, operationType);
        return count(queryWrapper);
    }

    @Override
    public List<TestPlanOperationLog> getRecentLogs(Long planId, int limit) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc")
                .limit(limit);
        return list(queryWrapper);
    }

    @Override
    public boolean save(TestPlanOperationLog testPlanOperationLog) {
        // 设置默认值
        if (testPlanOperationLog.getCreatedAt() == null) {
            testPlanOperationLog.setCreatedAt(System.currentTimeMillis());
        }

        // 设置操作人
        if (testPlanOperationLog.getCreatedBy() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    testPlanOperationLog.setCreatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }
        }

        return super.save(testPlanOperationLog);
    }
}