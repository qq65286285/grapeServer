package com.grape.grape.service;

import com.grape.grape.entity.TestPlanExecuteStepAttachment;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 测试计划执行步骤附件表 服务层。
 *
 * @author Administrator
 * @since 2026-03-24
 */
public interface TestPlanExecuteStepAttachmentService extends IService<TestPlanExecuteStepAttachment> {

    /**
     * 根据执行步骤ID获取附件列表
     * @param executeStepId 执行步骤ID
     * @return 附件列表
     */
    List<TestPlanExecuteStepAttachment> getByExecuteStepId(Long executeStepId);

    /**
     * 根据执行记录ID获取附件列表
     * @param executeId 执行记录ID
     * @return 附件列表
     */
    List<TestPlanExecuteStepAttachment> getByExecuteId(Long executeId);

    /**
     * 保存执行步骤附件列表
     * @param executeStepId 执行步骤ID
     * @param executeId 执行记录ID
     * @param attachments 附件列表
     * @return 保存结果
     */
    boolean saveAttachments(Long executeStepId, Long executeId, List<TestPlanExecuteStepAttachment> attachments);

    /**
     * 删除执行步骤的所有附件
     * @param executeStepId 执行步骤ID
     * @return 删除结果
     */
    boolean removeByExecuteStepId(Long executeStepId);

    /**
     * 删除执行记录的所有附件
     * @param executeId 执行记录ID
     * @return 删除结果
     */
    boolean removeByExecuteId(Long executeId);
}