package com.grape.grape.service.impl;

import com.grape.grape.entity.TestPlanExecuteStep;
import com.grape.grape.mapper.TestPlanExecuteStepMapper;
import com.grape.grape.service.TestPlanExecuteStepService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 测试计划执行步骤记录表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanExecuteStepServiceImpl extends ServiceImpl<TestPlanExecuteStepMapper, TestPlanExecuteStep> implements TestPlanExecuteStepService {

    @Override
    public List<TestPlanExecuteStep> listByExecuteId(Long executeId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("execute_id = ?", executeId)
                .orderBy("step_no asc");
        return list(queryWrapper);
    }

    @Override
    public boolean saveBatch(List<TestPlanExecuteStep> steps) {
        // 设置创建时间和更新时间
        Date now = new Date();
        for (TestPlanExecuteStep step : steps) {
            if (step.getCreatedAt() == null) {
                step.setCreatedAt(now);
            }
            if (step.getUpdatedAt() == null) {
                step.setUpdatedAt(now);
            }
        }
        return super.saveBatch(steps);
    }

    @Override
    public boolean removeByExecuteId(Long executeId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("execute_id = ?", executeId);
        return remove(queryWrapper);
    }

    @Override
    public Page<TestPlanExecuteStep> page(Page<TestPlanExecuteStep> page, Long executeId) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (executeId != null) {
            queryWrapper.where("execute_id = ?", executeId);
        }

        queryWrapper.orderBy("step_no asc");

        return getMapper().paginate(page, queryWrapper);
    }
}
