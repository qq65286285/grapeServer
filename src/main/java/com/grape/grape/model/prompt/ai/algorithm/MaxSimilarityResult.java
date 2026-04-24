package com.grape.grape.model.prompt.ai.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 最大相似度结果类
 * <p>
 * 存储最大相似度的结果信息，包括相似度分数和详细信息
 */
public class MaxSimilarityResult {
    /**
     * 相似度分数
     */
    private double score;
    
    /**
     * 详细信息，包含以下内容：
     * - strategy: 匹配策略（MAX）
     * - testCaseText: 测试用例文本
     * - bestMatch: 最佳匹配
     * - bestMatchIndex: 最佳匹配索引
     * - bestScore: 最佳分数
     * - totalCompared: 总比较数
     * - allMatches: 所有匹配项
     * - thresholdCheck: 阈值检查（如果配置了阈值）
     */
    private Map<String, Object> detail;

    /**
     * 构造函数
     * 
     * @param score 相似度分数
     * @param detail 详细信息
     */
    public MaxSimilarityResult(double score, Map<String, Object> detail) {
        this.score = score;
        this.detail = detail;
    }

    /**
     * 创建默认的空结果
     * 
     * @return 默认的空结果，分数为0.0
     */
    public static MaxSimilarityResult createEmptyResult() {
        Map<String, Object> detail = new HashMap<>();
        detail.put("strategy", "MAX");
        detail.put("testCaseText", null);
        detail.put("bestMatch", null);
        detail.put("bestMatchIndex", -1);
        detail.put("bestScore", 0.0);
        detail.put("totalCompared", 0);
        detail.put("allMatches", new ArrayList<>());
        return new MaxSimilarityResult(0.0, detail);
    }

    /**
     * 获取相似度分数
     * 
     * @return 相似度分数
     */
    public double getScore() {
        return score;
    }

    /**
     * 设置相似度分数
     * 
     * @param score 相似度分数
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