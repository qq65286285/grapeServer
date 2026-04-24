package com.grape.grape.service.async;

import com.grape.grape.entity.TestCaseGenerateTask;
import com.grape.grape.mapper.TestCaseGenerateTaskMapper;
import com.grape.grape.service.biz.AIGenerateTestCaseBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 测试用例生成异步服务
 */
@Service
public class TestCaseGenerateAsyncService {

    private static final Logger log = LoggerFactory.getLogger(TestCaseGenerateAsyncService.class);

    @Resource
    private TestCaseGenerateTaskMapper testCaseGenerateTaskMapper;

    @Resource
    private AIGenerateTestCaseBizService aiGenerateTestCaseBizService;

    /**
     * 异步执行测试用例生成
     * @param taskId 任务ID
     * @param testObject 测试对象
     * @param testSubScenario 测试子场景
     * @param testType 测试类型
     * @param flowType 流程类型
     */
    @Async
    @Transactional
    public void executeGenerateTestCase(Long taskId, String testObject, String testSubScenario, String testType, String flowType) {
        TestCaseGenerateTask task = testCaseGenerateTaskMapper.selectOneById(taskId);
        if (task == null) {
            log.error("任务不存在: {}", taskId);
            return;
        }

        try {
            // 更新任务状态为执行中
            task.setStatus(1); // 1-执行中
            task.setStartTime(LocalDateTime.now());
            testCaseGenerateTaskMapper.update(task);

            log.info("开始执行测试用例生成任务: {}, testObject={}, testSubScenario={}, testType={}, flowType={}",
                    taskId, testObject, testSubScenario, testType, flowType);

            // 执行测试用例生成
            String testCaseContent = aiGenerateTestCaseBizService.generateTestCases(
                    testObject, testSubScenario, testType, flowType
            );

            // 更新任务状态为成功
            task.setStatus(2); // 2-成功
            task.setCompleteTime(LocalDateTime.now());
            task.setTestCaseContent(testCaseContent);
            testCaseGenerateTaskMapper.update(task);

            log.info("测试用例生成任务执行成功: {}", taskId);
        } catch (Exception e) {
            // 更新任务状态为失败
            task.setStatus(3); // 3-失败
            task.setCompleteTime(LocalDateTime.now());
            task.setErrorMessage(e.getMessage());
            testCaseGenerateTaskMapper.update(task);

            log.error("测试用例生成任务执行失败: {}", taskId, e);
        }
    }
}