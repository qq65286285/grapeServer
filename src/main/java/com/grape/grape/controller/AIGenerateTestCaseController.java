package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.AIGenerateTestCaseRequest;
import com.grape.grape.service.TestCaseGenerateTaskService;
import com.grape.grape.service.async.TestCaseGenerateAsyncService;
import com.grape.grape.component.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * AI生成测试用例控制器
 */
@RestController
@RequestMapping("/ai/testcase")
public class AIGenerateTestCaseController {

    private static final Logger log = LoggerFactory.getLogger(AIGenerateTestCaseController.class);

    @Resource
    private TestCaseGenerateTaskService testCaseGenerateTaskService;

    @Resource
    private TestCaseGenerateAsyncService testCaseGenerateAsyncService;

    @PostMapping("/generate")
    public Resp generateTestCases(@RequestBody AIGenerateTestCaseRequest request) {
        log.info("生成测试用例请求: testObject={}, testSubScenario={}, testType={}, flowType={}",
                request.getTestObject(), request.getTestSubScenario(), request.getTestType(), request.getFlowType());

        try {
            String creatorId = UserUtils.getCurrentUsername();
            String creatorName = UserUtils.getCurrentUsername();

            var task = testCaseGenerateTaskService.createTask(request, creatorId, creatorName);
            log.info("创建测试用例生成任务成功: {}", task.getId());

            testCaseGenerateAsyncService.executeGenerateTestCase(
                    task.getId(),
                    request.getTestObject(),
                    request.getTestSubScenario(),
                    request.getTestType(),
                    request.getFlowType()
            );

            return Resp.ok( "任务已提交，正在异步执行中");
        } catch (Exception e) {
            log.error("创建测试用例生成任务失败", e);
            return Resp.error();
        }
    }
}