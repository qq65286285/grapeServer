package com.grape.grape.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Qdrant服务测试类
 * 用于测试QdrantService的实际调用功能
 */
@SpringBootTest
public class QdrantServiceTest {

    @Autowired
    private QdrantService qdrantService;

    private static final String COLLECTION_NAME = "test_collection";

    /**
     * 测试健康检查
     * 验证Qdrant服务是否可用
     */
    @Test
    public void testHealthCheck() {
        System.out.println("=== 测试Qdrant健康检查 ===");
        try {
            boolean result = qdrantService.healthCheck();
            if (result) {
                System.out.println("✅ Qdrant服务健康检查成功");
            } else {
                System.out.println("❌ Qdrant服务健康检查失败");
            }
        } catch (Exception e) {
            System.out.println("❌ Qdrant服务健康检查失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 健康检查测试完成 ===\n");
    }

    /**
     * 测试创建集合
     * 创建一个新的向量集合
     */
    @Test
    public void testCreateCollection() {
        System.out.println("=== 测试创建Qdrant集合 ===");
        try {
            boolean result = qdrantService.createCollection(COLLECTION_NAME, 4);
            if (result) {
                System.out.println("✅ 成功创建集合: " + COLLECTION_NAME);
            } else {
                System.out.println("❌ 创建集合失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 创建集合失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 创建集合测试完成 ===\n");
    }

    /**
     * 测试插入点
     * 向集合中插入向量点
     */
    @Test
    public void testUpsertPoints() {
        System.out.println("=== 测试插入Qdrant向量点 ===");
        try {
            // 准备向量点
            List<Map<String, Object>> points = new ArrayList<>();
            
            // 第一个点
            Map<String, Object> point1 = new HashMap<>();
            point1.put("id", 1);
            point1.put("vector", new double[]{0.1, 0.2, 0.3, 0.4});
            Map<String, Object> payload1 = new HashMap<>();
            payload1.put("name", "测试点1");
            point1.put("payload", payload1);
            points.add(point1);
            
            // 第二个点
            Map<String, Object> point2 = new HashMap<>();
            point2.put("id", 2);
            point2.put("vector", new double[]{0.5, 0.6, 0.7, 0.8});
            Map<String, Object> payload2 = new HashMap<>();
            payload2.put("name", "测试点2");
            point2.put("payload", payload2);
            points.add(point2);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("points", points);
            
            boolean result = qdrantService.upsertPoints(COLLECTION_NAME, requestBody);
            if (result) {
                System.out.println("✅ 成功插入2个向量点");
            } else {
                System.out.println("❌ 插入向量点失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 插入向量点失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 插入向量点测试完成 ===\n");
    }

    /**
     * 测试搜索点
     * 在集合中搜索相似的向量点
     */
    @Test
    public void testSearchPoints() {
        System.out.println("=== 测试搜索Qdrant向量点 ===");
        try {
            // 构建搜索请求
            Map<String, Object> searchRequest = new HashMap<>();
            searchRequest.put("vector", new double[]{0.15, 0.25, 0.35, 0.45});
            searchRequest.put("limit", 2);
            searchRequest.put("with_payload", true);
            
            String result = qdrantService.searchPoints(COLLECTION_NAME, searchRequest);
            if (result != null) {
                System.out.println("✅ 搜索完成，结果:");
                System.out.println(result);
            } else {
                System.out.println("❌ 搜索向量点失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 搜索向量点失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 搜索向量点测试完成 ===\n");
    }

    /**
     * 测试获取集合信息
     * 获取集合的详细信息
     */
    @Test
    public void testCollectionInfo() {
        System.out.println("=== 测试获取Qdrant集合信息 ===");
        try {
            String result = qdrantService.getCollectionInfo(COLLECTION_NAME);
            if (result != null) {
                System.out.println("✅ 成功获取集合信息: " + COLLECTION_NAME);
                System.out.println(result);
            } else {
                System.out.println("❌ 获取集合信息失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 获取集合信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 获取集合信息测试完成 ===\n");
    }

    /**
     * 测试删除集合
     * 删除测试用的集合
     */
    @Test
    public void testDeleteCollection() {
        System.out.println("=== 测试删除Qdrant集合 ===");
        try {
            boolean result = qdrantService.deleteCollection(COLLECTION_NAME);
            if (result) {
                System.out.println("✅ 成功删除集合: " + COLLECTION_NAME);
            } else {
                System.out.println("❌ 删除集合失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 删除集合失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== 删除集合测试完成 ===\n");
    }
}
