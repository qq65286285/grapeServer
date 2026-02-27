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
    @Value("${xfyun.spark.host-url:https://spark-api.xf-yun.com/v1/x1}")
    private String apiUrl;
    
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
        
        Map<String, Object> requestBody = new HashMap<>();
        
        // 构建header
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", appId);
        header.put("uid", String.valueOf(System.nanoTime()));
        header.put("status", 3);
        requestBody.put("header", header);
        
        // 构建parameter
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> chat = new HashMap<>();
        chat.put("domain", "spark-x");
        chat.put("temperature", 0.7);
        chat.put("max_tokens", 4096);
        parameter.put("chat", chat);
        requestBody.put("parameter", parameter);
        
        // 构建payload
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        payload.put("message", message);
        requestBody.put("payload", payload);

        // 生成请求体字符串
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        
        // 打印完整的请求体
        logger.info("Request body: {}", requestBodyStr);
        
        // 构建认证URL
        String authUrl = null;
        try {
            // 解析API URL获取host和path
            java.net.URL url = new java.net.URL(apiUrl);
            String host = url.getHost();
            String path = url.getPath();
            
            // 生成date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            String date = sdf.format(new java.util.Date());
            
            // 生成向量字符串
            String vectorString = String.format("host: %s\ndate: %s\nPOST %s HTTP/1.1", host, date, path);
            
            // 使用 HMAC-SHA256 算法签名
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(apiSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(vectorString.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // 对签名进行 base64 编码
            String signature = java.util.Base64.getEncoder().encodeToString(signatureBytes);
            
            // 生成 authorization_origin 字符串
            String authorizationOrigin = String.format(
                "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                apiKey, signature
            );
            
            // 对 authorization_origin 进行 base64 编码生成 authorization
            String authorization = java.util.Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // 组装 URL 参数
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(apiUrl).append("?");
            urlBuilder.append("authorization=").append(java.net.URLEncoder.encode(authorization, "UTF-8"));
            urlBuilder.append("&date=").append(java.net.URLEncoder.encode(date, "UTF-8"));
            urlBuilder.append("&host=").append(java.net.URLEncoder.encode(host, "UTF-8"));
            
            authUrl = urlBuilder.toString();
        } catch (Exception e) {
            logger.error("Error generating auth URL: {}", e.getMessage(), e);
            throw new IOException("Error generating auth URL", e);
        }
        
        logger.info("Auth URL: {}", authUrl);
        
        // 构建请求
        Request request = new Request.Builder()
                .url(authUrl)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(
                        requestBodyStr,
                        MediaType.get("application/json")
                ))
                .build();

        // 输出请求日志
        logger.info("**********************");
        logger.info("请求url：{}", authUrl);
        logger.info("请求header：{}", request.headers());
        logger.info("请求入参：{}", requestBodyStr);
        
        try (Response response = httpClient.newCall(request).execute()) {
            int responseCode = response.code();
            String responseBody = response.body() != null ? response.body().string() : "No response body";
            
            // 输出响应日志
            logger.info("请求结果：code = {}", responseCode);
            logger.info("请求返回：{}", responseBody);
            logger.info("*******************");
            
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response + ", Body: " + responseBody);
            }
            
            return parseContentResponse(responseBody);
        } catch (Exception e) {
            logger.error("Error in sendSyncRequest: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 发送带上下文的实体/关系抽取请求
    public List<ExtractedEntity> extractEntities(String text) throws IOException {
        
        Map<String, Object> requestBody = new HashMap<>();
        
        // 构建header
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", appId);
        header.put("uid", String.valueOf(System.nanoTime()));
        header.put("status", 3);
        requestBody.put("header", header);
        
        // 构建parameter
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> chat = new HashMap<>();
        chat.put("domain", "spark-x");
        chat.put("temperature", 0.7);
        chat.put("max_tokens", 4096);
        parameter.put("chat", chat);
        requestBody.put("parameter", parameter);
        
        // 构建payload
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", buildPrompt(text)); // 关键！构造精准提示词
        payload.put("message", message);
        requestBody.put("payload", payload);

        // 生成请求体字符串
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        
        // 打印完整的请求体
        logger.info("Request body: {}", requestBodyStr);
        
        // 构建认证URL
        String authUrl = null;
        try {
            // 解析API URL获取host和path
            java.net.URL url = new java.net.URL(apiUrl);
            String host = url.getHost();
            String path = url.getPath();
            
            // 生成date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            String date = sdf.format(new java.util.Date());
            
            // 生成向量字符串
            String vectorString = String.format("host: %s\ndate: %s\nPOST %s HTTP/1.1", host, date, path);
            
            // 使用 HMAC-SHA256 算法签名
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(apiSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(vectorString.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // 对签名进行 base64 编码
            String signature = java.util.Base64.getEncoder().encodeToString(signatureBytes);
            
            // 生成 authorization_origin 字符串
            String authorizationOrigin = String.format(
                "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                apiKey, signature
            );
            
            // 对 authorization_origin 进行 base64 编码生成 authorization
            String authorization = java.util.Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            // 组装 URL 参数
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(apiUrl).append("?");
            urlBuilder.append("authorization=").append(java.net.URLEncoder.encode(authorization, "UTF-8"));
            urlBuilder.append("&date=").append(java.net.URLEncoder.encode(date, "UTF-8"));
            urlBuilder.append("&host=").append(java.net.URLEncoder.encode(host, "UTF-8"));
            
            authUrl = urlBuilder.toString();
        } catch (Exception e) {
            logger.error("Error generating auth URL: {}", e.getMessage(), e);
            throw new IOException("Error generating auth URL", e);
        }
        
        logger.info("Auth URL: {}", authUrl);
        
        // 构建请求
        Request request = new Request.Builder()
                .url(authUrl)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(
                        requestBodyStr,
                        MediaType.get("application/json")
                ))
                .build();

        // 输出请求日志
        logger.info("**********************");
        logger.info("请求url：{}", authUrl);
        logger.info("请求header：{}", request.headers());
        logger.info("请求入参：{}", requestBodyStr);
        
        try (Response response = httpClient.newCall(request).execute()) {
            int responseCode = response.code();
            String responseBody = response.body() != null ? response.body().string() : "No response body";
            
            // 输出响应日志
            logger.info("请求结果：code = {}", responseCode);
            logger.info("请求返回：{}", responseBody);
            logger.info("*******************");
            
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response + ", Body: " + responseBody);
            }
            
            return parseResponse(responseBody);
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