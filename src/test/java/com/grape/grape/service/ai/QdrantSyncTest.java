package com.grape.grape.service.ai;

import com.grape.grape.service.QdrantService;
import com.grape.grape.service.biz.QdrantSyncBizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Qdrant 同步测试类
 * 用于测试单个测试用例的向量化和存储功能
 */
@SpringBootTest
public class QdrantSyncTest {

    @Autowired
    private QdrantSyncBizService qdrantSyncBizService;

    @Autowired
    private QdrantService qdrantService;

    /**
     * 测试单个测试用例的向量化和存储
     */
    @Test
    public void testSingleCaseSync() {
        // 手动提供测试用例ID
        Integer testCaseId = 17; // 这里可以修改为你想要测试的用例ID

        System.out.println("=== 开始测试单个测试用例同步 ===");

        try {
            // 初始化向量数据库集合（调用bizservice中的方法）
            System.out.println("正在初始化向量数据库集合...");
            boolean initSuccess = qdrantSyncBizService.initVectorCollection();
            if (!initSuccess) {
                System.out.println("❌ 向量数据库集合初始化失败，无法继续测试");
                return;
            }

            // 调用业务服务处理测试用例
            Map<String, Object> result = qdrantSyncBizService.processSingleTestCase(testCaseId);

            // 打印处理结果
            if ((boolean) result.get("success")) {
                System.out.println("✅ 处理成功");
                System.out.println("消息: " + result.get("message"));
                System.out.println("测试用例: " + result.get("testCase"));
                System.out.println("步骤: " + result.get("steps"));
                System.out.println("向量维度: " + result.get("vectorDimension"));
                System.out.println("Payload: " + result.get("payload"));

                // 调用查询方法
                String searchResult = searchVectorDatabase((float[]) result.get("vector"));
                System.out.println("查询结果: " + searchResult);
            } else {
                System.out.println("❌ 处理失败: " + result.get("message"));
            }

        } catch (Exception e) {
            System.out.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 测试完成 ===");
    }

    /**
     * 查询向量数据库
     * @param vector 向量数据
     * @return 查询结果
     */
    private String searchVectorDatabase(float[] vector) {
        try {
            Map<String, Object> searchRequest = new HashMap<>();
            searchRequest.put("vector", vector);
            searchRequest.put("limit", 1);
            searchRequest.put("with_payload", true);
            searchRequest.put("with_vector", false);

            return qdrantService.searchPoints("test_case_memory", searchRequest);
        } catch (Exception e) {
            System.out.println("❌ 查询向量数据库失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
