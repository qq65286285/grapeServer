package com.grape.grape.service.biz;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.grape.grape.config.Kaptcha.MathExpressionEvaluator;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码业务服务类
 * 负责处理验证码的生成、验证等业务逻辑
 */
@Service
public class CaptchaBizService {
    
    @Resource
    private DefaultKaptcha advancedMathKaptcha;
    
    // 使用线程安全的 ConcurrentHashMap 存储验证码答案
    private final Map<String, Double> captchaMap = new ConcurrentHashMap<>();
    
    /**
     * 生成验证码
     * @param captchaId 验证码ID
     * @return 验证码信息，包含表达式和图片
     */
    public CaptchaResult generateCaptcha(String captchaId) {
        // 生成验证码表达式
        String expression = advancedMathKaptcha.createText();
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalStateException("Failed to generate captcha expression");
        }
        
        // 计算表达式答案
        double answer = MathExpressionEvaluator.evaluateExpression(expression);
        
        // 生成验证码图片
        BufferedImage image = advancedMathKaptcha.createImage(expression);
        
        // 存储验证码答案
        captchaMap.put(captchaId, answer);
        
        return new CaptchaResult(expression, image, answer);
    }
    
    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param userInput 用户输入
     * @return 是否验证成功
     */
    public boolean verifyCaptcha(String captchaId, double userInput) {
        Double answer = captchaMap.get(captchaId);
        if (answer == null) {
            return false;
        }
        
        // 允许浮点数误差
        boolean isValid = Math.abs(userInput - answer) < 0.01;
        
        // 验证后移除验证码（可选，根据业务需求）
        if (isValid) {
            captchaMap.remove(captchaId);
        }
        
        return isValid;
    }
    
    /**
     * 计算简单表达式值
     * @param expression 表达式
     * @return 计算结果
     */
    public double calculateSimpleExpr(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }
        
        try {
            return MathExpressionEvaluator.evaluateExpression(expression);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to evaluate expression: " + expression, e);
        }
    }
    
    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private final String expression;
        private final BufferedImage image;
        private final double answer;
        
        public CaptchaResult(String expression, BufferedImage image, double answer) {
            this.expression = expression;
            this.image = image;
            this.answer = answer;
        }
        
        public String getExpression() {
            return expression;
        }
        
        public BufferedImage getImage() {
            return image;
        }
        
        public double getAnswer() {
            return answer;
        }
    }
}