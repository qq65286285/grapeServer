package com.grape.grape.service.ai.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.net.URLEncoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigVectorUtil {
    
    @Value("${AI_API_HOST:emb-cn-huabei-1.xf-yun.com}")
    private String host;
    
    @Value("${xfyun.spark-emb.appid:}")
    private String appId;
    
    @Value("${xfyun.spark-emb.api-key:}")
    private String apiKey;
    
    @Value("${xfyun.spark-emb.api-secret:}")
    private String apiSecret;
    
    @Value("${xfyun.spark-emb.vector-url:http://emb-cn-huabei-1.xf-yun.com/}")
    private String vectorUrl;
    
    /**
     * 生成配置文件相关的向量字符串
     * @param path 请求路径
     * @param date 日期字符串
     * @return 拼接后的字符串
     */
    private String generateVectorString(String path, String date) {
        // 拼接生成字符串tmp
        StringBuilder tmp = new StringBuilder();
        tmp.append("host: " + host + "\n");
        tmp.append("date: " + date + "\n");
        tmp.append("POST " + path + " HTTP/1.1");
        
        return tmp.toString();
    }
    
    /**
     * 生成配置文件相关的向量字符串
     * @param path 请求路径
     * @return 拼接后的字符串
     */
    private String generateVectorString(String path) {
        // 生成date
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date());
        
        return generateVectorString(path, date);
    }
    
   
    
    /**
     * 使用 HMAC-SHA256 算法结合 APISecret 对 tmp 字符串进行签名
     * @param tmp 待签名的字符串
     * @param apiSecret API密钥
     * @return 签名后的字节数组
     * @throws NoSuchAlgorithmException 如果算法不存在
     * @throws InvalidKeyException 如果密钥无效
     */
    private byte[] generateHmacSha256Signature(String tmp, String apiSecret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(tmp.getBytes(StandardCharsets.UTF_8));
    }

    
    /**
     * 将签名后的字节数组进行 base64 编码生成 signature
     * @param tmpSha 签名后的字节数组
     * @return base64 编码后的字符串
     */
    private static String generateBase64Signature(byte[] tmpSha) {
        return Base64.getEncoder().encodeToString(tmpSha);
    }
    
    /**
     * 生成 authorization_origin 字符串
     * @param apiKey API密钥
     * @param signature 签名
     * @return authorization_origin 字符串
     */
    private static String generateAuthorizationOrigin(String apiKey, String signature) {
        return String.format(
            "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
            apiKey, signature
        );
    }
    
    /**
     * 对 authorization_origin 字符串进行 base64 编码生成 authorization
     * @param authorizationOrigin authorization_origin 字符串
     * @return base64 编码后的 authorization 字符串
     */
    private static String generateAuthorization(String authorizationOrigin) {
        return Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成完整的认证 URL
     * @param path 请求路径
     * @return 完整的认证 URL
     * @throws NoSuchAlgorithmException 如果算法不存在
     * @throws InvalidKeyException 如果密钥无效
     */
    public String generateAuthUrl(String path) throws NoSuchAlgorithmException, InvalidKeyException {
        // 生成向量字符串并保存 date 值
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date());
        
        // 生成向量字符串
        String vectorString = generateVectorString(path, date);
        
        // 使用 HMAC-SHA256 算法签名
        byte[] signatureBytes = generateHmacSha256Signature(vectorString, apiSecret);
        
        // 对签名进行 base64 编码
        String signature = generateBase64Signature(signatureBytes);
        
        // 生成 authorization_origin 字符串
        String authorizationOrigin = generateAuthorizationOrigin(apiKey, signature);
        
        // 对 authorization_origin 进行 base64 编码生成 authorization
        String authorization = generateAuthorization(authorizationOrigin);
        
        // 组装 URL 参数
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(vectorUrl.endsWith("/") ? vectorUrl.substring(0, vectorUrl.length() - 1) : vectorUrl).append(path).append("?");
        urlBuilder.append("authorization=").append(URLEncoder.encode(authorization, StandardCharsets.UTF_8));
        urlBuilder.append("&date=").append(URLEncoder.encode(date, StandardCharsets.UTF_8));
        urlBuilder.append("&host=").append(URLEncoder.encode(host, StandardCharsets.UTF_8));
        
        return urlBuilder.toString();
    }
}
