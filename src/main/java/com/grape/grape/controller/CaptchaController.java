package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.grape.grape.service.biz.CaptchaBizService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码控制器
 * 负责处理验证码相关的 HTTP 请求
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private CaptchaBizService captchaBizService;

    /**
     * 生成验证码图片
     * @param captchaId 验证码ID
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/gen")
    public void generateCaptcha(@RequestParam String captchaId, HttpServletResponse response) throws IOException {
        // 调用业务服务生成验证码
        CaptchaBizService.CaptchaResult captchaResult = captchaBizService.generateCaptcha(captchaId);
        
        // 输出图片流
        response.setContentType("image/jpeg");
        ImageIO.write(captchaResult.getImage(), "jpg", response.getOutputStream());
    }

    /**
     * 验证用户输入
     * @param captchaId 验证码ID
     * @param userInput 用户输入
     * @return 验证结果
     */
    @PostMapping("/verify")
    public Resp verifyCaptcha(@RequestParam String captchaId, @RequestParam double userInput) {
        // 调用业务服务验证验证码
        boolean isValid = captchaBizService.verifyCaptcha(captchaId, userInput);
        
        if (isValid) {
            return Resp.ok("验证成功");
        }
        return Resp.info(ResultEnumI18n.CODE_ERROR.getCode(), "验证码错误");
    }

}
