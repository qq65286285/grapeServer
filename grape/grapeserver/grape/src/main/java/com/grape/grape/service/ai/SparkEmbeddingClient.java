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
public class SparkEmbeddingClient {
    private static final Logger logger = LoggerFactory.getLogger(SparkEmbeddingClient.class);
    private static final String API_URL = "https://spark-api-open.xf-yun.com/v2/embeddings";
    
    @Value("${xfyun.spark-emb.appid}")
    private String appId;
    
    @Value("${xfyun.spark-emb.api-key}")
    private String apiKey;
    
    @Value("${xfyun.spark-emb.api-secret}")
    private String apiSecret;
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    // 无参构造函数（用于Spring依赖注入）
    public SparkEmbeddingClient() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 三参构造函数（用于兼容现有代码）
    public SparkEmbeddingClient(String appId, String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 获取文本的向量表示
    public List<Double> getEmbedding(String text) throws IOException {
        logger.info("=== Spark Embedding Client ===");
        logger.info("AppId: {}", appId);
        logger.info("ApiKey: {}", apiKey);
        logger.info("ApiSecret: {}", apiSecret);
        logger.info("API URL: {}", API_URL);
        logger.info("Text length: {}", text.length());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-embedding-3-small"); // 使用文本嵌入模型
        requestBody.put("input", text);
        requestBody.put("encoding_format", "float");

        // 生成认证头
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        
        // 科大讯飞API认证需要的参数
        long timestamp = System.currentTimeMillis() / 1000;
        String host = "spark-api-open.xf-yun.com";
        String path = "/v2/embeddings";
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
        
        return sendRequest(request);
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
    
    // 发送请求并处理响应
    private List<Double> sendRequest(Request request) throws IOException {
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
            
            return parseEmbeddingResponse(jsonBody);
        } catch (Exception e) {
            logger.error("Error in sendRequest: {}", e.getMessage(), e);
            throw e;
        }
    }
    

    
    // 计算请求体的摘要
    private String getDigest(String requestBody) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(requestBody.getBytes("UTF-8"));
        return java.util.Base64.getEncoder().encodeToString(hash);
    }
    
    // 生成HMAC SHA256签名
    private String hmacSHA256(String data, String key) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
        return java.util.Base64.getEncoder().encodeToString(hash);
    }

    // 生成文档向量（用于兼容现有代码）
    public float[] getEmbeddingPara(String text) throws IOException {
        List<Double> embedding = getEmbedding(text);
        float[] vector = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            vector[i] = embedding.get(i).floatValue();
        }
        return vector;
    }

    // 生成查询向量（用于兼容现有代码）
    public float[] getEmbeddingQuery(String text) throws IOException {
        return getEmbeddingPara(text);
    }

    private List<Double> parseEmbeddingResponse(String jsonBody) throws IOException {
        // 使用泛型类型引用避免类型转换警告
        Map<String, Object> root = objectMapper.readValue(jsonBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        
        // 安全获取 data 字段
        Object dataObj = root.get("data");
        if (!(dataObj instanceof List)) return Collections.emptyList();
        
        List<?> data = (List<?>) dataObj;
        if (data.isEmpty()) return Collections.emptyList();
        
        // 安全获取 embedding 字段
        Object firstItem = data.get(0);
        if (!(firstItem instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> itemMap = (Map<?, ?>) firstItem;
        Object embeddingObj = itemMap.get("embedding");
        if (!(embeddingObj instanceof List)) return Collections.emptyList();
        
        List<?> embeddingList = (List<?>) embeddingObj;
        List<Double> embedding = new ArrayList<>();
        for (Object obj : embeddingList) {
            if (obj instanceof Number) {
                embedding.add(((Number) obj).doubleValue());
            }
        }
        
        return embedding;
    }
}