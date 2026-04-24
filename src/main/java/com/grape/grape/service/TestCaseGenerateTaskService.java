package com.grape.grape.service;

import com.grape.grape.entity.TestCaseGenerateTask;
import com.grape.grape.mapper.TestCaseGenerateTaskMapper;
import com.grape.grape.model.vo.AIGenerateTestCaseRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 测试用例生成任务服务
 */
@Service
public class TestCaseGenerateTaskService {

    @Resource
    private TestCaseGenerateTaskMapper testCaseGenerateTaskMapper;

    public TestCaseGenerateTask createTask(AIGenerateTestCaseRequest request, String creatorId, String creatorName) {
        TestCaseGenerateTask task = request.toTask(creatorId, creatorName);
        testCaseGenerateTaskMapper.insert(task);
        return task;
    }

    public TestCaseGenerateTask getTaskById(Long id) {
        return testCaseGenerateTaskMapper.selectOneById(id);
    }
}