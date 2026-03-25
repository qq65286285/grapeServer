package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanExecuteRecord;
import com.grape.grape.entity.TestPlanExecuteStep;
import com.grape.grape.entity.TestPlanExecuteStepAttachment;
import com.grape.grape.entity.TestPlanCaseSnapshot;
import com.grape.grape.entity.TestPlanCaseSnapshotStep;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanExecuteRecordService;
import com.grape.grape.service.TestPlanExecuteStepService;
import com.grape.grape.service.TestPlanExecuteStepAttachmentService;
import com.grape.grape.service.MinioService;
import com.grape.grape.service.TestPlanCaseSnapshotService;
import com.grape.grape.service.TestPlanCaseSnapshotStepService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试计划执行记录表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlanExecuteRecord")
public class TestPlanExecuteRecordController {

    @Autowired
    private TestPlanExecuteRecordService testPlanExecuteRecordService;

    @Autowired
    private TestPlanExecuteStepService testPlanExecuteStepService;

    @Autowired
    private TestPlanExecuteStepAttachmentService testPlanExecuteStepAttachmentService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private TestPlanCaseSnapshotService testPlanCaseSnapshotService;

    @Autowired
    private TestPlanCaseSnapshotStepService testPlanCaseSnapshotStepService;

    /**
     * 新增测试计划执行记录
     *
     * @param testPlanExecuteRecord 测试计划执行记录
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanExecuteRecord testPlanExecuteRecord) {
        boolean saved = testPlanExecuteRecordService.save(testPlanExecuteRecord);
        if (saved) {
            return Resp.ok(testPlanExecuteRecord);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划执行记录
     *
     * @param testPlanExecuteRecord 测试计划执行记录
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanExecuteRecord testPlanExecuteRecord) {
        boolean updated = testPlanExecuteRecordService.updateById(testPlanExecuteRecord);
        if (updated) {
            return Resp.ok(testPlanExecuteRecord);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划执行记录
     *
     * @param id 测试计划执行记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanExecuteRecord record = testPlanExecuteRecordService.getById(id);
        if (record != null) {
            record.setIsDeleted(1);
            boolean deleted = testPlanExecuteRecordService.updateById(record);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划执行记录
     *
     * @param id 测试计划执行记录ID
     * @return 测试计划执行记录
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanExecuteRecord testPlanExecuteRecord = testPlanExecuteRecordService.getById(id);
        if (testPlanExecuteRecord != null && testPlanExecuteRecord.getIsDeleted() == 0) {
            return Resp.ok(testPlanExecuteRecord);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划执行记录
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param snapshotId 快照ID（可选）
     * @param executorId 执行人ID（可选）
     * @param executeStatus 执行状态（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Long snapshotId,
                    @RequestParam(required = false) Long executorId, @RequestParam(required = false) Integer executeStatus) {
        Page<TestPlanExecuteRecord> result = testPlanExecuteRecordService.page(Page.of(page, size), planId, snapshotId, executorId, executeStatus);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询执行记录列表
     *
     * @param planId 计划ID
     * @return 执行记录列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanExecuteRecord> list = testPlanExecuteRecordService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据快照ID查询执行记录列表
     *
     * @param snapshotId 快照ID
     * @return 执行记录列表
     */
    @GetMapping("/listBySnapshotId/{snapshotId}")
    public Resp listBySnapshotId(@PathVariable Long snapshotId) {
        List<TestPlanExecuteRecord> list = testPlanExecuteRecordService.listBySnapshotId(snapshotId);
        return Resp.ok(list);
    }

