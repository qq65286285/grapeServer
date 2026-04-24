package com.grape.grape.model.prompt.ai.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 总和相似度结果类
 * <p>
 * 存储总和相似度的结果信息，包括归一化后的总分和详细信息
 */
public class SumSimilarityResult {
    /**
     * 归一化后的总分
     */
    private double score;
    
    /**
     * 详细信息，包含以下内容：
     * - strategy: 匹配策略（SUM）
     * - testCaseTexts: 测试用例文本列表
     * - sumScore: 总分
     * - normalizedSumScore: 归一化后的总分
     * - totalCompared: 总比较数
     * - matches: 匹配详情列表
     * - normalizationMethod: 归一化方法
     * - maxPossibleScore: 理论最大分数
     * - thresholdCheck: 阈值检查（如果配置了阈值）
     */
    private Map<String, Object> detail;

    /**
     * 构造函数
     * 
     * @param score 归一化后的总分
     * @param detail 详细信息
     */
    public SumSimilarityResult(double score, Map<String, Object> detail) {
        this.score = score;
        this.detail = detail;
    }

    /**
     * 创建默认的空结果
     * 
     * @return 默认的空结果，分数为0.0
     */
    public static SumSimilarityResult createEmptyResult() {
        Map<String, Object> detail = new HashMap<>();
        detail.put("strategy", "SUM");
        detail.put("testCaseTexts", new ArrayList<>());
        detail.put("sumScore", 0.0);
        detail.put("normalizedSumScore", 0.0);
        detail.put("totalCompared", 0);
        detail.put("matches", new ArrayList<>());
        detail.put("normalizationMethod", "DIVIDE_BY_TESTCASE_COUNT");
        detail.put("maxPossibleScore", 0.0);
        return new SumSimilarityResult(0.0, detail);
    }

    /**
     * 获取归一化后的总分
     * 
     * @return 归一化后的总分
     */
    public double getScore() {
        return score;
    }

    /**
     * 设置归一化后的总分
     * 
     * @param score 归一化后的总分
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * 获取详细信息
     * 
     * @return 详细信息
     */
    public Map<String, Object> getDetail() {
        return detail;
    }

    /**
     * 设置详细信息
     * 
     * @param detail 详细信息
     */
    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }
}