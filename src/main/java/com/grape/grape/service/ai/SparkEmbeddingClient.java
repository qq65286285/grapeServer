package com.grape.grape.service.ai;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.grape.grape.service.ai.config.ConfigVectorUtil;

@Service
public class SparkEmbeddingClient {
    private static final Logger logger = LoggerFactory.getLogger(SparkEmbeddingClient.class);
    private static final String API_URL = "https://emb-cn-huabei-1.xf-yun.com/";
    
    @Value("${xfyun.spark-emb.appid}")
    private String appId;
    
    @Value("${xfyun.spark-emb.api-key}")
    private String apiKey;
    
    @Value("${xfyun.spark-emb.api-secret}")
    private String apiSecret;
    
    @Autowired
    ConfigVectorUtil configVectorUtil;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    // 无参构造函数（用于Spring依赖注入）
    public SparkEmbeddingClient() {
        this.configVectorUtil = null;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 构造函数（用于Spring依赖注入）
    public SparkEmbeddingClient(ConfigVectorUtil configVectorUtil) {
        this.configVectorUtil = configVectorUtil;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 四参构造函数（用于兼容现有代码）
    public SparkEmbeddingClient(String appId, String apiKey, String apiSecret, ConfigVectorUtil configVectorUtil) {
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.configVectorUtil = configVectorUtil;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 三参构造函数（用于兼容现有代码）
    public SparkEmbeddingClient(String appId, String apiKey, String apiSecret) {
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.configVectorUtil = null;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // 获取文本的向量表示
    public List<Double> getEmbedding(String text) throws Exception {
        
        // 检查 configVectorUtil 是否初始化
        if (configVectorUtil == null) {
            throw new IllegalStateException("ConfigVectorUtil not initialized. Please use the constructor with ConfigVectorUtil parameter.");
        }
        
        // 使用 ConfigVectorUtil 生成认证 URL
        String path = "/";
        String authUrl = configVectorUtil.generateAuthUrl(path);
        logger.info("Auth URL: {}", authUrl);
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        
        // 构建header
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", appId);
        header.put("uid", String.valueOf(System.nanoTime()));
        header.put("status", 3);
        requestBody.put("header", header);
        
        // 构建parameter
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> emb = new HashMap<>();
        emb.put("domain", "para"); // 使用para模式进行向量化
        Map<String, Object> feature = new HashMap<>();
        feature.put("encoding", "utf8");
        emb.put("feature", feature);
        parameter.put("emb", emb);
        requestBody.put("parameter", parameter);
        
        // 构建payload
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> messages = new HashMap<>();
        // 构建与参考文件一致的消息格式
        try {
            // 构建消息对象，与参考文件保持一致
            Map<String, Object> messageObj = new HashMap<>();
            Map<String, Object> message = new HashMap<>();
            message.put("content", text);
            message.put("role", "user");
            messageObj.put("messages", java.util.Collections.singletonList(message));
            
            // 将消息对象转换为JSON字符串，然后进行base64编码
            String textJson = objectMapper.writeValueAsString(messageObj);
            String encodedText = java.util.Base64.getEncoder().encodeToString(textJson.getBytes("UTF-8"));
            messages.put("text", encodedText);
        } catch (Exception e) {
            logger.error("Error encoding text: {}", e.getMessage(), e);
            throw new IOException("Error encoding text", e);
        }
        payload.put("messages", messages);
        requestBody.put("payload", payload);

        // 生成请求体字符串
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        
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
        
        return sendRequest(request);
    }
    
    // 发送请求并处理响应
    private List<Double> sendRequest(Request request) throws IOException {
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
            
            return parseEmbeddingResponse(responseBody);
        } catch (Exception e) {
            logger.error("Error in sendRequest: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 生成文档向量（用于兼容现有代码）
    public float[] getEmbeddingPara(String text) throws Exception {
        List<Double> embedding = getEmbedding(text);
        float[] vector = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            vector[i] = embedding.get(i).floatValue();
        }
        return vector;
    }

    // 生成查询向量（用于兼容现有代码）
    public float[] getEmbeddingQuery(String text) throws Exception {
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