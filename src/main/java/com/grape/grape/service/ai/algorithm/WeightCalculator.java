package com.grape.grape.service.ai.algorithm;

import com.grape.grape.config.FilterFieldConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 权重归一化算法实现
 * 
 * 该算法用于将字段的权重值归一化到0-1之间。
 * 主要应用于多路召回结果的权重分配，确保不同来源的权重能够合理融合。
 */
@Service
public class WeightCalculator {
    private static final Logger log = LoggerFactory.getLogger(WeightCalculator.class);
    
    /**
     * 计算归一化后的权重分配
     * @param activeFields 活跃字段集合
     * @param filterFieldConfig 过滤字段配置
     * @return 归一化后的权重映射
     */
    public Map<String, Double> calculateNormalizedWeights(
        Set<String> activeFields, FilterFieldConfig filterFieldConfig) {
        // 1. 收集有权重的字段
        Map<String, Double> rawWeights = new HashMap<>();
        
        if (filterFieldConfig != null && filterFieldConfig.getWeight() != null) {
            rawWeights.put(filterFieldConfig.getName(), filterFieldConfig.getWeight());
        }
        
        // 2. 边界检查
        if (rawWeights.isEmpty()) {
            log.debug("没有有权重的字段，返回空Map");
            return new HashMap<>();
        }
        
        // 3. 计算总和
        double totalWeight = rawWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // 4. 边界检查：权重总和为0
        if (totalWeight == 0) {
            log.warn("权重总和为0，使用平均分配");
            double avgWeight = 1.0 / rawWeights.size();
            Map<String, Double> normalized = new HashMap<>();
            for (String fieldName : rawWeights.keySet()) {
                normalized.put(fieldName, avgWeight);
            }
            log.debug("平均分配权重: " + normalized);
            return normalized;
        }
        
        // 5. 归一化
        Map<String, Double> normalized = new HashMap<>();
        for (Map.Entry<String, Double> entry : rawWeights.entrySet()) {
            normalized.put(entry.getKey(), entry.getValue() / totalWeight);
        }
        
        // 6. 日志输出
        log.debug("原始权重: " + rawWeights);
        log.debug("归一化权重: " + normalized);
        
        return normalized;
    }
}
