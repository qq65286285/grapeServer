package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlan;
import com.grape.grape.mapper.TestPlanMapper;
import com.grape.grape.service.TestPlanService;
import com.grape.grape.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 测试计划主表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan> implements TestPlanService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean save(TestPlan testPlan) {
        // 设置默认值
        if (testPlan.getStatus() == null) {
            testPlan.setStatus(1); // 1-未开始
        }
        if (testPlan.getProgress() == null) {
            testPlan.setProgress(new BigDecimal("0.00"));
        }
        if (testPlan.getIsTemplate() == null) {
            testPlan.setIsTemplate(0); // 0-否
        }
        if (testPlan.getTotalCaseCount() == null) {
            testPlan.setTotalCaseCount(0);
        }
        if (testPlan.getExecutedCaseCount() == null) {
            testPlan.setExecutedCaseCount(0);
        }
        if (testPlan.getPassedCaseCount() == null) {
            testPlan.setPassedCaseCount(0);
        }
        if (testPlan.getFailedCaseCount() == null) {
            testPlan.setFailedCaseCount(0);
        }
        if (testPlan.getBlockedCaseCount() == null) {
            testPlan.setBlockedCaseCount(0);
        }
        if (testPlan.getSkippedCaseCount() == null) {
            testPlan.setSkippedCaseCount(0);
        }
        if (testPlan.getIsDeleted() == null) {
            testPlan.setIsDeleted(0); // 0-否
        }

        // 设置创建时间和更新时间
        Date now = new Date();
        testPlan.setCreatedAt(now);
        testPlan.setUpdatedAt(now);

        // 设置创建人ID和更新人ID
        String creatorIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (creatorIdStr != null) {
            testPlan.setCreatedBy(creatorIdStr);
            testPlan.setUpdatedBy(creatorIdStr);
            log.info("设置测试计划创建人ID: {}", creatorIdStr);
        }

        return super.save(testPlan);
    }

    @Override
    public boolean updateById(TestPlan testPlan) {
        // 设置更新时间
        testPlan.setUpdatedAt(new Date());

        // 设置更新人ID和更新时间
        String updaterIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (updaterIdStr != null) {
            testPlan.setUpdatedBy(updaterIdStr);
            log.info("设置测试计划更新人ID: {}", updaterIdStr);
        }

        return super.updateById(testPlan);
    }
}
