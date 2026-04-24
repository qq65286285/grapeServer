package com.grape.grape.utils.ai.calamethods;

import java.util.List;

/**
 * 余弦相似度计算工具类
 * <p>
 * 提供向量相似度计算的静态方法
 */
public final class CosineSimilarity {

    private CosineSimilarity() {
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param vectorA 向量A
     * @param vectorB 向量B
     * @return 余弦相似度，范围[-1, 1]
     * @throws IllegalArgumentException 如果向量为null、为空或长度不一致
     */
    public static double calculate(List<Double> vectorA, List<Double> vectorB) {
        // 1. 边界检查
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("向量不能为 null");
        }
        
        if (vectorA.isEmpty() || vectorB.isEmpty()) {
            throw new IllegalArgumentException("向量不能为空");
        }
        
        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException(
                String.format("向量长度不一致: %d vs %d",
                    vectorA.size(), vectorB.size())
            );
        }

        // 2. 计算点积和模
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            double a = vectorA.get(i);
            double b = vectorB.get(i);
            
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        // 3. 计算模的平方根
        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        // 4. 处理零向量
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        // 5. 计算余弦相似度
        double cosineSim = dotProduct / (normA * normB);

        // 6. 范围限制（防止浮点误差导致超出范围）
        if (cosineSim > 1.0) {
            cosineSim = 1.0;
        }
        if (cosineSim < -1.0) {
            cosineSim = -1.0;
        }

        return cosineSim;
    }
}