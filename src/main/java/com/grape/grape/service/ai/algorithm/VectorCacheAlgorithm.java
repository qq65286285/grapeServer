package com.grape.grape.service.ai.algorithm;

import com.grape.grape.service.ai.OllamaVectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 向量缓存工具算法
 * <p>
 * 提供从缓存中获取文本向量的功能，如果缓存中不存在则调用 embedding 服务计算并缓存
 */
@Service
public class VectorCacheAlgorithm {

    @Autowired
    OllamaVectorService ollamaVectorService;


    /**
     * 从缓存中获取文本的向量，如果不存在则调用 embedding 服务计算并缓存
     *
     * @param text        要向量化的文本
     * @param vectorCache 向量缓存
     * @return 文本的向量表示
     * @throws IllegalArgumentException 如果文本为空或缓存对象为 null
     * @throws RuntimeException        如果获取向量失败
     */
    public List<Double> getVectorFromCache(String text, Map<String, List<Double>> vectorCache) {
        // 1. 边界检查
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("文本不能为空");
        }

        if (vectorCache == null) {
            throw new IllegalArgumentException("缓存对象不能为 null");
        }

        // 2. 归一化文本（作为缓存 key）
        String normalizedText = text.trim();

        // 3. 检查缓存
        if (vectorCache.containsKey(normalizedText)) {
            return vectorCache.get(normalizedText);
        }

        // 4. 缓存未命中，调用 embedding 服务
        try {
            // 调用 OllamaVectorService 获取 embedding
            float[] embedding = ollamaVectorService.embed(normalizedText);

            // 5. 转换为 List<Double>
            List<Double> vector = new ArrayList<>();
            for (float value : embedding) {
                vector.add((double) value);
            }

            // 6. 验证返回的向量
            if (vector.isEmpty()) {
                throw new RuntimeException("Embedding 服务返回了空向量");
            }

            // 7. 存入缓存
            vectorCache.put(normalizedText, vector);

            // 8. 返回向量
            return vector;

        } catch (Exception e) {
            throw new RuntimeException(
                String.format("获取文本向量失败: %s", normalizedText),
                e
            );
        }
    }
}
