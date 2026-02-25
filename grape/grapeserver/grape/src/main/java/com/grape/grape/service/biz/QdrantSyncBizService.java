package com.grape.grape.service.biz;

import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.mapper.CasesMapper;
import com.grape.grape.mapper.TestCaseStepMapper;
import com.grape.grape.service.QdrantService;
import com.grape.grape.service.ai.SparkEmbeddingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Qdrant 同步业务服务
 * 用于处理测试用例向量化和存储的业务逻辑
 */
@Service
public class QdrantSyncBizService {

    @Autowired
    private CasesMapper casesMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private QdrantService qdrantService;

    @Autowired
    private SparkEmbeddingClient sparkEmbeddingClient;

    private static final String COLLECTION_NAME = "test_case_memory";
    private static final int VECTOR_SIZE = 2560; // 星火向量维度

    /**
     * 初始化向量数据库集合
     * 确保Qdrant集合存在且维度正确
     * @return 是否初始化成功
     */
    public boolean initVectorCollection() {
        try {
            // 先删除现有集合
            System.out.println("正在删除现有Qdrant集合: " + COLLECTION_NAME);
            boolean deleteSuccess = qdrantService.deleteCollection(COLLECTION_NAME);
            if (deleteSuccess) {
                System.out.println("✅ Qdrant集合删除成功");
            } else {
                System.out.println("⚠️  Qdrant集合删除失败或不存在，继续创建新集合");
            }

            // 再创建2560维的集合
            System.out.println("正在创建2560维的Qdrant集合: " + COLLECTION_NAME);
            boolean createSuccess = qdrantService.createCollection(COLLECTION_NAME, VECTOR_SIZE);
            if (createSuccess) {
                System.out.println("✅ Qdrant集合创建成功");
                return true;
            } else {
                System.out.println("❌ Qdrant集合创建失败");
                return false;
            }
        } catch (Exception e) {
            System.out.println("⚠️  初始化Qdrant集合时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 处理单个测试用例的向量化和存储
     * @param testCaseId 测试用例ID
     * @return 处理结果
     */
    public Map<String, Object> processSingleTestCase(Integer testCaseId) throws Exception {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询测试用例
        Cases testCase = casesMapper.selectOneById(testCaseId);
        if (testCase == null) {
            result.put("success", false);
            result.put("message", "未找到测试用例，ID: " + testCaseId);
            return result;
        }

        // 查询测试用例步骤
        List<TestCaseStep> steps = testCaseStepMapper.getByTestCaseId(testCaseId);

        // 2. 拼接文本并向量化
        String text = buildCaseText(testCase, steps);
        float[] vector = generateEmbedding(text);

        // 3. 构建 payload 并存入向量数据库
        Map<String, Object> payload = buildPayload(testCase, steps);
        Map<String, Object> point = new HashMap<>();
        point.put("id", testCase.getId()); // 使用整数类型的ID
        point.put("vector", vector);
        point.put("payload", payload);

        List<Map<String, Object>> points = Collections.singletonList(point);
        Map<String, Object> requestBody = Map.of("points", points);

        boolean success = qdrantService.upsertPoints(COLLECTION_NAME, requestBody);
        if (!success) {
            result.put("success", false);
            result.put("message", "存入向量数据库失败");
            return result;
        }

        // 构建返回结果
        result.put("success", true);
        result.put("message", "处理成功");
        result.put("testCase", testCase);
        result.put("steps", steps);
        result.put("vector", vector);
        result.put("vectorDimension", vector.length);
        result.put("payload", payload);

        return result;
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
        return sparkEmbeddingClient.getEmbeddingPara(text);
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
}
