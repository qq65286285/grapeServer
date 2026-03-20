package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanVersion;
import com.grape.grape.mapper.TestPlanVersionMapper;
import com.grape.grape.service.TestPlanVersionService;
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
 * 测试计划版本表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanVersionServiceImpl extends ServiceImpl<TestPlanVersionMapper, TestPlanVersion> implements TestPlanVersionService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanVersionServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanVersion> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanVersion> listByVersionType(Integer versionType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("version_type = ?", versionType)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanVersion> listByIsBaseline(Integer isBaseline) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_baseline = ?", isBaseline)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanVersion getByVersionNo(String versionNo) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("version_no = ?", versionNo);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanVersion> page(Page<TestPlanVersion> page, Long planId, Integer versionType, Integer isBaseline, Integer changeType) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (planId != null) {
            queryWrapper.where("plan_id = ?", planId);
        }

        if (versionType != null) {
            queryWrapper.and("version_type = ?", versionType);
        }

        if (isBaseline != null) {
            queryWrapper.and("is_baseline = ?", isBaseline);
        }

        if (changeType != null) {
            queryWrapper.and("change_type = ?", changeType);
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public TestPlanVersion getLatestVersion(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc")
                .limit(1);
        return getOne(queryWrapper);
    }

    @Override
    public List<TestPlanVersion> listBaselineVersions(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_baseline = 1", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public boolean setAsBaseline(Long id, String baselineName) {
        TestPlanVersion version = getById(id);
        if (version != null) {
            // 先将该计划的所有版本设置为非基线
            List<TestPlanVersion> versions = listByPlanId(version.getPlanId());
            for (TestPlanVersion v : versions) {
                v.setIsBaseline(0);
                v.setBaselineName(null);
                updateById(v);
            }

            // 将当前版本设置为基线
            version.setIsBaseline(1);
            version.setBaselineName(baselineName);
            return updateById(version);
        }
        return false;
    }

    @Override
    public int batchDelete(List<Long> ids) {
        int successCount = 0;
        for (Long id : ids) {
            if (removeById(id)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public int deleteByPlanId(Long planId) {
        List<TestPlanVersion> versions = listByPlanId(planId);
        int successCount = 0;
        for (TestPlanVersion version : versions) {
            if (removeById(version.getId())) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public String compareVersions(Long versionId1, Long versionId2) {
        // 这里实现版本对比逻辑，返回对比结果
        // 实际实现中可能需要解析快照数据并进行差异比较
        TestPlanVersion version1 = getById(versionId1);
        TestPlanVersion version2 = getById(versionId2);
        if (version1 != null && version2 != null) {
            // 简单返回版本信息，实际实现需要更复杂的对比逻辑
            return "对比结果：版本1(" + version1.getVersionNo() + ") 与 版本2(" + version2.getVersionNo() + ")";
        }
        return "版本不存在";
    }

    @Override
    public boolean rollbackToVersion(Long planId, Long versionId) {
        // 这里实现回滚逻辑
        // 实际实现中可能需要从快照数据中恢复计划状态
        TestPlanVersion version = getById(versionId);
        if (version != null && version.getPlanId().equals(planId)) {
            // 简单返回成功，实际实现需要更复杂的回滚逻辑
            return true;
        }
        return false;
    }

    @Override
    public boolean save(TestPlanVersion version) {
        // 设置创建时间
        if (version.getCreatedAt() == null) {
            version.setCreatedAt(System.currentTimeMillis());
        }

        // 设置创建人
        if (version.getCreatedBy() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    version.setCreatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }
        }

        return super.save(version);
    }
}