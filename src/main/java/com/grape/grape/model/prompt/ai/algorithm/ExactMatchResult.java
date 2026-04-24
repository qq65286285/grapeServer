package com.grape.grape.model.prompt.ai.algorithm;

import java.util.Map;

/**
 * 精确匹配结果类
 * <p>
 * 存储精确匹配的结果信息，包括匹配分数和详细信息
 */
public class ExactMatchResult {
    /**
     * 匹配分数：1.0表示完全匹配，0.0表示不匹配
     */
    private double score;
    
    /**
     * 详细信息，包含以下内容：
     * - expected: 期望值
     * - actual: 实际值
     * - isMatch: 是否匹配
     * - strategy: 匹配策略（EXACT）
     */
    private Map<String, Object> detail;

    /**
     * 构造函数
     * 
     * @param score 匹配分数
     * @param detail 详细信息
     */
    public ExactMatchResult(double score, Map<String, Object> detail) {
        this.score = score;
        this.detail = detail;
    }

    /**
     * 获取匹配分数
     * 
     * @return 匹配分数
     */
    public double getScore() {
        return score;
    }

    /**
     * 设置匹配分数
     * 
     * @param score 匹配分数
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