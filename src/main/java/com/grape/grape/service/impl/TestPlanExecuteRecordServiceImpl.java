package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanExecuteRecord;
import com.grape.grape.mapper.TestPlanExecuteRecordMapper;
import com.grape.grape.service.TestPlanExecuteRecordService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试计划执行记录表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanExecuteRecordServiceImpl extends ServiceImpl<TestPlanExecuteRecordMapper, TestPlanExecuteRecord> implements TestPlanExecuteRecordService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanExecuteRecordServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanExecuteRecord> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("execute_time desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanExecuteRecord> listBySnapshotId(Long snapshotId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("snapshot_id = ? and is_deleted = 0", snapshotId)
                .orderBy("execute_time desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanExecuteRecord getByExecuteNo(String executeNo) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("execute_no = ? and is_deleted = 0", executeNo);
        return getOne(queryWrapper);
    }

    @Override
    public List<TestPlanExecuteRecord> listByPlanIdAndStatus(Long planId, Integer executeStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and execute_status = ? and is_deleted = 0", planId, executeStatus)
                .orderBy("execute_time desc");
        return list(queryWrapper);
    }

    @Override
    public Map<String, Object> calculateExecutionStats(Long planId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询所有执行记录
        List<TestPlanExecuteRecord> records = listByPlanId(planId);
        
        int total = records.size();
        int passed = 0;
        int failed = 0;
        int blocked = 0;
        int skipped = 0;
        
        for (TestPlanExecuteRecord record : records) {
            switch (record.getExecuteStatus()) {
                case 1: // 通过
                    passed++;
                    break;
                case 2: // 失败
                    failed++;
                    break;
                case 3: // 阻塞
                    blocked++;
                    break;
                case 4: // 跳过
                    skipped++;
                    break;
            }
        }
        
        stats.put("total", total);
        stats.put("passed", passed);
        stats.put("failed", failed);
        stats.put("blocked", blocked);
        stats.put("skipped", skipped);
        
        // 计算通过率
        double passRate = total > 0 ? (double) passed / total * 100 : 0;
        stats.put("passRate", passRate);
        
        return stats;
    }

    @Override
    public Page<TestPlanExecuteRecord> page(Page<TestPlanExecuteRecord> page, Long planId, Long snapshotId, Long executorId, Integer executeStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (snapshotId != null) {
            queryWrapper.and("snapshot_id = ?", snapshotId);
        }

        if (executorId != null) {
            queryWrapper.and("executor_id = ?", executorId);
        }

        if (executeStatus != null) {
            queryWrapper.and("execute_status = ?", executeStatus);
        }

        queryWrapper.orderBy("execute_time desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean reviewRecord(Long id, Integer isReviewed, Long reviewedBy, String reviewComment) {
        TestPlanExecuteRecord record = getById(id);
        if (record != null) {
            record.setIsReviewed(isReviewed);
            record.setReviewedBy(reviewedBy);
            record.setReviewedAt(System.currentTimeMillis());
            record.setReviewComment(reviewComment);
            record.setUpdatedAt(System.currentTimeMillis());
            return updateById(record);
        }
        return false;
    }

    @Override
    public boolean save(TestPlanExecuteRecord testPlanExecuteRecord) {
        // 设置默认值
        if (testPlanExecuteRecord.getExecuteRound() == null) {
            testPlanExecuteRecord.setExecuteRound(1);
        }
        if (testPlanExecuteRecord.getAttachmentCount() == null) {
            testPlanExecuteRecord.setAttachmentCount(0);
        }
        if (testPlanExecuteRecord.getIsReviewed() == null) {
            testPlanExecuteRecord.setIsReviewed(0);
        }
        if (testPlanExecuteRecord.getIsDeleted() == null) {
            testPlanExecuteRecord.setIsDeleted(0);
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanExecuteRecord.getExecuteTime() == null) {
            testPlanExecuteRecord.setExecuteTime(now);
        }
        if (testPlanExecuteRecord.getCreatedAt() == null) {
            testPlanExecuteRecord.setCreatedAt(now);
        }
        if (testPlanExecuteRecord.getUpdatedAt() == null) {
            testPlanExecuteRecord.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanExecuteRecord.getCreatedBy() == null) {
                    testPlanExecuteRecord.setCreatedBy(userId);
                }
                if (testPlanExecuteRecord.getExecutorId() == null) {
                    testPlanExecuteRecord.setExecutorId(userId);
                }
                testPlanExecuteRecord.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        // 生成执行编号
        if (testPlanExecuteRecord.getExecuteNo() == null) {
            String executeNo = "EXEC-" + testPlanExecuteRecord.getPlanId() + "-" + testPlanExecuteRecord.getSnapshotId() + "-" + now;
            testPlanExecuteRecord.setExecuteNo(executeNo);
        }

        return super.save(testPlanExecuteRecord);
    }

    @Override
    public boolean updateById(TestPlanExecuteRecord testPlanExecuteRecord) {
        // 设置更新时间
        testPlanExecuteRecord.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanExecuteRecord.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanExecuteRecord);
    }
}
