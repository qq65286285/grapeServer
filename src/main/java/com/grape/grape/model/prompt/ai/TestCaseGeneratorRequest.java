package com.grape.grape.model.prompt.ai;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestCaseGeneratorRequest {
    private String roleDefinition; // 角色定义
    private List<String> contextInfo; // 上下文信息
    private String userScenario; // 用户场景描述
    private String testScenario; // 测试场景
    private List<TestCase> similarTestCases; // Milvus 向量数据库相似用例
    private String similarTestCasesString; // 用例列表组成的字符串
    private String sirchmunkDocument; // Sirchmunk 文档所用的字符串
    private String outputRequirements; // 输出要求
    private Map<String, Long> stepTimings; // 步骤耗时记录

    // 构造方法
    public TestCaseGeneratorRequest() {
        this.roleDefinition = """
        你是一名专业的测试工程师，
        精通软件测试理论和实践，
        擅长从测试需求中提取关键信息并进行结构化分析。\r\n
                                """;
        this.contextInfo = List.of();
        this.userScenario = "";
        this.testScenario = "";
        this.similarTestCases = List.of();
        this.similarTestCasesString = "";
        this.sirchmunkDocument = "";
        this.outputRequirements = "";
        this.stepTimings = Map.of();
    }

    /**
     * 记录步骤开始时间
     * @param stepName 步骤名称
     */
    public void startStep(String stepName) {
        if (stepTimings == null) {
            stepTimings = new java.util.HashMap<>();
        }
        stepTimings.put(stepName + "_start", System.currentTimeMillis());
    }

    /**
     * 记录步骤结束时间并计算耗时
     * @param stepName 步骤名称
     * @return 该步骤的耗时（毫秒）
     */
    public long endStep(String stepName) {
        if (stepTimings == null) {
            stepTimings = new java.util.HashMap<>();
            return 0;
        }
        long endTime = System.currentTimeMillis();
        Long startTime = stepTimings.get(stepName + "_start");
        long duration = (startTime != null) ? (endTime - startTime) : 0;
        stepTimings.put(stepName + "_end", endTime);
        stepTimings.put(stepName + "_duration", duration);
        return duration;
    }

    /**
     * 获取步骤耗时
     * @param stepName 步骤名称
     * @return 耗时（毫秒），如果不存在返回0
     */
    public long getStepDuration(String stepName) {
        if (stepTimings == null) {
            return 0;
        }
        Long duration = stepTimings.get(stepName + "_duration");
        return (duration != null) ? duration : 0;
    }

    @Override
    public String toString() {
        return "TestCaseGeneratorRequest{" +
                "roleDefinition='" + roleDefinition + '\'' +
                ", contextInfo.size()=" + (contextInfo != null ? contextInfo.size() : 0) +
                ", userScenario='" + userScenario + '\'' +
                ", testScenario='" + testScenario + '\'' +
                ", similarTestCases.size()=" + (similarTestCases != null ? similarTestCases.size() : 0) +
                ", similarTestCasesString='" + similarTestCasesString + '\'' +
                ", sirchmunkDocument='" + sirchmunkDocument + '\'' +
                ", outputRequirements='" + outputRequirements + '\'' +
                ", stepTimings.size()=" + (stepTimings != null ? stepTimings.size() : 0) +
                '}';
    }
}