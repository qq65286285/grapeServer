package com.grape.grape.utils.ai.calamethods;

import java.util.List;

/**
 * 向量操作工具类
 * <p>
 * 提供向量相关的计算方法
 */
public class VectorOperations {

    /**
     * 计算两个向量的点积（内积）
     * <p>
     * 点积公式：A · B = Σ(a_i × b_i)
     * 即：对应位置元素相乘后求和
     *
     * @param vectorA 向量A
     * @param vectorB 向量B
     * @return 点积结果
     * @throws IllegalArgumentException 如果向量为null或长度不一致
     */
    public static double dotProduct(List<Double> vectorA, List<Double> vectorB) {
        // 1. 边界检查
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("向量不能为 null");
        }

        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException(
                String.format("向量长度不一致: %d vs %d",
                    vectorA.size(), vectorB.size())
            );
        }

        // 2. 计算点积
        double result = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            result += vectorA.get(i) * vectorB.get(i);
        }

        return result;
    }
}
