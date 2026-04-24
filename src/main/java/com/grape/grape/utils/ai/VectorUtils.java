package com.grape.grape.utils.ai;

import com.grape.grape.service.ai.OllamaVectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 向量工具类
 * <p>
 * 提供向量缓存和计算相关的工具方法
 */
@Component
public class VectorUtils {

    private static OllamaVectorService ollamaVectorService;

    @Autowired
    public void setOllamaVectorService(OllamaVectorService service) {
        ollamaVectorService = service;
    }

    /**
     * 从缓存获取向量（如果不存在则调用 embedding 服务）
     *
     * @param text 文本内容
     * @param cache 向量缓存
     * @return 向量
     */
    public static List<Double> getVectorFromCache(String text, Map<String, List<Double>> cache) {
        if (cache.containsKey(text)) {
            return cache.get(text);
        }

        // 实时计算
        float[] embedding = ollamaVectorService.embed(text);
        List<Double> vector = new ArrayList<>();
        for (float value : embedding) {
            vector.add((double) value);
        }
        cache.put(text, vector);

        return vector;
    }

    /**
     * 将 Map<String, float[]> 转换为 Map<String, List<Double>>
     * @param floatCache float类型的向量缓存
     * @return Double类型的向量缓存
     */
    public static Map<String, List<Double>> convertToDoubleListCache(Map<String, float[]> floatCache) {
        Map<String, List<Double>> doubleCache = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, float[]> entry : floatCache.entrySet()) {
            List<Double> doubleList = new ArrayList<>();
            for (float value : entry.getValue()) {
                doubleList.add((double) value);
            }
            doubleCache.put(entry.getKey(), doubleList);
        }
        return doubleCache;
    }

}
