package com.grape.grape.config.Kaptcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Author:Gin.44.Candy
 * @Date: 2025/10/14  15:43
 * @Version
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha advancedMathKaptcha() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties props = new Properties();
        props.setProperty("kaptcha.textproducer.impl", "com.grape.grape.config.Kaptcha.AdvancedMathTextCreator");
        // 绑定自定义生成器
        props.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        // 减轻字体扭曲，使用轻微的水波纹效果
        props.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 移除噪声干扰线
        props.setProperty("kaptcha.textproducer.font.color", "30,30,30");
        // 使用深灰色字体
        props.setProperty("kaptcha.background.clear.from", "255,255,255");
        // 白色背景
        props.setProperty("kaptcha.background.clear.to", "255,255,255");
        // 统一背景色
        props.setProperty("kaptcha.textproducer.font.size", "32");
        // 字体大小
        props.setProperty("kaptcha.image.width", "150");
        // 图片宽度
        props.setProperty("kaptcha.image.height", "50");
        // 图片高度
        kaptcha.setConfig(new Config(props));
        return kaptcha;
    }


}