    /**
     * 根据执行编号查询执行记录
     *
     * @param executeNo 执行编号
     * @return 执行记录
     */
    @GetMapping("/getByExecuteNo/{executeNo}")
    public Resp getByExecuteNo(@PathVariable String executeNo) {
        TestPlanExecuteRecord record = testPlanExecuteRecordService.getByExecuteNo(executeNo);
        if (record != null) {
            return Resp.ok(record);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据计划ID和执行状态查询执行记录列表
     *
     * @param planId 计划ID
     * @param executeStatus 执行状态
     * @return 执行记录列表
     */
    @GetMapping("/listByPlanIdAndStatus")
    public Resp listByPlanIdAndStatus(@RequestParam Long planId, @RequestParam Integer executeStatus) {
        List<TestPlanExecuteRecord> list = testPlanExecuteRecordService.listByPlanIdAndStatus(planId, executeStatus);
        return Resp.ok(list);
    }

    /**
     * 计算计划的执行统计
     *
     * @param planId 计划ID
     * @return 统计结果
     */
    @GetMapping("/calculateExecutionStats/{planId}")
    public Resp calculateExecutionStats(@PathVariable Long planId) {
        Map<String, Object> stats = testPlanExecuteRecordService.calculateExecutionStats(planId);
        return Resp.ok(stats);
    }

    /**
     * 审核执行记录
     *
     * @param id 执行记录ID
     * @param isReviewed 是否通过审核
     * @param reviewedBy 审核人ID
     * @param reviewComment 审核意见
     * @return 操作结果
     */
    @PostMapping("/reviewRecord/{id}")
    public Resp reviewRecord(@PathVariable Long id, @RequestParam Integer isReviewed,
                            @RequestParam Long reviewedBy, @RequestParam String reviewComment) {
        boolean reviewed = testPlanExecuteRecordService.reviewRecord(id, isReviewed, reviewedBy, reviewComment);
        if (reviewed) {
            return Resp.ok("审核成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据执行记录ID查询执行详情，包括步骤和对应的附件
     *
     * @param id 执行记录ID
     * @return 执行详情，包含执行记录、步骤列表和附件列表
     */
    @GetMapping("/getDetail/{id}")
    public Resp getDetail(@PathVariable Long id) {
        // 1. 获取执行记录
        TestPlanExecuteRecord executeRecord = testPlanExecuteRecordService.getById(id);
        if (executeRecord == null || executeRecord.getIsDeleted() == 1) {
            return Resp.info(404, "执行记录不存在");
        }

        // 2. 获取步骤列表
        List<TestPlanExecuteStep> steps = testPlanExecuteStepService.listByExecuteId(id);

        // 3. 如果步骤列表为空，从快照表中获取
        if (steps == null || steps.isEmpty()) {
            // 获取快照信息
            TestPlanCaseSnapshot snapshot = testPlanCaseSnapshotService.getById(executeRecord.getSnapshotId());
            if (snapshot != null) {
                // 从快照步骤表中获取步骤信息
                List<TestPlanCaseSnapshotStep> snapshotSteps = testPlanCaseSnapshotStepService.getBySnapshotId(executeRecord.getSnapshotId());
                if (snapshotSteps != null && !snapshotSteps.isEmpty()) {
                    // 将快照步骤转换为执行步骤格式
                    steps = new java.util.ArrayList<>();
                    for (TestPlanCaseSnapshotStep snapshotStep : snapshotSteps) {
                        TestPlanExecuteStep step = new TestPlanExecuteStep();
                        step.setExecuteId(executeRecord.getId());
                        step.setStepNo(snapshotStep.getStepNumber());
                        step.setStepDescription(snapshotStep.getStepDescription());
                        step.setExpectedResult(snapshotStep.getExpectedResult());
                        step.setActualResult("");
                        step.setExecuteStatus(0);
                        step.setRemark("");
                        step.setSnapshotStepId(snapshotStep.getId());
                        step.setCreatedAt(new java.util.Date());
                        step.setUpdatedAt(new java.util.Date());
                        steps.add(step);
                    }
                }
            }
        } else {
            // 如果步骤列表不为空，检查每个步骤的 expectedResult 是否为空，如果为空，从快照步骤表中获取
            for (TestPlanExecuteStep step : steps) {
                if (step.getExpectedResult() == null || step.getExpectedResult().isEmpty() || step.getStepDescription() == null || step.getStepDescription().isEmpty()) {
                    if (step.getSnapshotStepId() != null) {
                        // 通过 snapshotStepId 从快照步骤表中获取预期结果和步骤描述
                        TestPlanCaseSnapshotStep snapshotStep = testPlanCaseSnapshotStepService.getById(step.getSnapshotStepId());
                        if (snapshotStep != null) {
                            step.setExpectedResult(snapshotStep.getExpectedResult());
                            step.setStepDescription(snapshotStep.getStepDescription());
                        }
                    } else {
                        // 如果没有 snapshotStepId，尝试通过步骤序号从快照步骤表中获取
                        TestPlanCaseSnapshot snapshot = testPlanCaseSnapshotService.getById(executeRecord.getSnapshotId());
                        if (snapshot != null) {
                            List<TestPlanCaseSnapshotStep> snapshotSteps = testPlanCaseSnapshotStepService.getBySnapshotId(executeRecord.getSnapshotId());
                            if (snapshotSteps != null && !snapshotSteps.isEmpty()) {
                                for (TestPlanCaseSnapshotStep snapshotStep : snapshotSteps) {
                                    if (snapshotStep.getStepNumber() == step.getStepNo()) {
                                        step.setExpectedResult(snapshotStep.getExpectedResult());
                                        step.setStepDescription(snapshotStep.getStepDescription());
                                        step.setSnapshotStepId(snapshotStep.getId());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. 获取每个步骤的附件列表
        List<Map<String, Object>> stepsWithAttachments = new java.util.ArrayList<>();
        for (TestPlanExecuteStep step : steps) {
            Map<String, Object> stepMap = new java.util.HashMap<>();
            // 手动构建步骤信息，确保所有字段都被包含
            Map<String, Object> stepInfo = new java.util.HashMap<>();
            stepInfo.put("id", step.getId());
            stepInfo.put("executeId", step.getExecuteId());
            stepInfo.put("stepNo", step.getStepNo());
            stepInfo.put("stepDescription", step.getStepDescription());
            stepInfo.put("expectedResult", step.getExpectedResult());
            stepInfo.put("actualResult", step.getActualResult());
            stepInfo.put("executeStatus", step.getExecuteStatus());
            stepInfo.put("remark", step.getRemark());
            stepInfo.put("createdAt", step.getCreatedAt());
            stepInfo.put("updatedAt", step.getUpdatedAt());
            stepMap.put("step", stepInfo);
            // 获取步骤附件
            List<TestPlanExecuteStepAttachment> attachments = testPlanExecuteStepAttachmentService.getByExecuteStepId(step.getId());
            stepMap.put("attachments", attachments);
            stepsWithAttachments.add(stepMap);
        }

        // 5. 构建返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("executeRecord", executeRecord);
        result.put("steps", stepsWithAttachments);

        return Resp.ok(result);
    }

    /**
     * 获取执行步骤附件的访问URL
     *
     * @param attachmentId 附件ID
     * @return 附件的访问URL
     */
    @GetMapping("/getAttachmentUrl/{attachmentId}")
    public Resp getAttachmentUrl(@PathVariable Long attachmentId) {
        // 1. 获取附件信息
        TestPlanExecuteStepAttachment attachment = testPlanExecuteStepAttachmentService.getById(attachmentId);
        if (attachment == null) {
            return Resp.info(404, "附件不存在");
        }

        // 2. 获取附件的访问URL
        try {
            // 这里假设附件的 fileName 就是 Minio 中的对象名称
            String fileUrl = minioService.getObjectUrl(attachment.getFileName());
            return Resp.ok(fileUrl);
        } catch (Exception e) {
            return Resp.error();
        }
    }
}
