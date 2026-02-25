package com.grape.grape.service.ai;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

@Service
public class SparkHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(SparkHttpClient.class);
    private static final String API_URL = "https://spark-api-open.xf-yun.com/v2/chat/completions";
    
    @Value("${xfyun.spark.appid}")
    private String appId;
    
    @Value("${xfyun.spark.api-key}")
    private String apiKey;
    
    @Value("${xfyun.spark.api-secret}")
    private String apiSecret;
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SparkHttpClient() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 三参构造函数（用于兼容现有代码）
    public SparkHttpClient(String appId, String apiKey, String apiSecret) {
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 发送同步请求
    public String sendSyncRequest(String prompt) throws IOException {
        logger.info("=== Spark HTTP Client ===");
        logger.info("AppId: {}", appId);
        logger.info("ApiKey: {}", apiKey);
        logger.info("ApiSecret: {}", apiSecret);
        logger.info("API URL: {}", API_URL);
        logger.info("Prompt length: {}", prompt.length());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "spark-x"); // 指定使用X系列模型
        requestBody.put("user", "test-case-generator");
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 4096);
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        requestBody.put("messages", Collections.singletonList(message));

        // 生成认证头
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        
        // 科大讯飞API认证需要的参数
        long timestamp = System.currentTimeMillis() / 1000;
        String host = "spark-api-open.xf-yun.com";
        String path = "/v2/chat/completions";
        String method = "POST";
        String contentType = "application/json";
        
        // 计算请求体的摘要
        String digest = null;
        String signature = null;
        try {
            digest = calculateDigest(requestBodyStr);
            
            // 构建签名源字符串（严格按照科大讯飞的要求）
            String signatureOrigin = String.format(
                "host:%s\ndate:%d\n%s %s HTTP/1.1\ndigest:%s\ncontent-type:%s",
                host, timestamp, method, path, digest, contentType
            );
            
            logger.info("Signature origin: {}", signatureOrigin);
            
            // 生成HMAC SHA256签名
            signature = calculateHmacSHA256(signatureOrigin, apiSecret);
        } catch (Exception e) {
            logger.error("Error generating signature: {}", e.getMessage(), e);
            throw new IOException("Error generating signature", e);
        }
        
        logger.info("Generated signature: {}", signature);
        
        // 构建Authorization头
        String authorization = String.format(
            "api_key=%s, algorithm=hmac-sha256, headers=host date request-line digest content-type, signature=%s",
            apiKey, signature
        );
        
        logger.info("Generated Authorization header: {}", authorization);
        
        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Host", host)
                .header("Date", String.valueOf(timestamp))
                .header("Content-Type", contentType)
                .header("Digest", digest)
                .header("Authorization", authorization)
                .header("X-Request-ID", java.util.UUID.randomUUID().toString())
                .post(RequestBody.create(
                        requestBodyStr,
                        MediaType.get("application/json")
                ))
                .build();

        logger.info("Sending request to: {}", API_URL);
        logger.info("Request headers: {}", request.headers());
        
        try (Response response = httpClient.newCall(request).execute()) {
            logger.info("Response code: {}", response.code());
            logger.info("Response message: {}", response.message());
            logger.info("Response headers: {}", response.headers());
            
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                logger.error("Error response body: {}", errorBody);
                throw new IOException("Unexpected code: " + response + ", Body: " + errorBody);
            }
            
            String jsonBody = response.body().string();
            logger.info("Response body length: {}", jsonBody.length());
            logger.info("Response body (first 500 chars): {}", jsonBody.substring(0, Math.min(500, jsonBody.length())));
            
            return parseContentResponse(jsonBody);
        } catch (Exception e) {
            logger.error("Error in sendSyncRequest: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 发送带上下文的实体/关系抽取请求
    public List<ExtractedEntity> extractEntities(String text) throws IOException {
        logger.info("=== Spark HTTP Client (Entity Extraction) ===");
        logger.info("AppId: {}", appId);
        logger.info("ApiKey: {}", apiKey);
        logger.info("ApiSecret: {}", apiSecret);
        logger.info("API URL: {}", API_URL);
        logger.info("Text length: {}", text.length());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "spark-x"); // 指定使用X系列模型
        requestBody.put("user", "document-processor");
        requestBody.put("stream", false);
        requestBody.put("max_tokens", 4096);
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", buildPrompt(text)); // 关键！构造精准提示词
        requestBody.put("messages", Collections.singletonList(message));

        // 生成认证头
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        
        // 科大讯飞API认证需要的参数
        long timestamp = System.currentTimeMillis() / 1000;
        String host = "spark-api-open.xf-yun.com";
        String path = "/v2/chat/completions";
        String method = "POST";
        String contentType = "application/json";
        
        // 计算请求体的摘要
        String digest = null;
        String signature = null;
        try {
            digest = calculateDigest(requestBodyStr);
            
            // 构建签名源字符串（严格按照科大讯飞的要求）
            String signatureOrigin = String.format(
                "host:%s\ndate:%d\n%s %s HTTP/1.1\ndigest:%s\ncontent-type:%s",
                host, timestamp, method, path, digest, contentType
            );
            
            logger.info("Signature origin: {}", signatureOrigin);
            
            // 生成HMAC SHA256签名
            signature = calculateHmacSHA256(signatureOrigin, apiSecret);
        } catch (Exception e) {
            logger.error("Error generating signature: {}", e.getMessage(), e);
            throw new IOException("Error generating signature", e);
        }
        
        logger.info("Generated signature: {}", signature);
        
        // 构建Authorization头
        String authorization = String.format(
            "api_key=%s, algorithm=hmac-sha256, headers=host date request-line digest content-type, signature=%s",
            apiKey, signature
        );
        
        logger.info("Generated Authorization header: {}", authorization);
        
        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Host", host)
                .header("Date", String.valueOf(timestamp))
                .header("Content-Type", contentType)
                .header("Digest", digest)
                .header("Authorization", authorization)
                .header("X-Request-ID", java.util.UUID.randomUUID().toString())
                .post(RequestBody.create(
                        requestBodyStr,
                        MediaType.get("application/json")
                ))
                .build();

        logger.info("Sending request to: {}", API_URL);
        logger.info("Request headers: {}", request.headers());
        
        try (Response response = httpClient.newCall(request).execute()) {
            logger.info("Response code: {}", response.code());
            logger.info("Response message: {}", response.message());
            logger.info("Response headers: {}", response.headers());
            
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                logger.error("Error response body: {}", errorBody);
                throw new IOException("Unexpected code: " + response + ", Body: " + errorBody);
            }
            
            String jsonBody = response.body().string();
            logger.info("Response body length: {}", jsonBody.length());
            logger.info("Response body (first 500 chars): {}", jsonBody.substring(0, Math.min(500, jsonBody.length())));
            
            return parseResponse(jsonBody);
        } catch (Exception e) {
            logger.error("Error in extractEntities: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // 计算请求体的摘要
    private String calculateDigest(String requestBody) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(requestBody.getBytes("UTF-8"));
        return "SHA-256=" + java.util.Base64.getEncoder().encodeToString(hash);
    }
    
    // 计算HMAC SHA256签名
    private String calculateHmacSHA256(String data, String key) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacHash = mac.doFinal(data.getBytes("UTF-8"));
        return java.util.Base64.getEncoder().encodeToString(hmacHash);
    }

    private String buildPrompt(String text) {
        return """
            请从以下文本中严格提取所有命名实体及其关系，仅返回JSON数组，不要添加任何解释：
            规则：
            1. 实体类型只能是 [人物PERSON|地点LOCATION|机构ORGANIZATION|时间DATE]
            2. 关系类型只能是 [出生于BORN_AT|位于LOCATED_IN|工作于WORKS_AT|成立于FOUNDED_ON]
            3. 如果找不到明确关系则忽略该实体对
            
            文本内容：
            %s
            
            期望输出格式示例：
            [{"entity1":"张三","type":"PERSON","relation":"WORKS_AT","entity2":"中科院","type":"ORGANIZATION"}]
            """.formatted(text.substring(0, Math.min(text.length(), 3000))); // 截断防超长
    }

    private String parseContentResponse(String jsonBody) throws IOException {
        // 使用泛型类型引用避免类型转换警告
        Map<String, Object> root = objectMapper.readValue(jsonBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        
        // 安全获取 choices 字段
        Object choicesObj = root.get("choices");
        if (!(choicesObj instanceof List)) return "";
        
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) return "";
        
        // 安全获取 message 字段
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map)) return "";
        
        Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
        Object messageObj = choiceMap.get("message");
        if (!(messageObj instanceof Map)) return "";
        
        Map<?, ?> messageMap = (Map<?, ?>) messageObj;
        Object contentObj = messageMap.get("content");
        if (contentObj == null) return "";
        
        return contentObj.toString();
    }

    private List<ExtractedEntity> parseResponse(String jsonBody) throws IOException {
        // 使用泛型类型引用避免类型转换警告
        Map<String, Object> root = objectMapper.readValue(jsonBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        
        // 安全获取 choices 字段
        Object choicesObj = root.get("choices");
        if (!(choicesObj instanceof List)) return Collections.emptyList();
        
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) return Collections.emptyList();
        
        // 安全获取 message 字段
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
        Object messageObj = choiceMap.get("message");
        if (!(messageObj instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> messageMap = (Map<?, ?>) messageObj;
        Object contentObj = messageMap.get("content");
        if (contentObj == null) return Collections.emptyList();
        
        String content = contentObj.toString();
        // 使用更简洁的类型引用方式
        return objectMapper.readValue(content, new com.fasterxml.jackson.core.type.TypeReference<List<ExtractedEntity>>() {});
    }

    // 定义抽取结果实体类
    public static class ExtractedEntity {
        public String entity1;
        public String type1;
        public String relation;
        public String entity2;
        public String type2;
        // Getters & Setters...
    }
}