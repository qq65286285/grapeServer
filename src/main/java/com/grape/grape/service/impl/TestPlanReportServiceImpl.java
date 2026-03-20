package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanReport;
import com.grape.grape.mapper.TestPlanReportMapper;
import com.grape.grape.service.TestPlanReportService;
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
 * 测试计划报告表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanReportServiceImpl extends ServiceImpl<TestPlanReportMapper, TestPlanReport> implements TestPlanReportService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanReportServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanReport> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanReport> listByReportType(Integer reportType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("report_type = ? and is_deleted = 0", reportType)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanReport> listByStatus(Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("status = ? and is_deleted = 0", status)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanReport> listByApproveStatus(Integer approveStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("approve_status = ? and is_deleted = 0", approveStatus)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanReport getByReportNo(String reportNo) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("report_no = ? and is_deleted = 0", reportNo);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanReport> page(Page<TestPlanReport> page, Long planId, Integer reportType, Integer status, Integer approveStatus, Integer isPublished, Long startDate, Long endDate) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (reportType != null) {
            queryWrapper.and("report_type = ?", reportType);
        }

        if (status != null) {
            queryWrapper.and("status = ?", status);
        }

        if (approveStatus != null) {
            queryWrapper.and("approve_status = ?", approveStatus);
        }

        if (isPublished != null) {
            queryWrapper.and("is_published = ?", isPublished);
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
    public String generateReportNo(Long planId) {
        long timestamp = System.currentTimeMillis();
        return "RPT-" + planId + "-" + timestamp;
    }

    @Override
    public boolean submitForApproval(Long id) {
        TestPlanReport report = getById(id);
        if (report != null) {
            report.setStatus(2); // 2-待审批
            report.setApproveStatus(0); // 0-待审批
            report.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    report.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(report);
        }
        return false;
    }

    @Override
    public boolean approveReport(Long id, Integer approveStatus, String approveRemark) {
        TestPlanReport report = getById(id);
        if (report != null) {
            report.setApproveStatus(approveStatus);
            report.setApproveRemark(approveRemark);
            report.setApprovedAt(System.currentTimeMillis());
            
            // 设置审批人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    report.setApprovedBy(Long.parseLong(userIdStr));
                    report.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            // 如果审批通过，更新状态为已发布
            if (approveStatus == 1) { // 1-已通过
                report.setStatus(3); // 3-已发布
                report.setIsPublished(1); // 1-是
                report.setPublishedAt(System.currentTimeMillis());
                report.setPublishedBy(report.getApprovedBy());
            }

            report.setUpdatedAt(System.currentTimeMillis());
            return updateById(report);
        }
        return false;
    }

    @Override
    public boolean publishReport(Long id) {
        TestPlanReport report = getById(id);
        if (report != null) {
            report.setIsPublished(1); // 1-是
            report.setStatus(3); // 3-已发布
            report.setPublishedAt(System.currentTimeMillis());
            report.setUpdatedAt(System.currentTimeMillis());
            
            // 设置发布人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    report.setPublishedBy(Long.parseLong(userIdStr));
                    report.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(report);
        }
        return false;
    }

    @Override
    public boolean archiveReport(Long id) {
        TestPlanReport report = getById(id);
        if (report != null) {
            report.setStatus(4); // 4-已归档
            report.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    report.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(report);
        }
        return false;
    }

    @Override
    public TestPlanReport getLatestReport(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("created_at desc")
                .limit(1);
        List<TestPlanReport> list = list(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ReportStats getReportStats(Long planId) {
        List<TestPlanReport> reports = listByPlanId(planId);
        ReportStats stats = new ReportStats();

        stats.setTotalCount(reports.size());

        for (TestPlanReport report : reports) {
            switch (report.getStatus()) {
                case 1:
                    stats.setDraftCount(stats.getDraftCount() + 1);
                    break;
                case 2:
                    stats.setPendingCount(stats.getPendingCount() + 1);
                    break;
                case 3:
                    stats.setPublishedCount(stats.getPublishedCount() + 1);
                    break;
                case 4:
                    stats.setArchivedCount(stats.getArchivedCount() + 1);
                    break;
            }
        }

        return stats;
    }

    @Override
    public boolean save(TestPlanReport testPlanReport) {
        // 设置默认值
        if (testPlanReport.getReportVersion() == null) {
            testPlanReport.setReportVersion("V1.0");
        }
        if (testPlanReport.getGenerateTime() == null) {
            testPlanReport.setGenerateTime(System.currentTimeMillis());
        }
        if (testPlanReport.getTotalCaseCount() == null) {
            testPlanReport.setTotalCaseCount(0);
        }
        if (testPlanReport.getExecutedCaseCount() == null) {
            testPlanReport.setExecutedCaseCount(0);
        }
        if (testPlanReport.getPassCaseCount() == null) {
            testPlanReport.setPassCaseCount(0);
        }
        if (testPlanReport.getFailCaseCount() == null) {
            testPlanReport.setFailCaseCount(0);
        }
        if (testPlanReport.getBlockCaseCount() == null) {
            testPlanReport.setBlockCaseCount(0);
        }
        if (testPlanReport.getSkipCaseCount() == null) {
            testPlanReport.setSkipCaseCount(0);
        }
        if (testPlanReport.getTotalBugCount() == null) {
            testPlanReport.setTotalBugCount(0);
        }
        if (testPlanReport.getFatalBugCount() == null) {
            testPlanReport.setFatalBugCount(0);
        }
        if (testPlanReport.getSeriousBugCount() == null) {
            testPlanReport.setSeriousBugCount(0);
        }
        if (testPlanReport.getNormalBugCount() == null) {
            testPlanReport.setNormalBugCount(0);
        }
        if (testPlanReport.getMinorBugCount() == null) {
            testPlanReport.setMinorBugCount(0);
        }
        if (testPlanReport.getFixedBugCount() == null) {
            testPlanReport.setFixedBugCount(0);
        }
        if (testPlanReport.getTotalExecutorCount() == null) {
            testPlanReport.setTotalExecutorCount(0);
        }
        if (testPlanReport.getTotalDuration() == null) {
            testPlanReport.setTotalDuration(0);
        }
        if (testPlanReport.getAvgDuration() == null) {
            testPlanReport.setAvgDuration(0);
        }
        if (testPlanReport.getAttachmentCount() == null) {
            testPlanReport.setAttachmentCount(0);
        }
        if (testPlanReport.getApproveStatus() == null) {
            testPlanReport.setApproveStatus(0);
        }
        if (testPlanReport.getIsPublished() == null) {
            testPlanReport.setIsPublished(0);
        }
        if (testPlanReport.getStatus() == null) {
            testPlanReport.setStatus(1); // 1-草稿
        }
        if (testPlanReport.getIsDeleted() == null) {
            testPlanReport.setIsDeleted(0);
        }

        // 生成报告编号
        if (testPlanReport.getReportNo() == null && testPlanReport.getPlanId() != null) {
            testPlanReport.setReportNo(generateReportNo(testPlanReport.getPlanId()));
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanReport.getCreatedAt() == null) {
            testPlanReport.setCreatedAt(now);
        }
        if (testPlanReport.getUpdatedAt() == null) {
            testPlanReport.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanReport.getCreatedBy() == null) {
                    testPlanReport.setCreatedBy(userId);
                }
                testPlanReport.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanReport);
    }

    @Override
    public boolean updateById(TestPlanReport testPlanReport) {
        // 设置更新时间
        testPlanReport.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                testPlanReport.setUpdatedBy(Long.parseLong(userIdStr));
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanReport);
    }

    public boolean removeById(Long id) {
        TestPlanReport report = getById(id);
        if (report != null) {
            report.setIsDeleted(1);
            report.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    report.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(report);
        }
        return false;
    }
}