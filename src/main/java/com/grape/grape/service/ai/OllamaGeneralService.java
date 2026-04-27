package com.grape.grape.service.ai;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaGeneralService {

    @Value("${ollama.base.url}")
    private String ollamaBaseUrl;

    @Value("${ollama.general.model}")
    private String generalModel;

    @Value("${ollama.timeout.connect}")
    private int connectTimeout;

    @Value("${ollama.timeout.read}")
    private int readTimeout;

    @Value("${ollama.timeout.write}")
    private int writeTimeout;

    @Value("${ollama.max.retries}")
    private int maxRetries;

    @Value("${ollama.api.generate}")
    private String ollamaApiGenerate;

    private final OllamaClient ollamaClient;

    @Autowired
    public OllamaGeneralService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    /**
     * 提取测试场景中的关键信息
     * @param testScenario 测试场景描述
     * @param prompt 提示词
     * @return 提取的关键信息
     * @throws Exception 当提取关键信息失败时抛出异常
     */
    public String extractKeyInfo(String testScenario, String prompt) throws Exception {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", generalModel);
        requestBody.put("prompt", prompt);
        requestBody.put("format", "json");
        requestBody.put("stream", false);
        
        // 发送请求
        System.out.println("Sending request to Ollama API...   " + DateTime.now().toString(DatePattern.NORM_DATETIME_PATTERN));
        
        String response = ollamaClient.withRetry(() -> 
            ollamaClient.sendSyncRequest(ollamaApiGenerate, requestBody)
        );
        // 打印原始响应
        // System.out.println("Raw Response: " + response);
        System.out.println("Received response from Ollama API:   " + DateTime.now().toString(DatePattern.NORM_DATETIME_PATTERN));

        // 解析响应
        JSONObject jsonResponse = ollamaClient.parseJsonResponse(response);
        String content = jsonResponse.getStr("response");
        // 处理大模型返回null的情况
        if (content == null || content.isEmpty()) {
            throw new Exception("Model returned null or empty response");
        }
        
        // 尝试解析返回的JSON，确保它符合TestScenarioAnalysis的结构
        try {
            return content;
        } catch (Exception e) {
            throw new Exception("Error parsing JSON response: " + e.getMessage(), e);
        }
    }

    /**
     * 通用文本生成方法
     * @param prompt 提示词
     * @return 生成的文本
     */
    public String generateText(String prompt) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("num_predict", 4096);
            options.put("temperature", 0.7);
            options.put("repeat_penalty", 1.0);
            options.put("stop", new ArrayList<>());

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", generalModel);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            // requestBody.put("format", "json");
            requestBody.put("options", options);

            // 发送请求
            System.out.println("-----------------");
            System.out.println("Request Body: " + JSONUtil.toJsonPrettyStr(requestBody));
            System.out.println("-----------------");
            System.out.println("Sending request to Ollama API...   " + DateTime.now().toString(DatePattern.NORM_DATETIME_PATTERN));
            String response = ollamaClient.withRetry(() -> 
                ollamaClient.sendSyncRequest(ollamaApiGenerate, requestBody)
            );
            System.out.println("Received response from Ollama API:   " + DateTime.now().toString(DatePattern.NORM_DATETIME_PATTERN));
            
            // 解析响应
            JSONObject jsonResponse = ollamaClient.parseJsonResponse(response);
            String content = jsonResponse.getStr("response");

            return content;
        } catch (Exception e) {
            System.out.println("Error generating text: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * 将JSON字符串转换为TestScenarioAnalysis对象
     * @param jsonString JSON字符串
     * @return TestScenarioAnalysis对象
     */
    public TestScenarioAnalysis parseTestScenarioAnalysis(String jsonString) {
        try {
            return JSONUtil.toBean(jsonString, TestScenarioAnalysis.class);
        } catch (Exception e) {
            System.out.println("Error parsing test scenario analysis: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}