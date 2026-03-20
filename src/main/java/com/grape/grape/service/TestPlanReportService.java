package com.grape.grape.service;

import com.grape.grape.entity.TestPlanReport;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划报告表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanReportService extends MyBaseService<TestPlanReport> {

    /**
     * 根据计划ID查询报告列表
     *
     * @param planId 计划ID
     * @return 报告列表
     */
    List<TestPlanReport> listByPlanId(Long planId);

    /**
     * 根据报告类型查询报告列表
     *
     * @param reportType 报告类型
     * @return 报告列表
     */
    List<TestPlanReport> listByReportType(Integer reportType);

    /**
     * 根据状态查询报告列表
     *
     * @param status 状态
     * @return 报告列表
     */
    List<TestPlanReport> listByStatus(Integer status);

    /**
     * 根据审批状态查询报告列表
     *
     * @param approveStatus 审批状态
     * @return 报告列表
     */
    List<TestPlanReport> listByApproveStatus(Integer approveStatus);

    /**
     * 根据报告编号查询报告
     *
     * @param reportNo 报告编号
     * @return 报告
     */
    TestPlanReport getByReportNo(String reportNo);

    /**
     * 分页查询报告
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param reportType 报告类型（可选）
     * @param status 状态（可选）
     * @param approveStatus 审批状态（可选）
     * @param isPublished 是否发布（可选）
     * @param startDate 开始时间（可选，毫秒级时间戳）
     * @param endDate 结束时间（可选，毫秒级时间戳）
     * @return 分页结果
     */
    Page<TestPlanReport> page(Page<TestPlanReport> page, Long planId, Integer reportType, Integer status, Integer approveStatus, Integer isPublished, Long startDate, Long endDate);

    /**
     * 生成报告编号
     *
     * @param planId 计划ID
     * @return 报告编号
     */
    String generateReportNo(Long planId);

    /**
     * 提交审批
     *
     * @param id 报告ID
     * @return 是否提交成功
     */
    boolean submitForApproval(Long id);

    /**
     * 审批报告
     *
     * @param id 报告ID
     * @param approveStatus 审批状态
     * @param approveRemark 审批意见
     * @return 是否审批成功
     */
    boolean approveReport(Long id, Integer approveStatus, String approveRemark);

    /**
     * 发布报告
     *
     * @param id 报告ID
     * @return 是否发布成功
     */
    boolean publishReport(Long id);

    /**
     * 归档报告
     *
     * @param id 报告ID
     * @return 是否归档成功
     */
    boolean archiveReport(Long id);

    /**
     * 获取计划的最新报告
     *
     * @param planId 计划ID
     * @return 最新报告
     */
    TestPlanReport getLatestReport(Long planId);

    /**
     * 获取计划的报告统计
     *
     * @param planId 计划ID
     * @return 报告统计信息
     */
    ReportStats getReportStats(Long planId);

    /**
     * 报告统计
     */
    class ReportStats {
        private int totalCount;        // 总报告数
        private int draftCount;        // 草稿数
        private int pendingCount;      // 待审批数
        private int publishedCount;    // 已发布数
        private int archivedCount;     // 已归档数

        // getter和setter方法
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getDraftCount() {
            return draftCount;
        }

        public void setDraftCount(int draftCount) {
            this.draftCount = draftCount;
        }

        public int getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(int pendingCount) {
            this.pendingCount = pendingCount;
        }

        public int getPublishedCount() {
            return publishedCount;
        }

        public void setPublishedCount(int publishedCount) {
            this.publishedCount = publishedCount;
        }

        public int getArchivedCount() {
            return archivedCount;
        }

        public void setArchivedCount(int archivedCount) {
            this.archivedCount = archivedCount;
        }
    }
}