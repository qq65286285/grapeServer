package com.grape.grape.utils.ai.calamethods;

import java.util.List;

/**
 * 向量工具类
 * <p>
 * 提供向量相关的数学计算方法
 */
public final class VectorMathUtils {

    private VectorMathUtils() {
    }

    /**
     * 计算向量的模（L2范数）
     * <p>
     * L2 范数公式：||A|| = √(Σ(a_i²))
     * 即：所有元素平方和的平方根
     *
     * @param vector 输入向量
     * @return 向量的模
     * @throws IllegalArgumentException 如果向量为 null
     */
    public static double vectorNorm(List<Double> vector) {
        // 1. 边界检查
        if (vector == null) {
            throw new IllegalArgumentException("向量不能为 null");
        }

        if (vector.isEmpty()) {
            return 0.0;
        }

        // 2. 计算平方和
        double sumOfSquares = 0.0;

        for (Double value : vector) {
            sumOfSquares += value * value;
        }

        // 3. 返回平方根
        return Math.sqrt(sumOfSquares);
    }
}
