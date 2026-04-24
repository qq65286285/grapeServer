package com.grape.grape.model.prompt.ai;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import io.milvus.response.QueryResultsWrapper;

public class TestCase {
    private Long id; // 主键字段
    private String caseId; // 用例编号（业务主键）
    private String caseName; // 用例名称
    private String fullText; // 完整原文（用于返回）
    private String precondition; // 前置条件
    private String testSteps; // 测试步骤
    private String expectedResult; // 期望结果
    private String priority; // 用例分级 (P0/P1/P2)
    private String iteration; // 迭代版本
    private String isSmokeTest; // 是否冒烟测试
    private String automationFlag; // 自动化标记
    private String screenshot; // 截图
    private String remark; // 备注
    private float[] embedding; // 向量字段
    private long createdAt; // 创建时间（Unix 时间戳）

    // 构造方法
    public TestCase() {
        this.id = 0L;
        this.caseId = "";
        this.caseName = "";
        this.fullText = "";
        this.precondition = "";
        this.testSteps = "";
        this.expectedResult = "";
        this.priority = "";
        this.iteration = "";
        this.isSmokeTest = "";
        this.automationFlag = "";
        this.screenshot = "";
        this.remark = "";
        this.embedding = new float[0];
        this.createdAt = 0;
    }

    // 从Excel行数据创建TestCase对象
    public static TestCase fromExcelRow(List<String> row) {
        TestCase testCase = new TestCase();
        if (row.size() >= 10) {
            testCase.setCaseName(row.get(0));
            testCase.setPrecondition(row.get(1));
            testCase.setTestSteps(row.get(2));
            testCase.setExpectedResult(row.get(3));
            testCase.setPriority(row.get(4));
            testCase.setIteration(row.get(5));
            testCase.setIsSmokeTest(row.get(6));
            testCase.setAutomationFlag(row.get(7));
            testCase.setScreenshot(row.get(8));
            testCase.setRemark(row.get(9));
        }
        // 生成完整原文
        testCase.generateFullText();
        // 设置创建时间
        testCase.setCreatedAt(System.currentTimeMillis() / 1000);
        return testCase;
    }

    // 生成完整原文
    public void generateFullText() {
        StringBuilder sb = new StringBuilder();
        sb.append("用例名称：").append(caseName).append("，");
        sb.append("前置条件：").append(precondition).append("，");
        sb.append("测试步骤：").append(testSteps).append("，");
        sb.append("期望结果：").append(expectedResult).append("，");
        sb.append("用例分级：").append(priority).append("，");
        sb.append("迭代：").append(iteration).append("，");
        sb.append("是否冒烟测试：").append(isSmokeTest).append("，");
        sb.append("自动化标记：").append(automationFlag).append("，");
        sb.append("截图：").append(screenshot).append("，");
        sb.append("备注：").append(remark);
        this.fullText = sb.toString();
    }

    // 获取用于向量化的文本
    public String getTextForEmbedding() {
        return fullText;
    }

    // 转换为向量对象（设置向量值）
    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    // 生成用例编号（如果没有）
    public void generateCaseId() {
        if (caseId == null || caseId.isEmpty()) {
            this.caseId = "CASE_" + RandomUtil.randomBigDecimal();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getPrecondition() {
        return precondition;
    }

    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    public String getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(String testSteps) {
        this.testSteps = testSteps;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getIsSmokeTest() {
        return isSmokeTest;
    }

    public void setIsSmokeTest(String isSmokeTest) {
        this.isSmokeTest = isSmokeTest;
    }

    public String getAutomationFlag() {
        return automationFlag;
    }

    public void setAutomationFlag(String automationFlag) {
        this.automationFlag = automationFlag;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "id=" + id +
                ", caseId='" + caseId + '\'' +
                ", caseName='" + caseName + '\'' +
                ", priority='" + priority + '\'' +
                ", iteration='" + iteration + '\'' +
                '}';
    }

    public static List<String> getFieldNames() {
        return Arrays.asList(
            "id", "case_id", "case_name", "full_text", "precondition",
            "test_steps", "expected_result", "priority", "iteration",
            "is_smoke_test", "automation_flag", "screenshot", "remark",
            "embedding", "created_at"
        );
    }

    public TestCase getByRecord(QueryResultsWrapper.RowRecord record) {
        this.setId(Convert.toLong(record.get("id"), -1L));
        this.setCaseId(Convert.toStr(record.get("case_id"), ""));
        this.setCaseName(Convert.toStr(record.get("case_name"), ""));
        this.setFullText(Convert.toStr(record.get("full_text"), ""));
        this.setPrecondition(Convert.toStr(record.get("precondition"), ""));
        this.setTestSteps(Convert.toStr(record.get("test_steps"), ""));
        this.setExpectedResult(Convert.toStr(record.get("expected_result"), ""));
        this.setPriority(Convert.toStr(record.get("priority"), ""));
        this.setIteration(Convert.toStr(record.get("iteration"), ""));
        this.setIsSmokeTest(Convert.toStr(record.get("is_smoke_test"), ""));
        this.setAutomationFlag(Convert.toStr(record.get("automation_flag"), ""));
        this.setScreenshot(Convert.toStr(record.get("screenshot"), ""));
        // 处理embedding字段
        Object embeddingObj = record.get("embedding");
        if (embeddingObj instanceof List) {
            List<?> embeddingList = (List<?>) embeddingObj;
            float[] embeddingArray = new float[embeddingList.size()];
            for (int i = 0; i < embeddingList.size(); i++) {
                Object item = embeddingList.get(i);
                if (item instanceof Number) {
                    embeddingArray[i] = ((Number) item).floatValue();
                }
            }
            this.setEmbedding(embeddingArray);
        } else if (embeddingObj instanceof Float[]) {
            Float[] floatArray = (Float[]) embeddingObj;
            float[] embeddingArray = new float[floatArray.length];
            for (int i = 0; i < floatArray.length; i++) {
                if (floatArray[i] != null) {
                    embeddingArray[i] = floatArray[i];
                }
            }
            this.setEmbedding(embeddingArray);
        } else {
            this.setEmbedding(new float[0]);
        }
        this.setRemark(Convert.toStr(record.get("remark"), ""));
        this.setCreatedAt(Convert.toLong(record.get("created_at"), -1L));
        return this;
    }

}