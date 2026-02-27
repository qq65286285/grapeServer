package com.grape.grape.service.ai;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SparkEmbeddingClientTest {

    private static final String API_KEY = "fea2b3c358dfea4e8754cb3730a42058";
    private static final String API_SECRET = "NGRjODRhZmE1OTcyY2VlNWM5OTVkYzRk";
    private static final String API_URL = "https://emb-cn-huabei-1.xf-yun.com/";

    public static void main(String[] args) {
        try {
            testSignatureGeneration();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void testSignatureGeneration() throws Exception {
        System.out.println("=== 测试 Spark Embedding Client 签名生成 ===");
        System.out.println("API Key: " + API_KEY);
        System.out.println("API Secret: " + API_SECRET);
        System.out.println("API URL: " + API_URL);

        // 生成符合RFC1123格式的日期
        ZonedDateTime zdt = ZonedDateTime.now(java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US);
        String date = zdt.format(formatter).replace("Z", "GMT");
        System.out.println("Generated date: " + date);

        // 解析URL获取host和path
        int stidx = API_URL.indexOf("://");
        String hostPart = API_URL.substring(stidx + 3);
        int edidx = hostPart.indexOf("/");
        String host = hostPart.substring(0, edidx);
        String path = hostPart.substring(edidx);
        System.out.println("Host: " + host);
        System.out.println("Path: " + path);

        // 构建签名源字符串
        String signatureOrigin = String.format("host: %s\ndate: %s\nPOST %s HTTP/1.1", host, date, path);
        System.out.println("Signature origin:");
        System.out.println(signatureOrigin);

        // 生成HMAC SHA256签名
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(API_SECRET.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacHash = mac.doFinal(signatureOrigin.getBytes("UTF-8"));
        String signature = java.util.Base64.getEncoder().encodeToString(hmacHash);
        System.out.println("Generated signature: " + signature);
        System.out.println("Signature length: " + signature.length());
        System.out.println("Expected signature length: 44");
        System.out.println("Signature length is correct: " + (signature.length() == 44));

        // 构建Authorization头
        String authorizationOrigin = String.format("api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"", API_KEY, signature);
        String authorization = java.util.Base64.getEncoder().encodeToString(authorizationOrigin.getBytes("UTF-8"));
        System.out.println("Generated authorization: " + authorization);
        System.out.println("Authorization length: " + authorization.length());

        // 构建查询参数
        String queryString = String.format("host=%s&date=%s&authorization=%s",
                java.net.URLEncoder.encode(host, "UTF-8"),
                java.net.URLEncoder.encode(date, "UTF-8"),
                java.net.URLEncoder.encode(authorization, "UTF-8"));

        String authUrl = API_URL + "?" + queryString;
        System.out.println("Auth URL length: " + authUrl.length());
        System.out.println("Auth URL (first 200 chars): " + authUrl.substring(0, Math.min(200, authUrl.length())) + "...");

        System.out.println("\n=== 测试完成 ===");
    }
}
