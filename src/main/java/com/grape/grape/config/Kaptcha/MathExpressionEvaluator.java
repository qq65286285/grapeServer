package com.grape.grape.config.Kaptcha;

/**
 * @Author:Gin.44.Candy
 * @Date: 2025/10/14  16:10
 * @Version 
 */


public class MathExpressionEvaluator {
    // 新增静态方法作为外部接口
    public static double evaluateExpression(String expression) {
        return new MathExpressionEvaluator(expression).calculateSimpleExpr();
    }

    // 原有计算逻辑保持不变（参考历史对话）
    private int index = 0;
    private final String expr;

    public MathExpressionEvaluator(String expr) {
        this.expr  = expr.replaceAll("\\s+",  "");
    }

    public double calculateSimpleExpr() {
        index = 0;
        return parseExpression();
    }

    // 解析表达式（加减）
    public double parseExpression() {
        double result = parseTerm();
        while (index < expr.length())  {
            char op = expr.charAt(index);
            if (op == '+' || op == '-') {
                index++;
                double term = parseTerm();
                result = (op == '+') ? result + term : result - term;
            } else {
                break;
            }
        }
        return result;
    }

    // 解析项（乘除）
    private double parseTerm() {
        double result = parseFactor();
        while (index < expr.length())  {
            char op = expr.charAt(index);
            if (op == '*' || op == '/' || op == '÷') {
                index++;
                double factor = parseFactor();
                result = (op == '*') ? result * factor : result / factor;
            } else {
                break;
            }
        }
        return result;
    }

    // 解析因子（处理正负号/平方/根号）
    private double parseFactor() {
        // 处理正负号
        int sign = 1;
        if (index < expr.length()  && (expr.charAt(index)  == '+' || expr.charAt(index)  == '-')) {
            sign = (expr.charAt(index)  == '-') ? -1 : 1;
            index++;
        }
// 解析幂运算
        double result = parsePower();
        result *= sign;

        // 处理连续平方
        while (index < expr.length()  && expr.charAt(index)  == '²') {
            index++;
            result = result * result;
        }
        return result;
    }

    // 解析幂运算和根号
    private double parsePower() {
        double result = parsePrimary();

        // 处理根号（前缀）
        while (index < expr.length()  && expr.charAt(index)  == '√') {
            index++;
            double operand = parsePrimary();
            result = Math.sqrt(operand);
        }
        return result;
    }

    // 解析基本元素（数字/括号）
    private double parsePrimary() {
        if (expr.charAt(index)  == '(') {
            index++;  // 跳过'('
            double result = parseExpression();
            if (index < expr.length()  && expr.charAt(index)  == ')') {
                index++;  // 跳过')'
            }
            return result;
        } else {
            // 解析数字
            return parseNumber();
        }
    }

    // 解析数字（整数/小数）
    private double parseNumber() {
        int start = index;
        // 遍历字符，找到数字的结束位置
        while (index < expr.length() && (Character.isDigit(expr.charAt(index)) || expr.charAt(index) == '.')) {
            index++;
        }

        // 添加空字符串检查
        String numberStr = expr.substring(start, index);
        if (numberStr.isEmpty()) {
            throw new IllegalArgumentException("Invalid number format at position " + start);
        }

        try {
            return Double.parseDouble(numberStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + numberStr, e);
        }
    }
}