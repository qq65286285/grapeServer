package com.grape.grape.service.ai;

import com.grape.grape.service.QdrantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Qdrant测试类
 * 用于测试Qdrant服务的实际调用功能
 */
@SpringBootTest
public class QdrantTest {

    @Autowired
    private QdrantService qdrantService;

    private static final String COLLECTION_NAME_1 = "test_collection";
    private static final String COLLECTION_NAME_2 = "test_collection_2";

    /**
     * 测试创建集合
     * 创建一个新的向量集合
     */
    @Test
    public void testCreateCollection() {
        System.out.println("=== 测试创建Qdrant集合 ===");
        try {
            // 创建集合
            boolean success = qdrantService.createCollection(COLLECTION_NAME_2, 4);
            if (success) {
                System.out.println("✅ 成功创建集合: " + COLLECTION_NAME_2);
            } else {
                System.out.println("❌ 创建集合失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 创建集合失败: " + e.getMessage());
            // 忽略已存在的异常，继续测试
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
            // 准备向量点数据
            List<Map<String, Object>> points = new ArrayList<>();
            
            // 第一个点
            Map<String, Object> point1 = new HashMap<>();
            point1.put("id", 1);
            
            List<Float> vector1 = List.of(0.1f, 0.2f, 0.3f, 0.4f);
            point1.put("vector", vector1);
            
            Map<String, Object> payload1 = new HashMap<>();
            payload1.put("name", "测试点1");
            point1.put("payload", payload1);
            points.add(point1);
            
            // 第二个点
            Map<String, Object> point2 = new HashMap<>();
            point2.put("id", 2);
            
            List<Float> vector2 = List.of(0.5f, 0.6f, 0.7f, 0.8f);
            point2.put("vector", vector2);
            
            Map<String, Object> payload2 = new HashMap<>();
            payload2.put("name", "测试点2");
            point2.put("payload", payload2);
            points.add(point2);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("points", points);
            
            // 插入点
            boolean success = qdrantService.upsertPoints(COLLECTION_NAME_2, requestBody);
            if (success) {
                System.out.println("✅ 成功插入2个向量点到集合: " + COLLECTION_NAME_2);
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
            searchRequest.put("vector", List.of(0.15f, 0.25f, 0.35f, 0.45f));
            searchRequest.put("limit", 2);
            searchRequest.put("with_payload", true);
            searchRequest.put("with_vector", false);
            
            // 执行搜索
            String result = qdrantService.searchPoints(COLLECTION_NAME_1, searchRequest);
            if (result != null) {
                System.out.println("✅ 搜索完成，结果:");
                System.out.println(result);
            } else {
                System.out.println("❌ 搜索向量点失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 搜索向量点失败: " + e.getMessage());
            // 只打印错误消息，不打印堆栈跟踪
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
            // 获取集合信息
            String info = qdrantService.getCollectionInfo(COLLECTION_NAME_2);
            if (info != null) {
                System.out.println("✅ 成功获取集合信息: " + COLLECTION_NAME_2);
                System.out.println(info);
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
            // 删除集合
            boolean success = qdrantService.deleteCollection(COLLECTION_NAME_2);
            if (success) {
                System.out.println("✅ 成功删除集合: " + COLLECTION_NAME_2);
            } else {
                System.out.println("❌ 删除集合失败");
            }
        } catch (Exception e) {
            System.out.println("❌ 删除集合失败: " + e.getMessage());
            // 忽略不存在的异常
        }
        System.out.println("=== 删除集合测试完成 ===\n");
    }
}
