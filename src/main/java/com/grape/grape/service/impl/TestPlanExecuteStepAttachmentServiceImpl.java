package com.grape.grape.service.impl;

import com.grape.grape.entity.TestPlanExecuteStepAttachment;
import com.grape.grape.mapper.TestPlanExecuteStepAttachmentMapper;
import com.grape.grape.service.TestPlanExecuteStepAttachmentService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划执行步骤附件表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-24
 */
@Service
public class TestPlanExecuteStepAttachmentServiceImpl extends ServiceImpl<TestPlanExecuteStepAttachmentMapper, TestPlanExecuteStepAttachment> implements TestPlanExecuteStepAttachmentService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanExecuteStepAttachmentServiceImpl.class);

    @Override
    public List<TestPlanExecuteStepAttachment> getByExecuteStepId(Long executeStepId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("execute_step_id = ? and is_deleted = 0", executeStepId);
        queryWrapper.orderBy("sort_order asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanExecuteStepAttachment> getByExecuteId(Long executeId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("execute_id = ? and is_deleted = 0", executeId);
        queryWrapper.orderBy("execute_step_id asc, sort_order asc");
        return list(queryWrapper);
    }

    @Override
    public boolean saveAttachments(Long executeStepId, Long executeId, List<TestPlanExecuteStepAttachment> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return true;
        }

        try {
            for (int i = 0; i < attachments.size(); i++) {
                TestPlanExecuteStepAttachment attachment = attachments.get(i);
                attachment.setExecuteStepId(executeStepId);
                attachment.setExecuteId(executeId);
                attachment.setSortOrder(i + 1);
                attachment.setIsDeleted(0);
                save(attachment);
            }
            return true;
        } catch (Exception e) {
            log.error("保存执行步骤附件失败", e);
            return false;
        }
    }

    @Override
    public boolean removeByExecuteStepId(Long executeStepId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("execute_step_id = ?", executeStepId);
        return remove(queryWrapper);
    }

    @Override
    public boolean removeByExecuteId(Long executeId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("execute_id = ?", executeId);
        return remove(queryWrapper);
    }
}