package com.grape.grape.model.prompt.ai.algorithm;

import java.util.Map;

/**
 * 存在匹配结果类
 * <p>
 * 存储存在匹配的结果信息，包括匹配比例和详细信息
 */
public class ExistsMatchResult {
    /**
     * 匹配比例（0-1）
     */
    private double score;
    
    /**
     * 详细信息，包含以下内容：
     * - strategy: 匹配策略（EXISTS）
     * - matchMode: 匹配模式（EXACT 或 CONTAINS）
     * - expectedTags: 期望的标签列表
     * - actualTags: 实际的标签列表
     * - matchedCount: 匹配的标签数量
     * - totalExpected: 期望的标签总数
     * - matchRatio: 匹配比例
     * - matchDetails: 每个标签的匹配详情
     * - thresholdCheck: 阈值检查（如果配置了阈值）
     */
    private Map<String, Object> detail;

    /**
     * 构造函数
     * 
     * @param score 匹配比例
     * @param detail 详细信息
     */
    public ExistsMatchResult(double score, Map<String, Object> detail) {
        this.score = score;
        this.detail = detail;
    }

    /**
     * 获取匹配比例
     * 
     * @return 匹配比例
     */
    public double getScore() {
        return score;
    }

    /**
     * 设置匹配比例
     * 
     * @param score 匹配比例
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