package com.grape.grape.service.ai.algorithm;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import com.grape.grape.model.prompt.ai.algorithm.MaxSimilarityResult;
import com.grape.grape.utils.ai.ReflectionUtils;
import com.grape.grape.utils.ai.VectorUtils;
import com.grape.grape.utils.ai.calamethods.CosineSimilarity;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 最大相似度算法
 * <p>
 * 该算法用于计算测试用例字段与分析结果中多个候选项的相似度，取最大值作为该字段得分。
 * <p>
 * 核心功能：
 * 1. 从测试用例获取待比较的文本
 * 2. 从分析结果获取候选列表
 * 3. 计算测试用例文本与每个候选文本的余弦相似度
 * 4. 取最大值作为得分
 * 5. 检查阈值
 * 6. 构造返回结果
 */
@Service
public class MaxSimilarityAlgorithm {


    /**
     * 计算最大相似度结果
     * <p>
     * 计算测试用例字段与分析结果中多个候选项的相似度，取最大值作为该字段得分
     * 
     * @param testCase 测试用例对象
     * @param analysis 测试场景分析结果对象
     * @param config 字段配置，包含测试用例字段路径和分析结果字段路径
     * @param vectorCache 向量缓存
     * @return 最大相似度结果，包含相似度分数和详细信息
     */
    public MaxSimilarityResult calculateMaxSimilarity(
        TestCase testCase,
         TestScenarioAnalysis analysis, 
         FilterFieldConfig config, 
         Map<String, List<Double>> vectorCache) {
        // 1. 获取测试用例的文本
        String testCaseText = ReflectionUtils.getFieldValueGeneric(testCase, config.getTestCaseField());
        if (testCaseText == null || testCaseText.isEmpty()) {
            return MaxSimilarityResult.createEmptyResult();
        }
        
        // 2. 获取候选列表
        List<String> candidates = ReflectionUtils.getFieldValueGeneric(analysis, config.getAnalysisField());
        if (candidates == null || candidates.isEmpty()) {
            return MaxSimilarityResult.createEmptyResult();
        }
        
        // 3. 获取测试用例文本的向量
        List<Double> testCaseVector = VectorUtils.getVectorFromCache(testCaseText, vectorCache);
        
        // 4. 计算与每个候选项的相似度
        double maxScore = 0.0;
        String bestMatch = null;
        int bestMatchIndex = -1;
        List<Map<String, Object>> allMatches = new ArrayList<>();
        
        for (int i = 0; i < candidates.size(); i++) {
            String candidate = candidates.get(i);
            
            // 获取候选项的向量
            List<Double> candidateVector = VectorUtils.getVectorFromCache(candidate, vectorCache);
            
            // 计算余弦相似度
            double similarity = CosineSimilarity.calculate(testCaseVector, candidateVector);
            
            // 记录匹配详情
            Map<String, Object> match = new HashMap<>();
            match.put("analysisText", candidate);
            match.put("similarity", similarity);
            allMatches.add(match);
            
            // 更新最大值
            if (similarity > maxScore) {
                maxScore = similarity;
                bestMatch = candidate;
                bestMatchIndex = i;
            }
        }
        
        // 5. 检查阈值（如果配置了）
        // 阈值用于判断相似度匹配是否通过的临界值
        // 当计算的相似度分数低于此值时，认为匹配失败
        if (config.getThreshold() != null && maxScore < config.getThreshold()) {
            // 硬过滤失败的情况，detail 中需要记录阈值检查
            Map<String, Object> detail = new HashMap<>();
            detail.put("strategy", "MAX");
            detail.put("testCaseText", testCaseText);
            detail.put("bestMatch", bestMatch);
            detail.put("bestMatchIndex", bestMatchIndex);
            detail.put("bestScore", maxScore);
            detail.put("totalCompared", candidates.size());
            detail.put("allMatches", allMatches);
            
            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", maxScore);
            thresholdCheck.put("passed", false);
            detail.put("thresholdCheck", thresholdCheck);
            
            return new MaxSimilarityResult(maxScore, detail);
        }
        
        // 6. 构造返回结果
        Map<String, Object> detail = new HashMap<>();
        detail.put("strategy", "MAX");
        detail.put("testCaseText", testCaseText);
        detail.put("bestMatch", bestMatch);
        detail.put("bestMatchIndex", bestMatchIndex);
        detail.put("bestScore", maxScore);
        detail.put("totalCompared", candidates.size());
        detail.put("allMatches", allMatches);
        if (config.getThreshold() != null) {
            detail.put("thresholdCheck", Map.of(
                "threshold", config.getThreshold(),
                "actualScore", maxScore,
                "passed", true
            ));
        }
        return new MaxSimilarityResult(maxScore, detail);
    }

    public Double calculateTestCaseScore(
            TestCase testCase,
            TestScenarioAnalysis analysis,
            FilterFieldConfig config,
            Map<String, List<Double>> vectorCache) {
        double totalScore = 0.0;
        int configCount = 0;

        MaxSimilarityResult result = calculateMaxSimilarity(testCase, analysis, config, vectorCache);
        if (result != null && result.getDetail() != null) {
            totalScore += result.getScore() * config.getWeight();
            configCount++;
        }

        return configCount > 0 ? totalScore : null;
    }

    /**
     * 并行计算测试用例相似度分数
     * <p>
     * 使用多线程并发处理测试用例集合，提高计算效率
     * 
     * @param testCases 测试用例集合
     * @param analysis 测试场景分析结果对象
     * @param filterConfigs 字段配置映射
     * @param vectorCache 向量缓存
     * @param threadCount 线程数
     * @return 测试用例与分数的映射列表
     */
    public List<Map.Entry<TestCase, Double>> calculateTestCaseScoresParallel(
            List<TestCase> testCases,
            TestScenarioAnalysis analysis,
            Map<String, FilterFieldConfig> filterConfigs,
            Map<String, List<Double>> vectorCache,
            int threadCount) {
        // System.out.println("[MaxSimilarityAlgorithm] 开始并行计算测试用例相似度 - 线程数: " + threadCount + ", 测试用例数: " + testCases.size());
        
        List<Map.Entry<TestCase, Double>> scoredTestCases = new ArrayList<>();
        List<Map.Entry<TestCase, Double>> threadResults = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < testCases.size(); i++) {
                final TestCase testCase = testCases.get(i);
                
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    double totalScore = 0.0;
                    int configCount = 0;

                    for (FilterFieldConfig config : filterConfigs.values()) {
                        try {
                            MaxSimilarityResult result = calculateMaxSimilarity(
                                    testCase, analysis, config, vectorCache
                            );

                            if (result != null && result.getDetail() != null) {
                                totalScore += result.getScore() * config.getWeight();
                                configCount++;
                            }
                        } catch (Exception e) {
                            System.err.println("[MaxSimilarityAlgorithm] 计算配置 " + config.getName() + " 的相似度失败: " + e.getMessage());
                        }
                    }

                    synchronized (threadResults) {
                        if (configCount > 0) {
                            threadResults.add(new AbstractMap.SimpleEntry<>(testCase, totalScore));
                        }
                    }
                }, executor);

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            scoredTestCases.addAll(threadResults);
            
            // System.out.println("[MaxSimilarityAlgorithm] 并行计算完成 - 耗时: " + (endTime - startTime) + "ms, 有效得分测试用例数: " + scoredTestCases.size() + "/" + testCases.size());
        } finally {
            executor.shutdown();
        }

        return scoredTestCases;
    }

}
