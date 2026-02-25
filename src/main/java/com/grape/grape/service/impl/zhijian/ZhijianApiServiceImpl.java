package com.grape.grape.service.impl.zhijian;

import com.grape.grape.config.ZhijianApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ZhijianApiServiceImpl {
    
    @Autowired
    private ZhijianApiConfig zhijianApiConfig;
    
    @Autowired
    private RestTemplate zhijianRestTemplate;
    
    // 文本生成API
    public String generateText(String prompt) {
        String url = zhijianApiConfig.getBaseUrl() + "/chat/completions";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4");
        requestBody.put("messages", new Object[]{
            Map.of("role", "user", "content", prompt)
        });
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
        ResponseEntity<Map> response = zhijianRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("choices")) {
            Object choices = responseBody.get("choices");
            if (choices instanceof java.util.List) {
                java.util.List<?> choicesList = (java.util.List<?>) choices;
                if (!choicesList.isEmpty()) {
                    Object choice = choicesList.get(0);
                    if (choice instanceof Map) {
                        Map<?, ?> choiceMap = (Map<?, ?>) choice;
                        Object message = choiceMap.get("message");
                        if (message instanceof Map) {
                            Map<?, ?> messageMap = (Map<?, ?>) message;
                            return (String) messageMap.get("content");
                        }
                    }
                }
            }
        }
        
        return "Error: Unable to generate text";
    }
    
    // 对话API（支持多轮对话）
    public String chat(String[] messages) {
        String url = zhijianApiConfig.getBaseUrl() + "/chat/completions";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4");
        
        // 转换messages数组为API需要的格式
        java.util.List<Map<String, String>> messageList = new java.util.ArrayList<>();
        for (int i = 0; i < messages.length; i += 2) {
            String role = (i % 2 == 0) ? "user" : "assistant";
            String content = messages[i];
            messageList.add(Map.of("role", role, "content", content));
        }
        
        requestBody.put("messages", messageList);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
        ResponseEntity<Map> response = zhijianRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("choices")) {
            Object choices = responseBody.get("choices");
            if (choices instanceof java.util.List) {
                java.util.List<?> choicesList = (java.util.List<?>) choices;
                if (!choicesList.isEmpty()) {
                    Object choice = choicesList.get(0);
                    if (choice instanceof Map) {
                        Map<?, ?> choiceMap = (Map<?, ?>) choice;
                        Object message = choiceMap.get("message");
                        if (message instanceof Map) {
                            Map<?, ?> messageMap = (Map<?, ?>) message;
                            return (String) messageMap.get("content");
                        }
                    }
                }
            }
        }
        
        return "Error: Unable to process chat";
    }
    
    // 文本嵌入API
    public double[] embedText(String text) {
        String url = zhijianApiConfig.getBaseUrl() + "/embeddings";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-embedding-v1");
        requestBody.put("input", text);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);
        ResponseEntity<Map> response = zhijianRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("data")) {
            Object data = responseBody.get("data");
            if (data instanceof java.util.List) {
                java.util.List<?> dataList = (java.util.List<?>) data;
                if (!dataList.isEmpty()) {
                    Object dataItem = dataList.get(0);
                    if (dataItem instanceof Map) {
                        Map<?, ?> dataItemMap = (Map<?, ?>) dataItem;
                        Object embedding = dataItemMap.get("embedding");
                        if (embedding instanceof java.util.List) {
                            java.util.List<?> embeddingList = (java.util.List<?>) embedding;
                            return embeddingList.stream()
                                    .mapToDouble(d -> (Double) d)
                                    .toArray();
                        }
                    }
                }
            }
        }
        
        return new double[0];
    }
}