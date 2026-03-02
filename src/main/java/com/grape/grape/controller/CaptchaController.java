package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.grape.grape.service.biz.CaptchaBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码控制器
 * 负责处理验证码相关的HTTP请求，包括验证码生成和验证
 * 
 * 主要功能：
 * 1. 生成验证码图片（数学运算题）
 * 2. 验证用户输入的答案
 * 3. 防止恶意自动化攻击
 * 
 * @author grape-team
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    /**
     * 验证码业务服务
     * 负责验证码的生成、验证和缓存管理
     * 
     * 调用说明：
     * - 调用该服务的generateCaptcha()方法生成验证码
     * - 调用该服务的verifyCaptcha()方法验证用户输入
     * 
     * 数据库调用：
     * - 不直接调用数据库，使用内存缓存存储验证码
     * - 验证码有效期为5分钟
     */
    @Resource
    private CaptchaBizService captchaBizService;

    /**
     * 生成验证码图片
     * 
     * 功能说明：
     * 1. 接收验证码ID作为参数
     * 2. 调用业务服务生成数学运算验证码
     * 3. 将验证码图片输出到HTTP响应流
     * 4. 将验证码答案存储到缓存中
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，参数包含验证码ID
     * - 参数校验：验证captchaId不为空
     * - 验证码生成：调用业务服务生成数学运算题和图片
     * - 缓存存储：将验证码答案和过期时间存入缓存
     * - 图片输出：将验证码图片以JPEG格式输出到响应流
     * - 结束：完成验证码生成流程
     * 
     * 调用服务：
     * - 调用 captchaBizService.generateCaptcha(captchaId) 生成验证码
     *   该方法内部会执行以下操作：
     *   1. 生成两个随机数和运算符（+、-、*）
     *   2. 计算正确答案
     *   3. 生成包含数学题目的验证码图片
     *   4. 将答案和过期时间存入缓存
     *   5. 返回验证码结果对象（包含图片和答案）
     * 
     * 调用外部服务：
     * - 调用 Java AWT绘图API生成验证码图片
     *   使用Graphics2D绘制数学运算题和干扰线
     * 
     * 缓存调用：
     * - 将验证码答案存入内存缓存（ConcurrentHashMap）
     * - 缓存键：captchaId
     * - 缓存值：{answer: 正确答案, expireTime: 过期时间}
     * - 有效期：5分钟
     * 
     * 数据库调用：
     * - 无数据库调用，使用内存缓存
     * 
     * @param captchaId 验证码唯一标识，用于后续验证
     *                   通常使用UUID或用户SessionID
     *                   客户端需要在后续验证时携带此ID
     * @param response HTTP响应对象，用于输出验证码图片
     * @throws IOException 当图片输出失败时抛出IO异常
     * @return void 图片直接输出到响应流，不返回JSON数据
     */
    @GetMapping("/gen")
    public void generateCaptcha(@RequestParam String captchaId, HttpServletResponse response) throws IOException {
        // 方法开始：准备生成验证码
        log.info("开始生成验证码，验证码ID: {}", captchaId);
        
        // 设置响应内容类型为JPEG图片
        response.setContentType("image/jpeg");
        
        // 调用业务服务生成验证码
        // 该调用会：
        // 1. 生成数学运算题（如：3 + 5 = ?）
        // 2. 计算正确答案
        // 3. 生成验证码图片（包含干扰线）
        // 4. 将答案存入缓存，有效期为5分钟
        CaptchaBizService.CaptchaResult captchaResult = captchaBizService.generateCaptcha(captchaId);
        
        // 输出图片流
        // 使用Java ImageIO将BufferedImage以JPEG格式输出到响应流
        ImageIO.write(captchaResult.getImage(), "jpg", response.getOutputStream());
        
        // 方法结束：完成验证码生成和输出
        log.info("验证码生成完成，验证码ID: {}，答案: {}", captchaId, captchaResult.getAnswer());
    }

    /**
     * 验证用户输入
     * 
     * 功能说明：
     * 1. 接收验证码ID和用户输入的答案
     * 2. 从缓存中获取验证码的正确答案
     * 3. 验证用户输入是否正确
     * 4. 验证后从缓存中删除验证码（一次性使用）
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，参数包含验证码ID和用户输入
     * - 参数校验：验证captchaId和userInput不为空
     * - 缓存查询：从缓存中获取验证码的正确答案
     * - 过期检查：检查验证码是否已过期
     * - 答案验证：比较用户输入和正确答案
     * - 缓存清理：验证成功或失败后删除验证码
     * - 响应构建：返回验证结果
     * - 结束：完成验证流程
     * 
     * 调用服务：
     * - 调用 captchaBizService.verifyCaptcha(captchaId, userInput) 验证用户输入
     *   该方法内部会执行以下操作：
     *   1. 从缓存中获取验证码信息（答案和过期时间）
     *   2. 检查验证码是否过期
     *   3. 比较用户输入和正确答案（允许误差范围）
     *   4. 删除缓存中的验证码（一次性使用）
     *   5. 返回验证结果（true/false）
     * 
     * 缓存调用：
     * - 查询缓存：从内存缓存中获取验证码信息
     *   缓存键：captchaId
     *   返回值：{answer: 正确答案, expireTime: 过期时间} 或 null（不存在或已过期）
     * - 删除缓存：验证后删除验证码
     *   删除键：captchaId
     * 
     * 数据库调用：
     * - 无数据库调用，使用内存缓存
     * 
     * @param captchaId 验证码唯一标识，必须与生成验证码时使用的ID一致
     * @param userInput 用户输入的答案（数学运算题的计算结果）
     *                   例如：如果验证码是"3 + 5 = ?"，则输入8
     * @return Resp 响应对象，包含验证结果
     *         - 成功时：code=0, message="验证成功"
     *         - 失败时：code=1001, message="验证码错误"
     *         - 失败时：code=400, message="验证码已过期或不存在"
     */
    @PostMapping("/verify")
    public Resp verifyCaptcha(@RequestParam String captchaId, @RequestParam double userInput) {
        // 方法开始：准备验证用户输入
        log.info("开始验证验证码，验证码ID: {}，用户输入: {}", captchaId, userInput);
        
        // 调用业务服务验证验证码
        // 该调用会：
        // 1. 从缓存中获取验证码的正确答案
        // 2. 检查验证码是否已过期（有效期为5分钟）
        // 3. 比较用户输入和正确答案（允许误差范围±0.1）
        // 4. 删除缓存中的验证码（一次性使用，防止重复使用）
        boolean isValid = captchaBizService.verifyCaptcha(captchaId, userInput);
        
        // 方法中间：根据验证结果返回响应
        if (isValid) {
            // 验证成功
            log.info("验证码验证成功，验证码ID: {}", captchaId);
            return Resp.ok("验证成功");
        } else {
            // 验证失败
            log.warn("验证码验证失败，验证码ID: {}，用户输入: {}", captchaId, userInput);
            return Resp.info(ResultEnumI18n.CODE_ERROR.getCode(), "验证码错误");
        }
    }
}
