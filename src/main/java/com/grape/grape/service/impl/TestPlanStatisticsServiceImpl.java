package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanStatistics;
import com.grape.grape.mapper.TestPlanStatisticsMapper;
import com.grape.grape.service.TestPlanStatisticsService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 测试计划统计表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanStatisticsServiceImpl extends ServiceImpl<TestPlanStatisticsMapper, TestPlanStatistics> implements TestPlanStatisticsService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanStatisticsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanStatistics> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("stat_date desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanStatistics> listByPlanIdAndStatType(Long planId, Integer statType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and stat_type = ? and is_deleted = 0", planId, statType)
                .orderBy("stat_date desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanStatistics getByPlanIdAndDateAndType(Long planId, Date statDate, Integer statType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and stat_date = ? and stat_type = ? and is_deleted = 0", planId, statDate, statType);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanStatistics> page(Page<TestPlanStatistics> page, Long planId, Integer statType, String qualityLevel, Date startDate, Date endDate) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (statType != null) {
            queryWrapper.and("stat_type = ?", statType);
        }

        if (qualityLevel != null) {
            queryWrapper.and("quality_level = ?", qualityLevel);
        }

        if (startDate != null) {
            queryWrapper.and("stat_date >= ?", startDate);
        }

        if (endDate != null) {
            queryWrapper.and("stat_date <= ?", endDate);
        }

        queryWrapper.orderBy("stat_date desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public TestPlanStatistics getLatestStatistics(Long planId, Integer statType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and stat_type = ? and is_deleted = 0", planId, statType)
                .orderBy("stat_date desc")
                .limit(1);
        List<TestPlanStatistics> list = list(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public TestPlanStatistics generateDailyStatistics(Long planId, Date statDate) {
        // 检查是否已存在该日期的统计
        TestPlanStatistics existing = getByPlanIdAndDateAndType(planId, statDate, 1);
        if (existing != null) {
            return existing;
        }

        // 生成新的统计信息
        TestPlanStatistics statistics = new TestPlanStatistics();
        statistics.setPlanId(planId);
        statistics.setStatDate(statDate);
        statistics.setStatType(1); // 1-每日统计

        // 这里需要根据实际情况计算各项统计数据
        // 暂时设置为默认值
        setDefaultValues(statistics);

        // 计算质量评分
        double qualityScore = calculateQualityScore(statistics);
        statistics.setQualityScore(new BigDecimal(qualityScore));
        statistics.setQualityLevel(getQualityLevel(qualityScore));

        // 保存统计信息
        save(statistics);
        return statistics;
    }

    @Override
    public TestPlanStatistics generateRealTimeStatistics(Long planId) {
        // 生成实时统计信息
        TestPlanStatistics statistics = new TestPlanStatistics();
        statistics.setPlanId(planId);
        statistics.setStatDate(new Date());
        statistics.setStatType(4); // 4-实时统计

        // 这里需要根据实际情况计算各项统计数据
        // 暂时设置为默认值
        setDefaultValues(statistics);

        // 计算质量评分
        double qualityScore = calculateQualityScore(statistics);
        statistics.setQualityScore(new BigDecimal(qualityScore));
        statistics.setQualityLevel(getQualityLevel(qualityScore));

        // 保存统计信息
        save(statistics);
        return statistics;
    }

    @Override
    public List<TestPlanStatistics> getStatisticsTrend(Long planId, Integer statType, int days) {
        // 计算开始日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        Date startDate = calendar.getTime();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and stat_type = ? and stat_date >= ? and is_deleted = 0", planId, statType, startDate)
                .orderBy("stat_date asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanStatistics> getQualityScoreTrend(Long planId, Integer statType, int days) {
        return getStatisticsTrend(planId, statType, days);
    }

    @Override
    public double calculateQualityScore(TestPlanStatistics statistics) {
        // 简单的质量评分计算逻辑
        // 可以根据实际需求调整权重
        double score = 0.0;

        // 执行率权重 30%
        if (statistics.getExecuteRate() != null) {
            score += statistics.getExecuteRate().doubleValue() * 0.3;
        }

        // 通过率权重 40%
        if (statistics.getPassRate() != null) {
            score += statistics.getPassRate().doubleValue() * 0.4;
        }

        // 缺陷修复率权重 20%
        if (statistics.getTotalBugCount() != null && statistics.getTotalBugCount() > 0) {
            double fixRate = (double) statistics.getFixedBugCount() / statistics.getTotalBugCount();
            score += fixRate * 100 * 0.2;
        }

        // 活跃执行人数权重 10%
        if (statistics.getActiveExecutorCount() != null) {
            score += Math.min(statistics.getActiveExecutorCount() * 10, 10);
        }

        return Math.min(score, 100.0);
    }

    @Override
    public String getQualityLevel(double qualityScore) {
        if (qualityScore >= 90) {
            return "A";
        } else if (qualityScore >= 80) {
            return "B";
        } else if (qualityScore >= 60) {
            return "C";
        } else {
            return "D";
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(TestPlanStatistics statistics) {
        if (statistics.getTotalCaseCount() == null) {
            statistics.setTotalCaseCount(0);
        }
        if (statistics.getExecutedCaseCount() == null) {
            statistics.setExecutedCaseCount(0);
        }
        if (statistics.getUnexecutedCaseCount() == null) {
            statistics.setUnexecutedCaseCount(0);
        }
        if (statistics.getPassCaseCount() == null) {
            statistics.setPassCaseCount(0);
        }
        if (statistics.getFailCaseCount() == null) {
            statistics.setFailCaseCount(0);
        }
        if (statistics.getBlockCaseCount() == null) {
            statistics.setBlockCaseCount(0);
        }
        if (statistics.getSkipCaseCount() == null) {
            statistics.setSkipCaseCount(0);
        }
        if (statistics.getExecuteRate() == null) {
            statistics.setExecuteRate(BigDecimal.ZERO);
        }
        if (statistics.getPassRate() == null) {
            statistics.setPassRate(BigDecimal.ZERO);
        }
        if (statistics.getFailRate() == null) {
            statistics.setFailRate(BigDecimal.ZERO);
        }
        if (statistics.getNewBugCount() == null) {
            statistics.setNewBugCount(0);
        }
        if (statistics.getTotalBugCount() == null) {
            statistics.setTotalBugCount(0);
        }
        if (statistics.getFixedBugCount() == null) {
            statistics.setFixedBugCount(0);
        }
        if (statistics.getOpenBugCount() == null) {
            statistics.setOpenBugCount(0);
        }
        if (statistics.getFatalBugCount() == null) {
            statistics.setFatalBugCount(0);
        }
        if (statistics.getSeriousBugCount() == null) {
            statistics.setSeriousBugCount(0);
        }
        if (statistics.getNormalBugCount() == null) {
            statistics.setNormalBugCount(0);
        }
        if (statistics.getMinorBugCount() == null) {
            statistics.setMinorBugCount(0);
        }
        if (statistics.getActiveExecutorCount() == null) {
            statistics.setActiveExecutorCount(0);
        }
        if (statistics.getTotalExecuteTimes() == null) {
            statistics.setTotalExecuteTimes(0);
        }
        if (statistics.getTotalDuration() == null) {
            statistics.setTotalDuration(0);
        }
        if (statistics.getAvgDuration() == null) {
            statistics.setAvgDuration(0);
        }
        if (statistics.getDailyNewExecute() == null) {
            statistics.setDailyNewExecute(0);
        }
        if (statistics.getDailyNewPass() == null) {
            statistics.setDailyNewPass(0);
        }
        if (statistics.getDailyNewFail() == null) {
            statistics.setDailyNewFail(0);
        }
        if (statistics.getDailyNewBug() == null) {
            statistics.setDailyNewBug(0);
        }
        if (statistics.getQualityScore() == null) {
            statistics.setQualityScore(BigDecimal.ZERO);
        }
        if (statistics.getIsDeleted() == null) {
            statistics.setIsDeleted(0);
        }
    }

    @Override
    public boolean save(TestPlanStatistics testPlanStatistics) {
        // 设置默认值
        setDefaultValues(testPlanStatistics);

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanStatistics.getCreatedAt() == null) {
            testPlanStatistics.setCreatedAt(now);
        }
        if (testPlanStatistics.getUpdatedAt() == null) {
            testPlanStatistics.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanStatistics.getCreatedBy() == null) {
                    testPlanStatistics.setCreatedBy(userId);
                }
                testPlanStatistics.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanStatistics);
    }

    @Override
    public boolean updateById(TestPlanStatistics testPlanStatistics) {
        // 设置更新时间
        testPlanStatistics.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                testPlanStatistics.setUpdatedBy(Long.parseLong(userIdStr));
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanStatistics);
    }

    public boolean removeById(Long id) {
        TestPlanStatistics statistics = getById(id);
        if (statistics != null) {
            statistics.setIsDeleted(1);
            statistics.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    statistics.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(statistics);
        }
        return false;
    }
}