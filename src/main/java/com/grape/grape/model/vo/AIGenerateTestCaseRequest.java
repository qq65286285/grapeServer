package com.grape.grape.model.vo;

import com.grape.grape.entity.TestCaseGenerateTask;
import com.google.gson.Gson;

/**
 * AI生成测试用例请求参数
 */
public class AIGenerateTestCaseRequest {
    private static final Gson gson = new Gson();

    private String testObject;      // 测试对象（比如安卓，PC）
    private String testSubScenario; // 测试子场景（Joymaker的邮箱和手机号，谷歌登录，apple登录，游客登录）
    private String testType;        // 测试类型（功能，异常场景）
    private String flowType;        // 流程类型（异常场景，正常业务）

    public String getTestObject() {
        return testObject;
    }

    public void setTestObject(String testObject) {
        this.testObject = testObject;
    }

    public String getTestSubScenario() {
        return testSubScenario;
    }

    public void setTestSubScenario(String testSubScenario) {
        this.testSubScenario = testSubScenario;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public TestCaseGenerateTask toTask(String creatorId, String creatorName) {
        TestCaseGenerateTask task = new TestCaseGenerateTask();
        task.setTestObject(this.testObject);
        task.setTestSubScenario(this.testSubScenario);
        task.setTestType(this.testType);
        task.setFlowType(this.flowType);
        task.setStatus(0); // 0-待处理
        task.setCreatorId(creatorId);
        task.setCreatorName(creatorName);
        task.setCreatedAt(java.time.LocalDateTime.now());
        task.setRequestParams(gson.toJson(this));
        return task;
    }
}