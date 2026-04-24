package com.grape.grape.service.ai.algorithm;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import com.grape.grape.model.prompt.ai.algorithm.SumSimilarityResult;
import com.grape.grape.utils.ai.ReflectionUtils;
import com.grape.grape.utils.ai.VectorUtils;
import com.grape.grape.utils.ai.calamethods.CosineSimilarity;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总和相似度算法
 * <p>
 * 该算法用于计算测试用例的多个字段（通常是列表）与分析结果中多个候选项的相似度矩阵，
 * 对每个测试用例字段找到最佳匹配，然后求和归一化。
 * <p>
 * 核心功能：
 * 1. 从测试用例获取测试点列表
 * 2. 从分析结果获取候选测试点列表
 * 3. 对每个测试点找到最佳匹配的分析项
 * 4. 累加所有最佳匹配的相似度
 * 5. 归一化：除以测试用例测试点的数量
 * 6. 检查阈值
 * 7. 构造返回结果
 */
@Service
public class SumSimilarityAlgorithm {

    /**
     * 计算总和相似度结果
     * <p>
     * 计算测试用例的多个字段与分析结果中多个候选项的相似度矩阵，
     * 对每个测试用例字段找到最佳匹配，然后求和归一化。
     * 
     * @param testCase 测试用例对象
     * @param analysis 测试场景分析结果对象
     * @param config 字段配置，包含测试用例字段路径和分析结果字段路径
     * @param vectorCache 向量缓存
     * @return 总和相似度结果，包含归一化后的总分和详细信息
     */
    public SumSimilarityResult calculateSumSimilarity(
        TestCase testCase,
        TestScenarioAnalysis analysis,
        FilterFieldConfig config,
        Map<String, List<Double>> vectorCache) {
        // 1. 获取测试用例的测试点列表
        List<String> testCasePoints = ReflectionUtils.getFieldValueGeneric(testCase, config.getTestCaseField());
        if (testCasePoints == null || testCasePoints.isEmpty()) {
            return SumSimilarityResult.createEmptyResult();
        }
        
        // 2. 获取分析结果的测试点列表
        List<String> analysisPoints = ReflectionUtils.getFieldValueGeneric(analysis, config.getAnalysisField());
        if (analysisPoints == null || analysisPoints.isEmpty()) {
            return SumSimilarityResult.createEmptyResult();
        }
        
        // 3. 预先计算所有 analysisPoints 的向量（优化）
        List<List<Double>> analysisVectors = new ArrayList<>();
        for (String analysisPoint : analysisPoints) {
            analysisVectors.add(VectorUtils.getVectorFromCache(analysisPoint, vectorCache));
        }
        
        // 4. 对每个测试点找最佳匹配
        double sumScore = 0.0;
        List<Map<String, Object>> matches = new ArrayList<>();
        
        for (String testCasePoint : testCasePoints) {
            // 获取测试点的向量
            List<Double> testCaseVector = VectorUtils.getVectorFromCache(testCasePoint, vectorCache);
            
            // 找到最佳匹配的 analysisPoint
            double maxSimilarity = 0.0;
            String bestMatch = null;
            
            for (int i = 0; i < analysisPoints.size(); i++) {
                String analysisPoint = analysisPoints.get(i);
                List<Double> analysisVector = analysisVectors.get(i);
                
                double similarity = CosineSimilarity.calculate(testCaseVector, analysisVector);
                
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestMatch = analysisPoint;
                }
            }
            
            // 累加得分
            sumScore += maxSimilarity;
            
            // 记录匹配详情
            Map<String, Object> match = new HashMap<>();
            match.put("testCaseText", testCasePoint);
            match.put("analysisText", bestMatch);
            match.put("similarity", maxSimilarity);
            match.put("contribution", maxSimilarity / testCasePoints.size());  // 该点对总分的贡献
            matches.add(match);
        }
        
        // 5. 归一化
        double normalizedScore = sumScore / testCasePoints.size();
        
        // 也可以记录其他归一化方式供参考
        double maxPossibleScore = testCasePoints.size() * 1.0;  // 理论最大值
        
        // 6. 检查阈值（如果配置了）
        // 阈值用于判断相似度匹配是否通过的临界值
        // 当计算的相似度分数低于此值时，认为匹配失败
        if (config.getThreshold() != null && normalizedScore < config.getThreshold()) {
            // 硬过滤失败的情况，detail 中需要记录阈值检查
            Map<String, Object> detail = new HashMap<>();
            detail.put("strategy", "SUM");
            detail.put("testCaseTexts", testCasePoints);
            detail.put("sumScore", sumScore);
            detail.put("normalizedSumScore", normalizedScore);
            detail.put("totalCompared", analysisPoints.size());
            detail.put("matches", matches);
            detail.put("normalizationMethod", "DIVIDE_BY_TESTCASE_COUNT");
            detail.put("maxPossibleScore", maxPossibleScore);
            
            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", normalizedScore);
            thresholdCheck.put("passed", false);
            detail.put("thresholdCheck", thresholdCheck);
            
            return new SumSimilarityResult(normalizedScore, detail);
        }
        
        // 7. 构造返回结果
        Map<String, Object> detail = new HashMap<>();
        detail.put("strategy", "SUM");
        detail.put("testCaseTexts", testCasePoints);
        detail.put("sumScore", sumScore);
        detail.put("normalizedSumScore", normalizedScore);
        detail.put("totalCompared", analysisPoints.size());
        detail.put("matches", matches);
        detail.put("normalizationMethod", "DIVIDE_BY_TESTCASE_COUNT");
        detail.put("maxPossibleScore", maxPossibleScore);
        
        if (config.getThreshold() != null) {
            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", normalizedScore);
            thresholdCheck.put("passed", true);
            detail.put("thresholdCheck", thresholdCheck);
        }
        
        return new SumSimilarityResult(normalizedScore, detail);
    }

}
