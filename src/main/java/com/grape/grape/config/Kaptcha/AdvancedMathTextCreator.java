
package com.grape.grape.config.Kaptcha;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.security.SecureRandom;

/**
 * @Author:Gin.44.Candy
 * @Date: 2025/10/14  15:37
 * @Version
 */
public class AdvancedMathTextCreator extends DefaultTextCreator {
    // 扩展高等数学符号
    private static final String[] OPERATORS = {"+", "-", "*", "÷", "²", "√"};
    private final SecureRandom rand = new SecureRandom();

    @Override
    public String getText() {
        int x = rand.nextInt(100) + 1;
        int y = rand.nextInt(50) + 1;
        String operator = OPERATORS[rand.nextInt(OPERATORS.length)];

        // 示例：生成 √(x+y) 或 x² 等表达式
        switch (operator) {
            case "+":
                // 示例：生成 x+y
                return String.format("%d+%d", x, y);
            case "-":
                return String.format("%d-%d", x, y);
            case "*":
                return String.format("%d*%d", x, y);
            case "÷":
                return String.format("%d/%d", x, y);
            case "²":
                // 平方运算，生成 x²
                return String.format("%d²", x);
            case "√":
                // 根号运算，生成 √(x+y)
                return String.format("√(%d+%d)", x, y);
            default:
                return String.format("%s(%d,%d)", operator, x, y);
        }
    }
}