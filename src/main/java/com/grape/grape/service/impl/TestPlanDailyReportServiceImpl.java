package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanDailyReport;
import com.grape.grape.mapper.TestPlanDailyReportMapper;
import com.grape.grape.service.TestPlanDailyReportService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 测试计划日报表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanDailyReportServiceImpl extends ServiceImpl<TestPlanDailyReportMapper, TestPlanDailyReport> implements TestPlanDailyReportService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanDailyReportServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanDailyReport> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("report_date desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanDailyReport> listByPlanIdAndDateRange(Long planId, Date startDate, Date endDate) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .and("report_date >= ?", startDate)
                .and("report_date <= ?", endDate)
                .orderBy("report_date asc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanDailyReport getByPlanIdAndReportDate(Long planId, Date reportDate) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and report_date = ?", planId, reportDate);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanDailyReport> page(Page<TestPlanDailyReport> page, Long planId, Date startDate, Date endDate) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (planId != null) {
            queryWrapper.where("plan_id = ?", planId);
        }

        if (startDate != null) {
            queryWrapper.and("report_date >= ?", startDate);
        }

        if (endDate != null) {
            queryWrapper.and("report_date <= ?", endDate);
        }

        queryWrapper.orderBy("report_date desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean save(TestPlanDailyReport testPlanDailyReport) {
        // 设置默认值
        if (testPlanDailyReport.getExecutedCount() == null) {
            testPlanDailyReport.setExecutedCount(0);
        }
        if (testPlanDailyReport.getPassedCount() == null) {
            testPlanDailyReport.setPassedCount(0);
        }
        if (testPlanDailyReport.getFailedCount() == null) {
            testPlanDailyReport.setFailedCount(0);
        }
        if (testPlanDailyReport.getBlockedCount() == null) {
            testPlanDailyReport.setBlockedCount(0);
        }
        if (testPlanDailyReport.getNewBugCount() == null) {
            testPlanDailyReport.setNewBugCount(0);
        }
        if (testPlanDailyReport.getFixedBugCount() == null) {
            testPlanDailyReport.setFixedBugCount(0);
        }
        if (testPlanDailyReport.getProgress() == null) {
            testPlanDailyReport.setProgress(BigDecimal.ZERO);
        }

        // 设置创建时间
        if (testPlanDailyReport.getCreatedAt() == null) {
            testPlanDailyReport.setCreatedAt(new Date());
        }

        // 设置创建人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanDailyReport.getCreatedBy() == null) {
                    testPlanDailyReport.setCreatedBy(userId);
                }
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanDailyReport);
    }
}
