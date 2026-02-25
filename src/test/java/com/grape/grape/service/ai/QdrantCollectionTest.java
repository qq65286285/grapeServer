package com.grape.grape.service.ai;

import com.grape.grape.service.QdrantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Qdrant 集合测试类
 * 用于查看向量数据库中测试用例集合的结构
 */
@SpringBootTest
public class QdrantCollectionTest {

    @Autowired
    private QdrantService qdrantService;

    private static final String COLLECTION_NAME = "test_case_memory";

    /**
     * 查看集合信息
     */
    @Test
    public void testCollectionInfo() {
        System.out.println("=== 查看Qdrant集合信息 ===");

        try {
            // 获取集合信息
            String info = qdrantService.getCollectionInfo(COLLECTION_NAME);
            System.out.println("集合信息:");
            System.out.println(info);

        } catch (Exception e) {
            System.out.println("❌ 获取集合信息失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 测试完成 ===");
    }

    /**
     * 查看集合中的数据结构
     */
    @Test
    public void testCollectionData() {
        System.out.println("=== 查看Qdrant集合数据结构 ===");

        try {
            // 构建查询请求
            java.util.Map<String, Object> searchRequest = new java.util.HashMap<>();
            // 使用空向量进行查询
            float[] emptyVector = new float[2560]; // 星火文本向量维度
            searchRequest.put("vector", emptyVector);
            searchRequest.put("limit", 5); // 只查看前5条
            searchRequest.put("with_payload", true);
            searchRequest.put("with_vector", false);

            // 调用Qdrant服务查询
            String searchResult = qdrantService.searchPoints(COLLECTION_NAME, searchRequest);
            System.out.println("集合数据:");
            System.out.println(searchResult);

        } catch (Exception e) {
            System.out.println("❌ 查询集合数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 测试完成 ===");
    }
}
