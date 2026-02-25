package com.grape.grape.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.mapper.CasesMapper;
import com.grape.grape.mapper.TestCaseStepMapper;
import com.grape.grape.service.ai.SparkEmbeddingClient;
import com.mybatisflex.core.query.QueryWrapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Qdrant 同步服务
 * 用于将测试用例数据同步到 Qdrant 向量数据库
 */
@Service
public class QdrantSyncService {

    @Autowired
    private CasesMapper casesMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private QdrantService qdrantService;

    @Value("${xfyun.spark-emb.appid}")
    private String sparkEmbAppId;

    @Value("${xfyun.spark-emb.api-key}")
    private String sparkEmbApiKey;

    @Value("${xfyun.spark-emb.api-secret}")
    private String sparkEmbApiSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private SparkEmbeddingClient embeddingClient;

    private static final String COLLECTION_NAME = "test_case_memory";
    private static final int VECTOR_SIZE = 2560; // 星火文本向量维度
    private static final int BATCH_SIZE = 100;

    private long lastSyncTime = 0;

    // 获取SparkEmbeddingClient实例
    private SparkEmbeddingClient getEmbeddingClient() {
        if (embeddingClient == null) {
            embeddingClient = new SparkEmbeddingClient(sparkEmbAppId, sparkEmbApiKey, sparkEmbApiSecret);
        }
        return embeddingClient;
    }

    /**
     * 初始化同步服务
     */
    public void init() {
        // 检查并创建集合
        checkAndCreateCollection();
    }

