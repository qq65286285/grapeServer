package com.grape.grape.service.biz;

import com.grape.grape.entity.dto.RoleContent;
import com.grape.grape.model.vo.TestCaseGenerateRequest;
import com.grape.grape.service.QdrantService;
import com.grape.grape.service.ai.B_WsXModel;
import com.grape.grape.service.ai.SparkHttpClient;
import com.grape.grape.service.ai.SparkEmbeddingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * AI业务服务实现类
 * 实现AI相关的业务逻辑
 */
@Service
public class AiBizServiceImpl implements AiBizService {

    private static final Logger logger = LoggerFactory.getLogger(AiBizServiceImpl.class);

    @Autowired
    private B_WsXModel bWsXModel;

    @Autowired
    private QdrantService qdrantService;

    @Autowired
    private SparkHttpClient sparkHttpClient;

    @Autowired
    private SparkEmbeddingClient sparkEmbeddingClient;

    @Override
    public Map<String, Object> callSpark(String question) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (question == null || question.isEmpty()) {
                response.put("code", 400);
                response.put("message", "问题不能为空");
                return response;
            }

            // 创建角色内容对象
            RoleContent roleContent = new RoleContent();
            roleContent.setRole("user");
            roleContent.setContent(question);

            // 调用WebSocket初始化方法
            bWsXModel.initWebSocket(roleContent);

            // 注意：由于WebSocket是异步通信，这里无法直接返回结果
            // 实际应用中，需要通过WebSocket或其他方式将结果推送给前端
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", "已发送请求到科大讯飞Spark API，请等待结果");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "调用失败：" + e.getMessage());
        }
        return response;
    }

    @Override
    public Map<String, Object> generateTestCase(TestCaseGenerateRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. 接收请求参数
            if (request == null) {
                response.put("code", 400);
                response.put("message", "Request parameters cannot be empty");
                return response;
            }

            // 2. 如果参考模式=true
            List<Map<String, Object>> referenceCases = new ArrayList<>();
            if (request.isReferenceMode()) {
                // 构建检索向量
                String queryText = buildQueryText(request);
                List<Double> embedding = sparkEmbeddingClient.getEmbedding(queryText);

                if (embedding != null && !embedding.isEmpty()) {
                    // 调用向量数据库（qdrant）检索相似用例
                    Map<String, Object> searchRequest = new HashMap<>();
                    searchRequest.put("vector", embedding);
                    searchRequest.put("limit", request.getCaseCount());
                    searchRequest.put("score_threshold", request.getSimilarityThreshold());

                    String searchResult = qdrantService.searchPoints("test_cases", searchRequest);
                    if (searchResult != null) {
                        // 解析搜索结果，获取top N条相似用例作为参考
                        // 这里简化处理，实际需要解析JSON结果
                        referenceCases = parseSearchResult(searchResult);
                    }
                }
            }

            // 3. 构建AI提示词
            String prompt = buildPrompt(request, referenceCases);

            // 4. 调用AI模型API
            String aiResponse = sparkHttpClient.sendSyncRequest(prompt);

            // 5. 解析AI返回的JSON结果
            List<Map<String, Object>> generatedCases = parseAIResponse(aiResponse);

            // 6. 返回生成的用例列表
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", generatedCases);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "Failed to generate test cases: " + e.getMessage());
        }
        return response;
    }

    /**
     * 构建查询文本
     */
    private String buildQueryText(TestCaseGenerateRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getModule()).append(" ");
        sb.append(request.getUserStory()).append(" ");
        sb.append(request.getAcceptanceCriteria()).append(" ");
        sb.append(request.getBoundaryConditions());
        return sb.toString();
    }

    /**
     * 构建AI提示词
     */
    private String buildPrompt(TestCaseGenerateRequest request, List<Map<String, Object>> referenceCases) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a professional test case generation assistant, generate high-quality test cases based on the following requirements.\n");
        sb.append("\n[Requirement Information]\n");
        sb.append("Module: " + request.getModule()).append("\n");
        sb.append("User Story: " + request.getUserStory()).append("\n");
        sb.append("Acceptance Criteria: " + request.getAcceptanceCriteria()).append("\n");
        sb.append("Boundary Conditions: " + request.getBoundaryConditions()).append("\n");
        sb.append("Related Modules: " + request.getRelatedModules()).append("\n");
        sb.append("Test Dimensions: " + String.join(", ", request.getTestDimensions())).append("\n");
        sb.append("Case Type: " + request.getCaseType()).append("\n");

        if (!referenceCases.isEmpty()) {
            sb.append("\n[Reference Cases]\n");
            for (int i = 0; i < referenceCases.size(); i++) {
                Map<String, Object> caseMap = referenceCases.get(i);
                sb.append("Reference Case " + (i + 1) + ": " + caseMap.get("content")).append("\n");
            }
        }

        sb.append("\n[Generation Requirements]\n");
        sb.append("Case Count: " + request.getCaseCount()).append("\n");
        sb.append("Generate Mode: " + request.getGenerateMode()).append("\n");
        sb.append("Case Template: " + request.getCaseTemplate()).append("\n");
        sb.append("Coverage Requirements: " + String.join(", ", request.getCoverageRequirements())).append("\n");
        sb.append("\nPlease return the generated test case list strictly in JSON format, example:\n");
        sb.append("{\"test_cases\": [{\"case_id\": \"1\", \"title\": \"Test Title\", \"steps\": [\"Step 1\", \"Step 2\"], \"expected\": \"Expected Result\"}]}");
        return sb.toString();
    }

    /**
     * 解析搜索结果
     */
    private List<Map<String, Object>> parseSearchResult(String searchResult) {
        // 这里简化处理，实际需要使用JSON解析库解析结果
        List<Map<String, Object>> cases = new ArrayList<>();
        // 示例解析逻辑
        Map<String, Object> case1 = new HashMap<>();
        case1.put("content", "参考用例1：测试登录功能");
        cases.add(case1);
        return cases;
    }

    /**
     * 解析AI返回的结果
     */
    private List<Map<String, Object>> parseAIResponse(String aiResponse) {
        // 这里简化处理，实际需要使用JSON解析库解析结果
        List<Map<String, Object>> cases = new ArrayList<>();
        // 示例解析逻辑
        Map<String, Object> case1 = new HashMap<>();
        case1.put("case_id", "1");
        case1.put("title", "测试登录功能-正常流程");
        case1.put("steps", Arrays.asList("1. 输入用户名", "2. 输入密码", "3. 点击登录"));
        case1.put("expected", "登录成功，跳转到首页");
        cases.add(case1);
        return cases;
    }
}
