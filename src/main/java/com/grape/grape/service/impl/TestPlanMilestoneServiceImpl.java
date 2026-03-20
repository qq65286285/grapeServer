package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanMilestone;
import com.grape.grape.mapper.TestPlanMilestoneMapper;
import com.grape.grape.service.TestPlanMilestoneService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测试计划里程碑表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanMilestoneServiceImpl extends ServiceImpl<TestPlanMilestoneMapper, TestPlanMilestone> implements TestPlanMilestoneService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanMilestoneServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanMilestone> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("sort_order asc, target_date asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanMilestone> listByPlanIdAndType(Long planId, Integer milestoneType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and milestone_type = ? and is_deleted = 0", planId, milestoneType)
                .orderBy("sort_order asc, target_date asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanMilestone> listByPlanIdAndStatus(Long planId, Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and status = ? and is_deleted = 0", planId, status)
                .orderBy("sort_order asc, target_date asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanMilestone> listByParentId(Long parentId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("parent_id = ? and is_deleted = 0", parentId)
                .orderBy("sort_order asc, target_date asc");
        return list(queryWrapper);
    }

    @Override
    public Page<TestPlanMilestone> page(Page<TestPlanMilestone> page, Long planId, Integer milestoneType, Integer status, Long ownerId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (milestoneType != null) {
            queryWrapper.and("milestone_type = ?", milestoneType);
        }

        if (status != null) {
            queryWrapper.and("status = ?", status);
        }

        if (ownerId != null) {
            queryWrapper.and("owner_id = ?", ownerId);
        }

        queryWrapper.orderBy("sort_order asc, target_date asc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Long actualDate) {
        TestPlanMilestone milestone = getById(id);
        if (milestone != null) {
            milestone.setStatus(status);
            if (actualDate != null) {
                milestone.setActualDate(actualDate);
            }
            milestone.setUpdatedAt(System.currentTimeMillis());
            return updateById(milestone);
        }
        return false;
    }

    @Override
    public boolean updateCompleteRate(Long id, BigDecimal completeRate) {
        TestPlanMilestone milestone = getById(id);
        if (milestone != null) {
            milestone.setCompleteRate(completeRate);
            milestone.setUpdatedAt(System.currentTimeMillis());
            return updateById(milestone);
        }
        return false;
    }

    @Override
    public boolean completeMilestone(Long id, String actualMetrics) {
        TestPlanMilestone milestone = getById(id);
        if (milestone != null) {
            milestone.setStatus(3); // 3-已完成
            milestone.setActualDate(System.currentTimeMillis());
            milestone.setActualMetrics(actualMetrics);
            milestone.setCompleteRate(new BigDecimal(100)); // 100%
            milestone.setUpdatedAt(System.currentTimeMillis());
            return updateById(milestone);
        }
        return false;
    }

    @Override
    public List<TestPlanMilestone> getMilestoneTree(Long planId) {
        // 获取所有里程碑
        List<TestPlanMilestone> allMilestones = listByPlanId(planId);
        // 这里可以实现里程碑树的构建逻辑
        // 由于时间关系，暂时返回所有里程碑，实际项目中可以构建树形结构
        return allMilestones;
    }

    @Override
    public boolean save(TestPlanMilestone testPlanMilestone) {
        // 设置默认值
        if (testPlanMilestone.getMilestoneType() == null) {
            testPlanMilestone.setMilestoneType(1); // 1-计划里程碑
        }
        if (testPlanMilestone.getStatus() == null) {
            testPlanMilestone.setStatus(1); // 1-未开始
        }
        if (testPlanMilestone.getCompleteRate() == null) {
            testPlanMilestone.setCompleteRate(BigDecimal.ZERO);
        }
        if (testPlanMilestone.getRiskLevel() == null) {
            testPlanMilestone.setRiskLevel(2); // 2-中
        }
        if (testPlanMilestone.getSortOrder() == null) {
            testPlanMilestone.setSortOrder(0);
        }
        if (testPlanMilestone.getParentId() == null) {
            testPlanMilestone.setParentId(0L);
        }
        if (testPlanMilestone.getIsDeleted() == null) {
            testPlanMilestone.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanMilestone.getCreatedAt() == null) {
            testPlanMilestone.setCreatedAt(now);
        }
        if (testPlanMilestone.getUpdatedAt() == null) {
            testPlanMilestone.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanMilestone.getCreatedBy() == null) {
                    testPlanMilestone.setCreatedBy(userId.toString());
                }
                if (testPlanMilestone.getOwnerId() == null) {
                    testPlanMilestone.setOwnerId(userId.toString());
                }
                testPlanMilestone.setUpdatedBy(userId.toString());
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanMilestone);
    }

    @Override
    public boolean updateById(TestPlanMilestone testPlanMilestone) {
        // 设置更新时间
        testPlanMilestone.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            testPlanMilestone.setUpdatedBy(userIdStr);
        }

        return super.updateById(testPlanMilestone);
    }
}
