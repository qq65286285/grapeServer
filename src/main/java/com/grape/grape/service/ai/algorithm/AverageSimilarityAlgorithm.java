package com.grape.grape.service.ai.algorithm;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import com.grape.grape.model.prompt.ai.algorithm.AverageSimilarityResult;
import com.grape.grape.service.ai.OllamaVectorService;
import com.grape.grape.utils.ai.ReflectionUtils;
import com.grape.grape.utils.ai.VectorUtils;
import com.grape.grape.utils.ai.calamethods.CosineSimilarity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平均相似度算法
 * <p>
 * 该算法用于计算测试用例字段与分析结果中多个候选项的相似度，取平均值作为该字段得分。
 * <p>
 * 核心功能：
 * 1. 从测试用例获取待比较的文本
 * 2. 从分析结果获取候选列表
 * 3. 计算测试用例文本与每个候选文本的余弦相似度
 * 4. 计算所有相似度的平均值
 * 5. 检查阈值
 * 6. 构造返回结果
 */
@Service
public class AverageSimilarityAlgorithm {

    @Autowired
    private OllamaVectorService ollamaVectorService;

    /**
     * 计算平均相似度结果
     * <p>
     * 计算测试用例字段与分析结果中多个候选项的相似度，取平均值作为该字段得分
     * 
     * @param testCase 测试用例对象
     * @param analysis 测试场景分析结果对象
     * @param config 字段配置，包含测试用例字段路径和分析结果字段路径
     * @param vectorCache 向量缓存
     * @return 平均相似度结果，包含相似度分数和详细信息
     */
    public AverageSimilarityResult calculateAverageSimilarity(
        TestCase testCase,
        TestScenarioAnalysis analysis,
        FilterFieldConfig config,
        Map<String, List<Double>> vectorCache) {
        // 1. 获取测试用例的文本
        String testCaseText = ReflectionUtils.getFieldValueGeneric(testCase, config.getTestCaseField());
        if (testCaseText == null || testCaseText.isEmpty()) {
            return AverageSimilarityResult.createEmptyResult();
        }

        // 2. 获取候选列表
        List<String> candidates = ReflectionUtils.getFieldValueGeneric(analysis, config.getAnalysisField());
        if (candidates == null || candidates.isEmpty()) {
            return AverageSimilarityResult.createEmptyResult();
        }

        // 3. 获取测试用例文本的向量
        List<Double> testCaseVector = VectorUtils.getVectorFromCache(testCaseText, vectorCache);

        // 4. 计算与每个候选项的相似度
        double sumScore = 0.0;
        List<Map<String, Object>> allMatches = new ArrayList<>();

        for (int i = 0; i < candidates.size(); i++) {
            String candidate = candidates.get(i);

            // 获取候选项的向量
            List<Double> candidateVector = VectorUtils.getVectorFromCache(candidate, vectorCache);

            // 计算余弦相似度
            double similarity = CosineSimilarity.calculate(testCaseVector, candidateVector);

            // 累加分数
            sumScore += similarity;

            // 记录匹配详情
            Map<String, Object> match = new HashMap<>();
            match.put("analysisText", candidate);
            match.put("similarity", similarity);
            allMatches.add(match);
        }

        // 5. 计算平均值
        double averageScore = sumScore / candidates.size();

        // 6. 检查阈值（如果配置了）
        // 阈值用于判断相似度匹配是否通过的临界值
        // 当计算的相似度分数低于此值时，认为匹配失败
        if (config.getThreshold() != null && averageScore < config.getThreshold()) {
            // 硬过滤失败的情况，detail 中需要记录阈值检查
            Map<String, Object> detail = new HashMap<>();
            detail.put("strategy", "AVERAGE");
            detail.put("testCaseText", testCaseText);
            detail.put("averageScore", averageScore);
            detail.put("totalCompared", candidates.size());
            detail.put("allMatches", allMatches);

            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", averageScore);
            thresholdCheck.put("passed", false);
            detail.put("thresholdCheck", thresholdCheck);

            return new AverageSimilarityResult(averageScore, detail);
        }

        // 7. 构造返回结果
        Map<String, Object> detail = new HashMap<>();
        detail.put("strategy", "AVERAGE");
        detail.put("testCaseText", testCaseText);
        detail.put("averageScore", averageScore);
        detail.put("totalCompared", candidates.size());
        detail.put("allMatches", allMatches);

        if (config.getThreshold() != null) {
            Map<String, Object> thresholdCheck = new HashMap<>();
            thresholdCheck.put("threshold", config.getThreshold());
            thresholdCheck.put("actualScore", averageScore);
            thresholdCheck.put("passed", true);
            detail.put("thresholdCheck", thresholdCheck);
        }

        return new AverageSimilarityResult(averageScore, detail);
    }

}