    /**
     * 检查并创建集合
     */
    private void checkAndCreateCollection() {
        try {
            // 检查集合是否存在
            String info = qdrantService.getCollectionInfo(COLLECTION_NAME);
            if (info == null || info.contains("Not found")) {
                // 创建集合
                boolean success = qdrantService.createCollection(COLLECTION_NAME, VECTOR_SIZE);
                if (success) {
                    System.out.println("✅ 成功创建 Qdrant 集合: " + COLLECTION_NAME);
                } else {
                    System.out.println("❌ 创建 Qdrant 集合失败");
                }
            } else {
                System.out.println("✅ Qdrant 集合已存在: " + COLLECTION_NAME);
            }
        } catch (Exception e) {
            System.out.println("❌ 检查集合失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 全量同步所有测试用例
     */
    public void syncAllCases() {
        System.out.println("=== 开始全量同步测试用例到 Qdrant ===");
        
        try {
            // 查询所有未删除的测试用例
            QueryWrapper queryWrapper = QueryWrapper.create();
            List<Cases> casesList = casesMapper.selectListByQuery(queryWrapper);

            System.out.println("发现 " + casesList.size() + " 个测试用例");

            // 批量处理
            for (int i = 0; i < casesList.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, casesList.size());
                List<Cases> batchCases = casesList.subList(i, end);
                processBatchCases(batchCases);
                System.out.println("已同步 " + end + "/" + casesList.size() + " 个测试用例");
            }

            // 更新最后同步时间
            lastSyncTime = System.currentTimeMillis();
            System.out.println("=== 全量同步完成 ===");
        } catch (Exception e) {
            System.out.println("❌ 全量同步失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 增量同步测试用例
     */
    private void syncIncrementalCases() {
        System.out.println("=== 开始增量同步测试用例到 Qdrant ===");
        
        try {
            // 查询上次同步时间之后更新的测试用例
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .gt(Cases::getUpdatedAt, lastSyncTime);
            List<Cases> casesList = casesMapper.selectListByQuery(queryWrapper);

            System.out.println("发现 " + casesList.size() + " 个增量测试用例");

            // 批量处理
            for (int i = 0; i < casesList.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, casesList.size());
                List<Cases> batchCases = casesList.subList(i, end);
                processBatchCases(batchCases);
            }

            // 更新最后同步时间
            lastSyncTime = System.currentTimeMillis();
            System.out.println("=== 增量同步完成 ===");
        } catch (Exception e) {
            System.out.println("❌ 增量同步失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理批量测试用例
     */
    private void processBatchCases(List<Cases> batchCases) {
        List<Map<String, Object>> points = new ArrayList<>();

        for (Cases testCase : batchCases) {
            try {
                // 获取测试用例步骤
                List<TestCaseStep> steps = testCaseStepMapper.getByTestCaseId(testCase.getId());

                // 拼接文本
                String text = buildCaseText(testCase, steps);

                // 生成 embedding 向量
                float[] vector = generateEmbedding(text);

                // 构建 payload
                Map<String, Object> payload = buildPayload(testCase, steps);

                // 构建 point
                Map<String, Object> point = new HashMap<>();
                point.put("id", testCase.getId().toString());
                point.put("vector", vector);
                point.put("payload", payload);

                points.add(point);
            } catch (Exception e) {
                System.out.println("❌ 处理测试用例失败 (ID: " + testCase.getId() + "): " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 批量写入 Qdrant
        if (!points.isEmpty()) {
            Map<String, Object> requestBody = Map.of("points", points);
            boolean success = qdrantService.upsertPoints(COLLECTION_NAME, requestBody);
            if (success) {
                System.out.println("✅ 成功写入 " + points.size() + " 个测试用例到 Qdrant");
            } else {
                System.out.println("❌ 写入 Qdrant 失败");
            }
        }
    }

    /**
     * 拼接测试用例文本
     */
    private String buildCaseText(Cases testCase, List<TestCaseStep> steps) {
        // 主文本
        StringBuilder mainText = new StringBuilder();
        mainText.append(testCase.getTitle()).append("，");
        mainText.append(testCase.getDescription()).append("，");
        mainText.append("模块：").append(testCase.getModule()).append("，");
        mainText.append("优先级：").append(getPriorityText(testCase.getPriority())).append("，");
        mainText.append(testCase.getExpectedResult()).append("，");
        mainText.append(testCase.getRemark());

        // 步骤文本
        StringBuilder stepsText = new StringBuilder();
        for (TestCaseStep step : steps) {
            stepsText.append("[步骤").append(step.getStepNumber()).append("]");
            stepsText.append(step.getStep()).append("，");
            stepsText.append("预期结果：").append(step.getExpectedResult()).append(" | ");
        }

        return mainText.toString() + " " + stepsText.toString();
    }

    /**
     * 获取优先级文本
     */
    private String getPriorityText(Integer priority) {
        return switch (priority) {
            case 1 -> "Low";
            case 2 -> "Medium";
            case 3 -> "High";
            default -> "Unknown";
        };
    }

    /**
     * 生成 embedding 向量
     */
    private float[] generateEmbedding(String text) throws Exception {
        // 使用SparkEmbeddingClient生成文档向量
        return getEmbeddingClient().getEmbeddingPara(text);
    }

    /**
     * 查询所有向量数据
     */
    public List<Map<String, Object>> getAllVectors() throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 构建查询请求
        Map<String, Object> searchRequest = new HashMap<>();
        // 使用空向量进行查询
        float[] emptyVector = new float[VECTOR_SIZE];
        searchRequest.put("vector", emptyVector);
        searchRequest.put("limit", 100); // 限制返回数量
        searchRequest.put("with_payload", true);
        searchRequest.put("with_vector", false);
        
        // 调用Qdrant服务查询
        String searchResult = qdrantService.searchPoints(COLLECTION_NAME, searchRequest);
        
        // 解析结果
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(searchResult, Map.class);
        List<?> results = (List<?>) responseMap.get("result");
        
        for (Object item : results) {
            Map<?, ?> itemMap = (Map<?, ?>) item;
            Map<String, Object> vectorData = new HashMap<>();
            vectorData.put("id", itemMap.get("id"));
            vectorData.put("score", itemMap.get("score"));
            vectorData.put("payload", itemMap.get("payload"));
            result.add(vectorData);
        }
        
        return result;
    }

    /**
     * 根据测试用例ID查询向量
     */
    public Map<String, Object> getVectorByCaseId(Integer caseId) throws Exception {
        // 构建查询请求
        Map<String, Object> searchRequest = new HashMap<>();
        // 先获取测试用例文本
        Cases testCase = casesMapper.selectOneById(caseId);
        if (testCase == null) {
            throw new Exception("测试用例不存在，ID: " + caseId);
        }
        List<TestCaseStep> steps = testCaseStepMapper.getByTestCaseId(caseId);
        String text = buildCaseText(testCase, steps);
        // 生成向量
        float[] vector = generateEmbedding(text);
        searchRequest.put("vector", vector);
        searchRequest.put("limit", 1);
        searchRequest.put("with_payload", true);
        searchRequest.put("with_vector", false);
        
        // 调用Qdrant服务查询
        String searchResult = qdrantService.searchPoints(COLLECTION_NAME, searchRequest);
        
        // 解析结果
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(searchResult, Map.class);
        List<?> results = (List<?>) responseMap.get("result");
        
        if (results.isEmpty()) {
            return null;
        }
        
        Map<?, ?> itemMap = (Map<?, ?>) results.get(0);
        Map<String, Object> vectorData = new HashMap<>();
        vectorData.put("id", itemMap.get("id"));
        vectorData.put("score", itemMap.get("score"));
        vectorData.put("payload", itemMap.get("payload"));
        
        return vectorData;
    }

    /**
     * 相似向量搜索
     */
    public List<Map<String, Object>> searchSimilarVectors(String queryText, Integer limit) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 生成查询向量
        float[] queryVector = getEmbeddingClient().getEmbeddingQuery(queryText);
        
        // 构建查询请求
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put("vector", queryVector);
        searchRequest.put("limit", limit);
        searchRequest.put("with_payload", true);
        searchRequest.put("with_vector", false);
        
        // 调用Qdrant服务查询
        String searchResult = qdrantService.searchPoints(COLLECTION_NAME, searchRequest);
        
        // 解析结果
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(searchResult, Map.class);
        List<?> results = (List<?>) responseMap.get("result");
        
        for (Object item : results) {
            Map<?, ?> itemMap = (Map<?, ?>) item;
            Map<String, Object> vectorData = new HashMap<>();
            vectorData.put("id", itemMap.get("id"));
            vectorData.put("score", itemMap.get("score"));
            vectorData.put("payload", itemMap.get("payload"));
            result.add(vectorData);
        }
        
        return result;
    }

    /**
     * 构建 payload
     */
    private Map<String, Object> buildPayload(Cases testCase, List<TestCaseStep> steps) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("caseId", testCase.getId());
        payload.put("caseNumber", testCase.getCaseNumber());
        payload.put("caseTitle", testCase.getTitle());
        payload.put("caseDesc", testCase.getDescription());
        payload.put("priority", testCase.getPriority());
        payload.put("status", testCase.getStatus());
        payload.put("module", testCase.getModule());
        payload.put("expectedResult", testCase.getExpectedResult());
        payload.put("version", testCase.getVersion());
        payload.put("environmentId", testCase.getEnvironmentId());
        payload.put("remark", testCase.getRemark());
        payload.put("createdBy", testCase.getCreatedBy());
        payload.put("createdAt", testCase.getCreatedAt());
        payload.put("updatedAt", testCase.getUpdatedAt());
        payload.put("stepCount", steps.size());

        // 构建步骤列表
        List<Map<String, Object>> stepList = new ArrayList<>();
        for (TestCaseStep step : steps) {
            Map<String, Object> stepMap = new HashMap<>();
            stepMap.put("stepNumber", step.getStepNumber());
            stepMap.put("stepDesc", step.getStep());
            stepMap.put("expectedResult", step.getExpectedResult());
            stepList.add(stepMap);
        }
        payload.put("steps", stepList);

        return payload;
    }

    /**
     * 关闭定时任务
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
