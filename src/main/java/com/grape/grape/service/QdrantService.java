package com.grape.grape.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Qdrant服务类
 * 用于通过HTTP API与Qdrant向量数据库交互
 */
@Service
public class QdrantService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    /**
     * 构造函数
     * @param host Qdrant主机地址
     * @param port Qdrant端口
     * @param apiKey Qdrant API密钥（可选）
     */
    public QdrantService(
            @Value("${qdrant.host}") String host,
            @Value("${qdrant.port}") int port,
            @Value("${qdrant.api-key:}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = "http://" + host + ":" + port;
        this.apiKey = apiKey;
    }

    /**
     * 创建HTTP请求头
     * @return HttpHeaders对象
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.set("api-key", apiKey);
        }
        return headers;
    }

    /**
     * 健康检查
     * @return 健康状态
     */
    public boolean healthCheck() {
        try {
            String url = baseUrl + "/health";
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // 健康检查失败，返回 false
            return false;
        }
    }

    /**
     * 创建集合
     * @param collectionName 集合名称
     * @param vectorSize 向量维度
     * @return 是否创建成功
     */
    public boolean createCollection(String collectionName, int vectorSize) {
        try {
            String url = baseUrl + "/collections/" + collectionName;
            
            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                    "vectors", Map.of(
                            "size", vectorSize,
                            "distance", "Cosine"
                    )
            );
            
            HttpEntity<?> entity = new HttpEntity<>(requestBody, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // 创建集合失败，返回 false
            return false;
        }
    }

    /**
     * 插入向量点
     * @param collectionName 集合名称
     * @param points 向量点数据
     * @return 是否插入成功
     */
    public boolean upsertPoints(String collectionName, Object points) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points";
            HttpEntity<?> entity = new HttpEntity<>(points, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // 插入失败，返回 false
            return false;
        }
    }

    /**
     * 搜索向量点
     * @param collectionName 集合名称
     * @param searchRequest 搜索请求
     * @return 搜索结果
     */
    public String searchPoints(String collectionName, Object searchRequest) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points/search";
            HttpEntity<?> entity = new HttpEntity<>(searchRequest, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            // 搜索失败，返回 null
            return null;
        }
    }

    /**
     * 获取集合信息
     * @param collectionName 集合名称
     * @return 集合信息
     */
    public String getCollectionInfo(String collectionName) {
        try {
            String url = baseUrl + "/collections/" + collectionName;
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            // 集合不存在或获取失败，返回 null
            return null;
        }
    }

    /**
     * 删除集合
     * @param collectionName 集合名称
     * @return 是否删除成功
     */
    public boolean deleteCollection(String collectionName) {
        try {
            String url = baseUrl + "/collections/" + collectionName;
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // 删除失败，返回 false
            return false;
        }
    }
    
    /**
     * 删除指定点
     * @param collectionName 集合名称
     * @param pointId 点ID
     * @return 是否删除成功
     */
    public boolean deletePoint(String collectionName, Integer pointId) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/points/" + pointId;
            HttpEntity<?> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // 删除失败，返回 false
            return false;
        }
    }
}
