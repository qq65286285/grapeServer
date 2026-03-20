package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanQualityGate;
import com.grape.grape.mapper.TestPlanQualityGateMapper;
import com.grape.grape.service.TestPlanQualityGateService;
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
 * 测试计划质量门禁表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanQualityGateServiceImpl extends ServiceImpl<TestPlanQualityGateMapper, TestPlanQualityGate> implements TestPlanQualityGateService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanQualityGateServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanQualityGate> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanQualityGate> listByGateStatus(Integer gateStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("gate_status = ?", gateStatus)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanQualityGate getByPlanIdAndGateType(Long planId, Integer gateType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and gate_type = ?", planId, gateType);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanQualityGate> page(Page<TestPlanQualityGate> page, Long planId, Integer gateType, Integer gateStatus, Integer isMandatory) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (planId != null) {
            queryWrapper.where("plan_id = ?", planId);
        }

        if (gateType != null) {
            queryWrapper.and("gate_type = ?", gateType);
        }

        if (gateStatus != null) {
            queryWrapper.and("gate_status = ?", gateStatus);
        }

        if (isMandatory != null) {
            queryWrapper.and("is_mandatory = ?", isMandatory);
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean checkQualityGate(Long id, BigDecimal currentValue) {
        TestPlanQualityGate gate = getById(id);
        if (gate == null) {
            return false;
        }

        boolean passed = evaluateCondition(currentValue, gate.getThresholdValue(), gate.getConditionOperator());
        int status = passed ? 2 : 3; // 2-通过, 3-不通过

        // 更新门禁状态
        gate.setCurrentValue(currentValue);
        gate.setGateStatus(status);
        gate.setCheckTime(new Date());
        gate.setUpdatedAt(new Date());

        return updateById(gate);
    }

    @Override
    public boolean checkAllQualityGates(Long planId) {
        List<TestPlanQualityGate> gates = listByPlanId(planId);
        boolean allPassed = true;

        for (TestPlanQualityGate gate : gates) {
            // 这里需要根据实际情况计算currentValue
            // 暂时使用阈值作为当前值进行演示
            BigDecimal currentValue = gate.getThresholdValue();
            boolean passed = checkQualityGate(gate.getId(), currentValue);
            if (!passed) {
                allPassed = false;
            }
        }

        return allPassed;
    }

    @Override
    public boolean updateGateStatus(Long id, Integer gateStatus, BigDecimal currentValue) {
        TestPlanQualityGate gate = getById(id);
        if (gate != null) {
            gate.setGateStatus(gateStatus);
            gate.setCurrentValue(currentValue);
            gate.setCheckTime(new Date());
            gate.setUpdatedAt(new Date());
            return updateById(gate);
        }
        return false;
    }

    @Override
    public QualityGateStatusStats getQualityGateStatusStats(Long planId) {
        List<TestPlanQualityGate> gates = listByPlanId(planId);
        QualityGateStatusStats stats = new QualityGateStatusStats();

        stats.setTotalCount(gates.size());

        for (TestPlanQualityGate gate : gates) {
            switch (gate.getGateStatus()) {
                case 1:
                    stats.setUnCheckedCount(stats.getUnCheckedCount() + 1);
                    break;
                case 2:
                    stats.setPassedCount(stats.getPassedCount() + 1);
                    break;
                case 3:
                    stats.setFailedCount(stats.getFailedCount() + 1);
                    if (gate.getIsMandatory() == 1) {
                        stats.setMandatoryFailedCount(stats.getMandatoryFailedCount() + 1);
                    }
                    break;
            }
        }

        return stats;
    }

    /**
     * 评估条件是否满足
     *
     * @param currentValue 当前值
     * @param thresholdValue 阈值
     * @param operator 运算符
     * @return 是否满足条件
     */
    private boolean evaluateCondition(BigDecimal currentValue, BigDecimal thresholdValue, String operator) {
        if (currentValue == null || thresholdValue == null) {
            return false;
        }

        switch (operator) {
            case ">=":
                return currentValue.compareTo(thresholdValue) >= 0;
            case "<=":
                return currentValue.compareTo(thresholdValue) <= 0;
            case "=":
                return currentValue.compareTo(thresholdValue) == 0;
            case ">":
                return currentValue.compareTo(thresholdValue) > 0;
            case "<":
                return currentValue.compareTo(thresholdValue) < 0;
            default:
                return false;
        }
    }

    @Override
    public boolean save(TestPlanQualityGate testPlanQualityGate) {
        // 设置默认值
        if (testPlanQualityGate.getGateStatus() == null) {
            testPlanQualityGate.setGateStatus(1); // 1-未检查
        }
        if (testPlanQualityGate.getIsMandatory() == null) {
            testPlanQualityGate.setIsMandatory(1); // 1-是
        }

        // 设置时间戳
        if (testPlanQualityGate.getCreatedAt() == null) {
            testPlanQualityGate.setCreatedAt(new Date());
        }
        if (testPlanQualityGate.getUpdatedAt() == null) {
            testPlanQualityGate.setUpdatedAt(new Date());
        }

        // 设置创建人
        if (testPlanQualityGate.getCreatedBy() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    testPlanQualityGate.setCreatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }
        }

        return super.save(testPlanQualityGate);
    }

    @Override
    public boolean updateById(TestPlanQualityGate testPlanQualityGate) {
        // 设置更新时间
        testPlanQualityGate.setUpdatedAt(new Date());

        return super.updateById(testPlanQualityGate);
    }
}