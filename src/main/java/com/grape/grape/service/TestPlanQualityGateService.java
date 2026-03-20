package com.grape.grape.service;

import com.grape.grape.entity.TestPlanQualityGate;
import com.mybatisflex.core.paginate.Page;
import java.math.BigDecimal;
import java.util.List;

/**
 * 测试计划质量门禁表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanQualityGateService extends MyBaseService<TestPlanQualityGate> {

    /**
     * 根据计划ID查询质量门禁列表
     *
     * @param planId 计划ID
     * @return 质量门禁列表
     */
    List<TestPlanQualityGate> listByPlanId(Long planId);

    /**
     * 根据门禁状态查询质量门禁列表
     *
     * @param gateStatus 门禁状态
     * @return 质量门禁列表
     */
    List<TestPlanQualityGate> listByGateStatus(Integer gateStatus);

    /**
     * 根据计划ID和门禁类型查询质量门禁
     *
     * @param planId 计划ID
     * @param gateType 门禁类型
     * @return 质量门禁
     */
    TestPlanQualityGate getByPlanIdAndGateType(Long planId, Integer gateType);

    /**
     * 分页查询质量门禁
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param gateType 门禁类型（可选）
     * @param gateStatus 门禁状态（可选）
     * @param isMandatory 是否强制（可选）
     * @return 分页结果
     */
    Page<TestPlanQualityGate> page(Page<TestPlanQualityGate> page, Long planId, Integer gateType, Integer gateStatus, Integer isMandatory);

    /**
     * 检查质量门禁
     *
     * @param id 门禁ID
     * @param currentValue 当前值
     * @return 检查结果（true-通过，false-不通过）
     */
    boolean checkQualityGate(Long id, BigDecimal currentValue);

    /**
     * 批量检查计划的所有质量门禁
     *
     * @param planId 计划ID
     * @return 检查结果（true-所有门禁通过，false-存在未通过门禁）
     */
    boolean checkAllQualityGates(Long planId);

    /**
     * 更新门禁状态
     *
     * @param id 门禁ID
     * @param gateStatus 门禁状态
     * @param currentValue 当前值
     * @return 是否更新成功
     */
    boolean updateGateStatus(Long id, Integer gateStatus, BigDecimal currentValue);

    /**
     * 获取计划的质量门禁状态统计
     *
     * @param planId 计划ID
     * @return 状态统计信息
     */
    QualityGateStatusStats getQualityGateStatusStats(Long planId);

    /**
     * 质量门禁状态统计
     */
    class QualityGateStatusStats {
        private int totalCount;        // 总门禁数
        private int passedCount;       // 通过数
        private int failedCount;       // 不通过数
        private int unCheckedCount;    // 未检查数
        private int mandatoryFailedCount; // 强制门禁不通过数

        // getter和setter方法
        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPassedCount() {
            return passedCount;
        }

        public void setPassedCount(int passedCount) {
            this.passedCount = passedCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }

        public int getUnCheckedCount() {
            return unCheckedCount;
        }

        public void setUnCheckedCount(int unCheckedCount) {
            this.unCheckedCount = unCheckedCount;
        }

        public int getMandatoryFailedCount() {
            return mandatoryFailedCount;
        }

        public void setMandatoryFailedCount(int mandatoryFailedCount) {
            this.mandatoryFailedCount = mandatoryFailedCount;
        }
    }
}