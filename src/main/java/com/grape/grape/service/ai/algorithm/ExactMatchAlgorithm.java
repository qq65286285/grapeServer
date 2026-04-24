package com.grape.grape.service.ai.algorithm;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import com.grape.grape.model.prompt.ai.algorithm.ExactMatchResult;
import com.grape.grape.utils.ai.CompareUtils;
import com.grape.grape.utils.ai.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 精确匹配算法
 * <p>
 * 该算法用于比较测试用例（TestCase）和测试场景分析（TestScenarioAnalysis）中的字段值，
 * 支持多种数据类型的精确匹配，包括字符串、数字、枚举和列表等。
 * <p>
 * 核心功能：
 * 1. 通过反射机制获取对象的字段值，支持嵌套路径访问
 * 2. 处理空值情况
 * 3. 根据不同数据类型执行相应的比较逻辑
 * 4. 返回匹配结果和详细信息
 */
public class ExactMatchAlgorithm {

    /**
     * 计算精确匹配结果
     * <p>
     * 比较测试用例和分析结果中指定字段的值，返回匹配分数和详细信息
     * 
     * @param testCase 测试用例对象
     * @param analysis 测试场景分析结果对象
     * @param config 字段配置，包含测试用例字段路径和分析结果字段路径
     * @return 精确匹配结果，包含匹配分数和详细信息
     */
    public ExactMatchResult calculateExactMatch(TestCase testCase, TestScenarioAnalysis analysis, FilterFieldConfig config) {
        // 1. 获取测试用例的实际值
        Object actualValue = ReflectionUtils.getFieldValue(testCase, config.getTestCaseField());
        
        // 2. 获取分析结果的期望值
        Object expectedValue = ReflectionUtils.getFieldValue(analysis, config.getAnalysisField());
        
        // 3. 处理空值情况：如果任一值为null，直接返回不匹配结果
        if (actualValue == null || expectedValue == null) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("expected", expectedValue);
            detail.put("actual", actualValue);
            detail.put("isMatch", false);
            detail.put("strategy", "EXACT");
            return new ExactMatchResult(0.0, detail);
        }
        
        // 4. 执行值比较
        boolean isMatch = CompareUtils.compareValues(actualValue, expectedValue);
        
        // 5. 构造返回结果：匹配分数为1.0表示完全匹配，0.0表示不匹配
        Map<String, Object> detail = new HashMap<>();
        detail.put("expected", expectedValue);
        detail.put("actual", actualValue);
        detail.put("isMatch", isMatch);
        detail.put("strategy", "EXACT");
        
        double score = isMatch ? 1.0 : 0.0;
        
        return new ExactMatchResult(score, detail);
    }






}
