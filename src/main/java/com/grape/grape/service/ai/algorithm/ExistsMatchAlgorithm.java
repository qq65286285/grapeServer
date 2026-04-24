package com.grape.grape.service.ai.algorithm;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.TagMatchMode;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import com.grape.grape.model.prompt.ai.algorithm.ExistsMatchResult;
import com.grape.grape.utils.ai.ReflectionUtils;
import com.grape.grape.utils.ai.TagUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 存在匹配算法
 * <p>
 * 该算法用于检查测试用例中的标签/关键字列表是否存在于分析结果的标签列表中，
 * 计算匹配比例作为得分。
 * <p>
 * 核心功能：
 * 1. 从测试用例获取期望的标签列表
 * 2. 从分析结果获取实际的标签列表
 * 3. 支持两种匹配模式：精确匹配（EXACT）和包含匹配（CONTAINS）
 * 4. 忽略大小写和前后空格
 * 5. 计算匹配比例
 * 6. 检查阈值
 * 7. 构造返回结果
 */
@Service
public class ExistsMatchAlgorithm {

    /**
     * 计算存在匹配结果
     * <p>
     * 检查测试用例中的标签是否存在于分析结果的标签列表中，计算匹配比例作为得分
     * 
     * @param testCase 测试用例对象
     * @param analysis 测试场景分析结果对象
     * @param config 字段配置，包含测试用例字段路径和分析结果字段路径
     * @return 存在匹配结果，包含匹配比例和详细信息
     */
    public ExistsMatchResult calculateExistsMatch(
        TestCase testCase,
        TestScenarioAnalysis analysis,
        FilterFieldConfig config) {
        // 1. 获取期望的标签列表
        List<String> expectedTags = ReflectionUtils.getFieldValueGeneric(testCase, config.getTestCaseField());
        
        // 边界情况：没有期望标签 → 认为通过
        if (expectedTags == null || expectedTags.isEmpty()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("strategy", "EXISTS");
            detail.put("note", "没有期望标签，默认通过");
            return new ExistsMatchResult(1.0, detail);
        }
        
        // 2. 获取实际的标签列表
        List<String> actualTags = ReflectionUtils.getFieldValueGeneric(analysis, config.getAnalysisField());
        
        // 边界情况：分析结果没有标签
        if (actualTags == null || actualTags.isEmpty()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("strategy", "EXISTS");
            detail.put("expectedTags", expectedTags);
            detail.put("actualTags", List.of());
            detail.put("matchedCount", 0);
            detail.put("totalExpected", expectedTags.size());
            detail.put("matchRatio", 0.0);
            detail.put("note", "分析结果没有标签");
            return new ExistsMatchResult(0.0, detail);
        }
        
        // 3. 归一化标签（转小写，去空格）
        Set<String> normalizedActualTags = new HashSet<>();
        for (String tag : actualTags) {
            if (tag != null && !tag.trim().isEmpty()) {
                normalizedActualTags.add(tag.trim().toLowerCase());
            }
        }
        
        // 4. 检查每个期望标签是否匹配
        int matchedCount = 0;
        List<Map<String, Object>> matchDetails = new ArrayList<>();
        
        // 获取匹配模式（从配置中，默认 EXACT）
        TagMatchMode matchMode = TagUtils.getTagMatchMode(config);
        
        for (String expectedTag : expectedTags) {
            if (expectedTag == null || expectedTag.trim().isEmpty()) {
                continue;
            }
            
            String normalizedExpected = expectedTag.trim().toLowerCase();
            boolean isMatched = false;
            String matchedWith = null;
            
            // 根据匹配模式执行检查
            if (matchMode == TagMatchMode.EXACT) {
                // 精确匹配
                if (normalizedActualTags.contains(normalizedExpected)) {
                    isMatched = true;
                    matchedWith = normalizedExpected;
                }
            } else {
                // 包含匹配：expectedTag 存在于某个 actualTag 中
                for (String actualTag : normalizedActualTags) {
                    if (actualTag.contains(normalizedExpected) || normalizedExpected.contains(actualTag)) {
                        isMatched = true;
                        matchedWith = actualTag;
                        break;
                    }
                }
            }
            
            if (isMatched) {
                matchedCount++;
            }
            
            // 记录详情
            Map<String, Object> matchDetail = new HashMap<>();
            matchDetail.put("expectedTag", expectedTag);
            matchDetail.put("isMatched", isMatched);
            matchDetail.put("matchedWith", matchedWith);
            matchDetails.add(matchDetail);
        }
        
        // 5. 计算匹配率
        int totalExpected = expectedTags.size();
        double matchRatio = (double) matchedCount / totalExpected;
        
        // 6. 检查阈值
        if (config.getThreshold() != null && matchRatio < config.getThreshold()) {
            // 硬过滤失败
            Map<String, Object> detail = new HashMap<>();
            detail.put("strategy", "EXISTS");
            detail.put("matchMode", matchMode.name());
            detail.put("expectedTags", expectedTags);
            detail.put("actualTags", actualTags);
            detail.put("matchedCount", matchedCount);
            detail.put("totalExpected", totalExpected);
            detail.put("matchRatio", matchRatio);
            detail.put("matchDetails", matchDetails);
            
            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", matchRatio);
            thresholdCheck.put("passed", false);
            detail.put("thresholdCheck", thresholdCheck);
            
            return new ExistsMatchResult(matchRatio, detail);
        }
        
        // 7. 构造返回结果
        Map<String, Object> detail = new HashMap<>();
        detail.put("strategy", "EXISTS");
        detail.put("matchMode", matchMode.name());
        detail.put("expectedTags", expectedTags);
        detail.put("actualTags", actualTags);
        detail.put("matchedCount", matchedCount);
        detail.put("totalExpected", totalExpected);
        detail.put("matchRatio", matchRatio);
        detail.put("matchDetails", matchDetails);
        
        if (config.getThreshold() != null) {
            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", matchRatio);
            thresholdCheck.put("passed", true);
            detail.put("thresholdCheck", thresholdCheck);
        }
        
        return new ExistsMatchResult(matchRatio, detail);
    }

}
