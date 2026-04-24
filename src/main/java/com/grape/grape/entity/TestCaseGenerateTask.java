package com.grape.grape.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;

/**
 * 测试用例生成任务实体类
 */
@Table("test_case_generate_task")
public class TestCaseGenerateTask {

    /**
     * 任务ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 测试对象（比如安卓，PC）
     */
    private String testObject;

    /**
     * 测试子场景（Joymaker的邮箱和手机号，谷歌登录，apple登录，游客登录）
     */
    private String testSubScenario;

    /**
     * 测试类型（功能，异常场景）
     */
    private String testType;

    /**
     * 流程类型（异常场景，正常业务）
     */
    private String flowType;

    /**
     * 任务状态：0-待处理，1-执行中，2-成功，3-失败
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private String creatorId;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 开始执行时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 错误信息（如有）
     */
    private String errorMessage;

    /**
     * 生成的测试用例内容
     */
    private String testCaseContent;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 调用日志
     */
    private String callLog;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTestCaseContent() {
        return testCaseContent;
    }

    public void setTestCaseContent(String testCaseContent) {
        this.testCaseContent = testCaseContent;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getCallLog() {
        return callLog;
    }

    public void setCallLog(String callLog) {
        this.callLog = callLog;
    }
}