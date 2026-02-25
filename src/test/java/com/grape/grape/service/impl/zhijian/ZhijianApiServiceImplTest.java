package com.grape.grape.service.impl.zhijian;

import com.grape.grape.config.ZhijianApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ZhijianApiServiceImplTest {
    
    @Autowired
    private ZhijianApiServiceImpl zhijianApiService;
    
    @Autowired
    private ZhijianApiConfig zhijianApiConfig;
    
    @Autowired
    private RestTemplate zhijianRestTemplate;
    
    @Test
    void testServiceInitialization() {
        // 验证服务和配置是否正确初始化
        assertNotNull(zhijianApiService);
        assertNotNull(zhijianApiConfig);
        assertNotNull(zhijianRestTemplate);
        
        // 验证配置参数是否正确加载
        assertNotNull(zhijianApiConfig.getBaseUrl());
        assertNotNull(zhijianApiConfig.getApiKey());
        assertTrue(zhijianApiConfig.getTimeout() > 0);
        
        System.out.println("智谱API配置: ");
        System.out.println("Base URL: " + zhijianApiConfig.getBaseUrl());
        System.out.println("API Key: " + zhijianApiConfig.getApiKey());
        System.out.println("Timeout: " + zhijianApiConfig.getTimeout() + "ms");
    }
    
    // 注意：以下测试需要有效的智谱API密钥才能运行
    // 实际使用时，建议将API密钥配置在环境变量中，而不是直接硬编码在配置文件中
    
    @Test
    void testGenerateText() {
        // 此测试需要有效的API密钥
        // 取消注释以下代码以测试实际的API调用
        
        /*
        try {
            String prompt = "请简单介绍一下Spring Boot框架";
            String result = zhijianApiService.generateText(prompt);
            
            System.out.println("生成的文本: ");
            System.out.println(result);
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
        } catch (Exception e) {
            System.err.println("API调用失败: " + e.getMessage());
            System.err.println("请检查API密钥是否正确配置");
            // 仅记录异常，不中断测试
        }
        */
        
        System.out.println("跳过智谱API文本生成测试（需要有效API密钥）");
    }
    
    @Test
    void testChat() {
        // 此测试需要有效的API密钥
        // 取消注释以下代码以测试实际的API调用
        
        /*
        try {
            String[] messages = {
                "你好，我是一名开发者",
                "很高兴认识你！有什么我可以帮助你的吗？",
                "请简单解释一下什么是微服务架构"
            };
            
            String result = zhijianApiService.chat(messages);
            
            System.out.println("对话结果: ");
            System.out.println(result);
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
        } catch (Exception e) {
            System.err.println("API调用失败: " + e.getMessage());
            System.err.println("请检查API密钥是否正确配置");
            // 仅记录异常，不中断测试
        }
        */
        
        System.out.println("跳过智谱API对话测试（需要有效API密钥）");
    }
    
    @Test
    void testEmbedText() {
        // 此测试需要有效的API密钥
        // 取消注释以下代码以测试实际的API调用
        
        /*
        try {
            String text = "这是一段测试文本，用于生成文本嵌入";
            double[] embedding = zhijianApiService.embedText(text);
            
            System.out.println("生成的文本嵌入维度: " + embedding.length);
            System.out.println("前5个嵌入值: ");
            for (int i = 0; i < Math.min(5, embedding.length); i++) {
                System.out.println(embedding[i]);
            }
            
            assertNotNull(embedding);
            assertTrue(embedding.length > 0);
        } catch (Exception e) {
            System.err.println("API调用失败: " + e.getMessage());
            System.err.println("请检查API密钥是否正确配置");
            // 仅记录异常，不中断测试
        }
        */
        
        System.out.println("跳过智谱API文本嵌入测试（需要有效API密钥）");
    }
}