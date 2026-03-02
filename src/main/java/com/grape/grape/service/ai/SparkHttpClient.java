package com.grape.grape.service.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SparkHttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkHttpClient.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String TIMEZONE = "GMT";
    private static final String ALGORITHM = "hmac-sha256";
    private static final String HEADERS = "host date request-line";
    private static final String ENCODING = "UTF-8";
    private static final double TEMPERATURE = 0.7;
    private static final int MAX_TOKENS = 4096;
    private static final int PROMPT_MAX_LENGTH = 3000;
    
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

    public SparkHttpClient(String appId, String apiKey, String apiSecret) {
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String sendSyncRequest(String prompt) throws IOException {
        Map<String, Object> requestBody = buildRequestBody(prompt);
        return executeRequest(requestBody);
    }

    public List<ExtractedEntity> extractEntities(String text) throws IOException {
        Map<String, Object> requestBody = buildRequestBody(buildPrompt(text));
        String responseBody = executeRequest(requestBody);
        return parseExtractedEntitiesResponse(responseBody);
    }

    private Map<String, Object> buildRequestBody(String content) {
        Map<String, Object> requestBody = new HashMap<>();
        
        Map<String, Object> header = buildHeader();
        requestBody.put("header", header);
        
        Map<String, Object> parameter = buildParameter();
        requestBody.put("parameter", parameter);
        
        Map<String, Object> payload = buildPayload(content);
        requestBody.put("payload", payload);

        return requestBody;
    }

    private Map<String, Object> buildHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("app_id", appId);
        header.put("uid", String.valueOf(System.nanoTime()));
        header.put("status", 3);
        return header;
    }

    private Map<String, Object> buildParameter() {
        Map<String, Object> parameter = new HashMap<>();
        Map<String, Object> chat = new HashMap<>();
        chat.put("domain", "spark-x");
        chat.put("temperature", TEMPERATURE);
        chat.put("max_tokens", MAX_TOKENS);
        parameter.put("chat", chat);
        return parameter;
    }

    private Map<String, Object> buildPayload(String content) {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", content);
        payload.put("message", message);
        return payload;
    }

    private String executeRequest(Map<String, Object> requestBody) throws IOException {
        String requestBodyStr = objectMapper.writeValueAsString(requestBody);
        logger.info("Request body: {}", requestBodyStr);
        
        String authUrl = generateAuthUrl();
        logger.info("Auth URL: {}", authUrl);
        
        Request request = buildRequest(authUrl, requestBodyStr);
        logRequestDetails(request, requestBodyStr);
        
        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response);
        } catch (Exception e) {
            logger.error("Error executing request: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String generateAuthUrl() throws IOException {
        try {
            URL url = new URL(apiUrl);
            String host = url.getHost();
            String path = url.getPath();
            String date = generateDate();
            
            String signature = generateSignature(host, date, path);
            String authorization = buildAuthorization(signature);
            
            return buildAuthUrl(authorization, date, host);
        } catch (Exception e) {
            logger.error("Error generating auth URL: {}", e.getMessage(), e);
            throw new IOException("Error generating auth URL", e);
        }
    }

    private String generateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        return sdf.format(new Date());
    }

    private String generateSignature(String host, String date, String path) throws Exception {
        String vectorString = String.format("host: %s\ndate: %s\nPOST %s HTTP/1.1", host, date, path);
        
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(ENCODING), HMAC_SHA256);
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(vectorString.getBytes(ENCODING));
        
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    private String buildAuthorization(String signature) {
        String authorizationOrigin = String.format(
            "api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
            apiKey, ALGORITHM, HEADERS, signature
        );
        return Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(ENCODING));
    }

    private String buildAuthUrl(String authorization, String date, String host) throws Exception {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(apiUrl).append("?");
        urlBuilder.append("authorization=").append(java.net.URLEncoder.encode(authorization, ENCODING));
        urlBuilder.append("&date=").append(java.net.URLEncoder.encode(date, ENCODING));
        urlBuilder.append("&host=").append(java.net.URLEncoder.encode(host, ENCODING));
        return urlBuilder.toString();
    }

    private Request buildRequest(String authUrl, String requestBodyStr) {
        return new Request.Builder()
            .url(authUrl)
            .header("Content-Type", "application/json")
            .post(RequestBody.create(requestBodyStr, MediaType.get("application/json")))
            .build();
    }

    private void logRequestDetails(Request request, String requestBodyStr) {
        logger.info("**********************");
        logger.info("请求url：{}", request.url());
        logger.info("请求header：{}", request.headers());
        logger.info("请求入参：{}", requestBodyStr);
    }

    private String handleResponse(Response response) throws IOException {
        int responseCode = response.code();
        String responseBody = response.body() != null ? response.body().string() : "No response body";
        
        logger.info("请求结果：code = {}", responseCode);
        logger.info("请求返回：{}", responseBody);
        logger.info("*******************");
        
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code: " + response + ", Body: " + responseBody);
        }
        
        return parseContentResponse(responseBody);
    }

    private String buildPrompt(String text) {
        String truncatedText = text.substring(0, Math.min(text.length(), PROMPT_MAX_LENGTH));
        return String.format("""
            请从以下文本中严格提取所有命名实体及其关系，仅返回JSON数组，不要添加任何解释：
            规则：
            1. 实体类型只能是 [人物PERSON|地点LOCATION|机构ORGANIZATION|时间DATE]
            2. 关系类型只能是 [出生于BORN_AT|位于LOCATED_IN|工作于WORKS_AT|成立于FOUNDED_ON]
            3. 如果找不到明确关系则忽略该实体对
            
            文本内容：
            %s
            
            期望输出格式示例：
            [{"entity1":"张三","type":"PERSON","relation":"WORKS_AT","entity2":"中科院","type":"ORGANIZATION"}]
            """, truncatedText);
    }

    private String parseContentResponse(String jsonBody) throws IOException {
        Map<String, Object> root = objectMapper.readValue(jsonBody, new TypeReference<Map<String, Object>>() {});
        
        Object choicesObj = root.get("choices");
        if (!(choicesObj instanceof List)) return "";
        
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) return "";
        
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

    private List<ExtractedEntity> parseExtractedEntitiesResponse(String jsonBody) throws IOException {
        Map<String, Object> root = objectMapper.readValue(jsonBody, new TypeReference<Map<String, Object>>() {});
        
        Object choicesObj = root.get("choices");
        if (!(choicesObj instanceof List)) return Collections.emptyList();
        
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) return Collections.emptyList();
        
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
        Object messageObj = choiceMap.get("message");
        if (!(messageObj instanceof Map)) return Collections.emptyList();
        
        Map<?, ?> messageMap = (Map<?, ?>) messageObj;
        Object contentObj = messageMap.get("content");
        if (contentObj == null) return Collections.emptyList();
        
        String content = contentObj.toString();
        return objectMapper.readValue(content, new TypeReference<List<ExtractedEntity>>() {});
    }

    public static class ExtractedEntity {
        public String entity1;
        public String type1;
        public String relation;
        public String entity2;
        public String type2;
        
        public String getEntity1() {
            return entity1;
        }
        
        public void setEntity1(String entity1) {
            this.entity1 = entity1;
        }
        
        public String getType1() {
            return type1;
        }
        
        public void setType1(String type1) {
            this.type1 = type1;
        }
        
        public String getRelation() {
            return relation;
        }
        
        public void setRelation(String relation) {
            this.relation = relation;
        }
        
        public String getEntity2() {
            return entity2;
        }
        
        public void setEntity2(String entity2) {
            this.entity2 = entity2;
        }
        
        public String getType2() {
            return type2;
        }
        
        public void setType2(String type2) {
            this.type2 = type2;
        }
    }
}
