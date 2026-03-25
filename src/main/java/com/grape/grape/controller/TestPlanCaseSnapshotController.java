package com.grape.grape.controller;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanCaseSnapshot;
import com.grape.grape.entity.TestPlanCaseSnapshotStep;
import com.grape.grape.entity.TestPlanExecuteRecord;
import com.grape.grape.entity.TestPlanExecuteStep;
import com.grape.grape.entity.TestPlanExecuteStepAttachment;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanCaseSnapshotService;
import com.grape.grape.service.TestPlanExecuteRecordService;
import com.grape.grape.service.TestPlanExecuteStepService;
import com.grape.grape.service.TestPlanExecuteStepAttachmentService;
import com.grape.grape.service.TestPlanService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;

/**
 * 计划用例快照表-用例绑定到计划时的版本快照 控制层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlanCaseSnapshot")
public class TestPlanCaseSnapshotController {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCaseSnapshotController.class);

    @Autowired
    private TestPlanCaseSnapshotService testPlanCaseSnapshotService;

    @Autowired
    private TestPlanExecuteRecordService testPlanExecuteRecordService;

    @Autowired
    private TestPlanExecuteStepService testPlanExecuteStepService;

    @Autowired
    private TestPlanService testPlanService;

    @Autowired
    private UserService userService;

    @Autowired
    private com.grape.grape.service.TestPlanCaseSnapshotStepService testPlanCaseSnapshotStepService;

    @Autowired
    private TestPlanExecuteStepAttachmentService testPlanExecuteStepAttachmentService;

    /**
     * 添加计划用例快照
     *
     * @param testPlanCaseSnapshot 计划用例快照
     * @return 添加结果
     */
    @PostMapping("save")
    public Resp save(@RequestBody TestPlanCaseSnapshot testPlanCaseSnapshot) {
        boolean result = testPlanCaseSnapshotService.save(testPlanCaseSnapshot);
        if (result) {
            return Resp.ok("添加成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键删除计划用例快照
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean result = testPlanCaseSnapshotService.removeById(id);
        if (result) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键更新计划用例快照
     *
     * @param testPlanCaseSnapshot 计划用例快照
     * @return 更新结果
     */
    @PutMapping("update")
    public Resp update(@RequestBody TestPlanCaseSnapshot testPlanCaseSnapshot) {
        boolean result = testPlanCaseSnapshotService.updateById(testPlanCaseSnapshot);
        if (result) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键查询计划用例快照
     *
     * @param id 主键
     * @return 计划用例快照
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Long id) {
        TestPlanCaseSnapshot testPlanCaseSnapshot = testPlanCaseSnapshotService.getById(id);
        if (testPlanCaseSnapshot != null) {
            // 获取快照的步骤信息
            List<com.grape.grape.entity.TestPlanCaseSnapshotStep> steps = testPlanCaseSnapshotStepService.getBySnapshotId(id);
            // 构建响应数据
            Map<String, Object> responseData = new java.util.HashMap<>();
            responseData.put("snapshot", testPlanCaseSnapshot);
            responseData.put("steps", steps);
            return Resp.ok(responseData);
        } else {
            return Resp.info(404, "计划用例快照不存在");
        }
    }

    /**
     * 查询所有计划用例快照
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.list();
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 批量删除计划用例快照
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Long> ids) {
        boolean result = testPlanCaseSnapshotService.removeByIds(ids);
        if (result) {
            return Resp.ok("批量删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据条件查询计划用例快照
     */
    @PostMapping("listByCondition")
    public Resp listByCondition(@RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");

        // 构建查询条件
        if (params != null) {
            if (params.containsKey("planId")) {
                queryWrapper.and("plan_id = ?", params.get("planId"));
            }
            if (params.containsKey("originalCaseId")) {
                queryWrapper.and("original_case_id = ?", params.get("originalCaseId"));
            }
            if (params.containsKey("caseNumber")) {
                queryWrapper.and("case_number like ?", "%" + params.get("caseNumber") + "%");
            }
            if (params.containsKey("title")) {
                queryWrapper.and("title like ?", "%" + params.get("title") + "%");
            }
            if (params.containsKey("priority")) {
                queryWrapper.and("priority = ?", params.get("priority"));
            }
            if (params.containsKey("executeStatus")) {
                queryWrapper.and("execute_status = ?", params.get("executeStatus"));
            }
            if (params.containsKey("module")) {
                queryWrapper.and("module like ?", "%" + params.get("module") + "%");
            }
            if (params.containsKey("executorId")) {
                queryWrapper.and("executor_id = ?", params.get("executorId"));
            }
            if (params.containsKey("groupName")) {
                queryWrapper.and("group_name like ?", "%" + params.get("groupName") + "%");
            }
            if (params.containsKey("batchNo")) {
                queryWrapper.and("batch_no = ?", params.get("batchNo"));
            }
        }

        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.list(queryWrapper);
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 根据计划ID查询快照列表
     */
    @GetMapping("listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByPlanId(planId);
        
        // 计算汇总信息
        int totalCount = testPlanCaseSnapshots.size();
        int executedCount = 0;
        int passCount = 0;
        int failCount = 0;
        int unexecutedCount = 0;
        
        for (TestPlanCaseSnapshot snapshot : testPlanCaseSnapshots) {
            Integer executeStatus = snapshot.getExecuteStatus();
            if (executeStatus == 0) {
                unexecutedCount++;
            } else {
                executedCount++;
                if (executeStatus == 1) {
                    passCount++;
                } else if (executeStatus == 2) {
                    failCount++;
                }
            }
        }
        
        // 计算百分比
        final double executeRate = totalCount > 0 ? (double) executedCount / totalCount * 100 : 0;
        final double passRate = executedCount > 0 ? (double) passCount / executedCount * 100 : 0;
        final double failRate = executedCount > 0 ? (double) failCount / executedCount * 100 : 0;
        final double unexecutedRate = totalCount > 0 ? (double) unexecutedCount / totalCount * 100 : 0;
        final int finalTotalCount = totalCount;
        final int finalExecutedCount = executedCount;
        final int finalPassCount = passCount;
        final int finalFailCount = failCount;
        final int finalUnexecutedCount = unexecutedCount;
        
        // 构建响应数据
        Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("cases", testPlanCaseSnapshots);
        responseData.put("summary", new java.util.HashMap<String, Object>() {
            {
                put("totalCount", finalTotalCount);
                put("executedCount", finalExecutedCount);
                put("passCount", finalPassCount);
                put("failCount", finalFailCount);
                put("unexecutedCount", finalUnexecutedCount);
                put("executeRate", String.format("%.2f%%", executeRate));
                put("passRate", String.format("%.2f%%", passRate));
                put("failRate", String.format("%.2f%%", failRate));
                put("unexecutedRate", String.format("%.2f%%", unexecutedRate));
            }
        });
        
        return Resp.ok(responseData);
    }

    /**
     * 根据原用例ID查询快照列表
     */
    @GetMapping("listByOriginalCaseId/{originalCaseId}")
    public Resp listByOriginalCaseId(@PathVariable Integer originalCaseId) {
        List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByOriginalCaseId(originalCaseId);
        return Resp.ok(testPlanCaseSnapshots);
    }

    /**
     * 根据执行状态查询快照列表
     */
    @PostMapping("listByExecuteStatus")
    public Resp listByExecuteStatus(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("executeStatus")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            Integer executeStatus = Integer.parseInt(params.get("executeStatus").toString());
            List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByExecuteStatus(planId, executeStatus);
            return Resp.ok(testPlanCaseSnapshots);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和executeStatus");
        }
    }

    /**
     * 根据模块查询快照列表
     */
    @PostMapping("listByModule")
    public Resp listByModule(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("module")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            String module = params.get("module").toString();
            List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByModule(planId, module);
            return Resp.ok(testPlanCaseSnapshots);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和module");
        }
    }

    /**
     * 根据执行人查询快照列表
     */
    @PostMapping("listByExecutorId")
    public Resp listByExecutorId(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("executorId")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            String executorId = params.get("executorId").toString();
            List<TestPlanCaseSnapshot> testPlanCaseSnapshots = testPlanCaseSnapshotService.listByExecutorId(planId, executorId);
            return Resp.ok(testPlanCaseSnapshots);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和executorId");
        }
    }

    /**
     * 更新执行状态
     */
    @PostMapping("updateExecuteStatus")
    public Resp updateExecuteStatus(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("id") && params.containsKey("executeStatus")) {
            Long id = Long.parseLong(params.get("id").toString());
            Integer executeStatus = Integer.parseInt(params.get("executeStatus").toString());
            String executorId = params.containsKey("executorId") ? params.get("executorId").toString() : null;

            boolean result = testPlanCaseSnapshotService.updateExecuteStatus(id, executeStatus, executorId);
            if (result) {
                return Resp.ok("更新执行状态成功");
            } else {
                return Resp.error();
            }
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含id和executeStatus");
        }
    }

    /**
     * 批量绑定测试用例到测试计划
     */
    @PostMapping("batchBind")
    public Resp batchBind(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId") && params.containsKey("caseIds")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            List<Integer> caseIds = (List<Integer>) params.get("caseIds");
            String executorId = params.containsKey("executorId") ? params.get("executorId").toString() : null;

            int successCount = testPlanCaseSnapshotService.batchBindCases(planId, caseIds, executorId);
            return Resp.ok(successCount);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId和caseIds");
        }
    }

    /**
     * 根据测试计划和执行人查询已分配的测试用例（精简信息）
     */
    @PostMapping("listByPlanIdAndExecutor")
    public Resp listByPlanIdAndExecutor(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("planId")) {
            Long planId = Long.parseLong(params.get("planId").toString());
            String executorId = params.containsKey("executorId") && params.get("executorId") != null ? params.get("executorId").toString() : null;
            
            // 如果executorId为空，自动获取当前登录用户的ID
            if (executorId == null || executorId.isEmpty()) {
                executorId = UserUtils.getCurrentLoginUserId(userService);
                if (executorId == null) {
                    return Resp.info(400, "用户未登录");
                }
            }

            List<TestPlanCaseSnapshot> snapshots = testPlanCaseSnapshotService.listByExecutorId(planId, executorId);
            
            // 构建精简的用例信息列表
            List<Map<String, Object>> simpleCases = new java.util.ArrayList<>();
            for (TestPlanCaseSnapshot snapshot : snapshots) {
                Map<String, Object> simpleCase = new java.util.HashMap<>();
                simpleCase.put("id", snapshot.getId());
                simpleCase.put("originalCaseId", snapshot.getOriginalCaseId());
                simpleCase.put("caseNumber", snapshot.getCaseNumber());
                simpleCase.put("title", snapshot.getTitle());
                simpleCase.put("description", snapshot.getDescription());
                simpleCase.put("priority", snapshot.getPriority());
                simpleCase.put("executeStatus", snapshot.getExecuteStatus());
                simpleCases.add(simpleCase);
            }
            
            return Resp.ok(simpleCases);
        } else {
            return Resp.info(400, "请求参数不能为空，且必须包含planId");
        }
    }

    /**
     * 获取测试计划用例快照的步骤信息
     */
    @GetMapping("steps/{snapshotId}")
    public Resp getSteps(@PathVariable Long snapshotId) {
        if (snapshotId == null) {
            return Resp.info(400, "快照ID不能为空");
        }
        
        List<com.grape.grape.entity.TestPlanCaseSnapshotStep> steps = testPlanCaseSnapshotStepService.getBySnapshotId(snapshotId);
        return Resp.ok(steps);
    }

    /**
     * 执行测试用例
     */
    @PostMapping("execute")
    public Resp execute(@RequestBody Map<String, Object> params) {
        try {
            // 验证参数
            if (params == null || !params.containsKey("snapshotId") || !params.containsKey("executeStatus")) {
                return Resp.info(400, "请求参数不能为空，且必须包含snapshotId和executeStatus");
            }

            Long snapshotId = Long.parseLong(params.get("snapshotId").toString());
            Integer executeStatus = Integer.parseInt(params.get("executeStatus").toString());
            // 获取当前登录用户的ID作为 executorId
            String executorId = UserUtils.getCurrentLoginUserId(userService);
            if (executorId == null) {
                return Resp.info(400, "用户未登录");
            }
            Long executeTime = params.containsKey("executeTime") ? Long.parseLong(params.get("executeTime").toString()) : System.currentTimeMillis();
            Integer executeDuration = params.containsKey("executeDuration") ? Integer.parseInt(params.get("executeDuration").toString()) : 0;
            String actualResult = "";
            if (params.containsKey("actualResult")) {
                actualResult = params.get("actualResult").toString();
            } else {
                // 如果顶层没有 actualResult，从 steps 数组中获取
                List<Map<String, Object>> tempSteps = params.containsKey("steps") ? (List<Map<String, Object>>) params.get("steps") : null;
                if (tempSteps != null && !tempSteps.isEmpty()) {
                    Map<String, Object> firstStep = tempSteps.get(0);
                    if (firstStep.containsKey("actualResult")) {
                        actualResult = firstStep.get("actualResult").toString();
                    }
                }
            }
            Integer environmentId = params.containsKey("environmentId") ? Integer.parseInt(params.get("environmentId").toString()) : 1;
            List<Map<String, Object>> steps = params.containsKey("steps") ? (List<Map<String, Object>>) params.get("steps") : null;
            List<Map<String, Object>> attachments = params.containsKey("attachments") ? (List<Map<String, Object>>) params.get("attachments") : null;

            // 1. 更新测试用例快照的执行状态
            TestPlanCaseSnapshot snapshot = testPlanCaseSnapshotService.getById(snapshotId);
            if (snapshot == null) {
                return Resp.info(404, "测试用例快照不存在");
            }

            // 2. 获取快照步骤信息，用于填充预期结果
            List<TestPlanCaseSnapshotStep> snapshotSteps = testPlanCaseSnapshotStepService.getBySnapshotId(snapshotId);

            // 2. 保存执行记录
            TestPlanExecuteRecord executeRecord = new TestPlanExecuteRecord();
            executeRecord.setPlanId(snapshot.getPlanId());
            executeRecord.setSnapshotId(snapshotId);
            executeRecord.setOriginalCaseId(snapshot.getOriginalCaseId());
            executeRecord.setCaseNumber(snapshot.getCaseNumber());
            executeRecord.setExecuteNo("EXEC-" + snapshot.getPlanId() + "-" + snapshotId + "-" + System.currentTimeMillis());
            executeRecord.setExecuteRound(1); // 默认第一轮执行
            executeRecord.setExecutorId(executorId);
            executeRecord.setExecuteTime(executeTime);
            executeRecord.setExecuteDuration(executeDuration);
            executeRecord.setExecuteStatus(executeStatus);
            executeRecord.setActualResult(actualResult);
            executeRecord.setEnvironmentId(environmentId);
            
            // 设置附件信息
            if (attachments != null && !attachments.isEmpty()) {
                executeRecord.setAttachmentCount(attachments.size());
                executeRecord.setAttachments(com.alibaba.fastjson.JSON.toJSONString(attachments));
            } else {
                executeRecord.setAttachmentCount(0);
                executeRecord.setAttachments("[]");
            }
            
            testPlanExecuteRecordService.save(executeRecord);

            // 3. 保存执行步骤记录
            if (steps != null && !steps.isEmpty()) {
                for (int i = 0; i < steps.size(); i++) {
                    Map<String, Object> stepMap = steps.get(i);
                    TestPlanExecuteStep step = new TestPlanExecuteStep();
                    step.setExecuteId(executeRecord.getId());
                    step.setStepNo(i + 1);
                    step.setStepDescription(stepMap.containsKey("stepDescription") ? stepMap.get("stepDescription").toString() : "");
                    // 如果步骤中没有提供 expectedResult，从快照步骤中获取
                    String expectedResult = stepMap.containsKey("expectedResult") ? stepMap.get("expectedResult").toString() : "";
                    Long snapshotStepId = null;
                    if (snapshotSteps != null && !snapshotSteps.isEmpty()) {
                        for (TestPlanCaseSnapshotStep snapshotStep : snapshotSteps) {
                            if (snapshotStep.getStepNumber() == (i + 1)) {
                                if (expectedResult.isEmpty()) {
                                    expectedResult = snapshotStep.getExpectedResult();
                                }
                                snapshotStepId = snapshotStep.getId();
                                break;
                            }
                        }
                    }
                    step.setExpectedResult(expectedResult);
                    step.setSnapshotStepId(snapshotStepId);
                    step.setActualResult(stepMap.containsKey("actualResult") ? stepMap.get("actualResult").toString() : "");
                    step.setExecuteStatus(stepMap.containsKey("executeStatus") ? Integer.parseInt(stepMap.get("executeStatus").toString()) : 0);
                    step.setRemark(stepMap.containsKey("remark") ? stepMap.get("remark").toString() : "");
                    step.setCreatedAt(new java.util.Date());
                    step.setUpdatedAt(new java.util.Date());
                    testPlanExecuteStepService.save(step);
                    
                    // 4. 保存步骤附件
                    if (stepMap.containsKey("attachments")) {
                        List<String> stepAttachments = (List<String>) stepMap.get("attachments");
                        if (stepAttachments != null && !stepAttachments.isEmpty()) {
                            List<TestPlanExecuteStepAttachment> stepAttachmentList = new java.util.ArrayList<>();
                            for (String fileName : stepAttachments) {
                                TestPlanExecuteStepAttachment attachment = new TestPlanExecuteStepAttachment();
                                attachment.setFileName(fileName);
                                attachment.setFileUrl("");
                                attachment.setFileSize(0L);
                                attachment.setFileType(fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "");
                                attachment.setCreatedAt(System.currentTimeMillis());
                                attachment.setUpdatedAt(System.currentTimeMillis());
                                stepAttachmentList.add(attachment);
                            }
                            testPlanExecuteStepAttachmentService.saveAttachments(step.getId(), executeRecord.getId(), stepAttachmentList);
                        }
                    }
                }
            }

            // 4. 更新测试计划的统计信息
            Map<String, Object> stats = testPlanExecuteRecordService.calculateExecutionStats(snapshot.getPlanId());
            
            // 5. 更新测试用例快照的执行状态
            testPlanCaseSnapshotService.updateExecuteStatus(snapshotId, executeStatus, executorId);

            return Resp.ok("执行成功");
        } catch (Exception e) {
            log.error("执行测试用例失败", e);
            return Resp.error();
        }
    }
}
