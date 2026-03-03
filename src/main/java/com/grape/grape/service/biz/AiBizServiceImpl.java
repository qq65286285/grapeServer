package com.grape.grape.service.biz;

import com.grape.grape.entity.dto.RoleContent;
import com.grape.grape.model.vo.TestCaseGenerateRequest;
import com.grape.grape.service.QdrantService;
import com.grape.grape.service.ai.B_WsXModel;
import com.grape.grape.service.ai.SparkHttpClient;
import com.grape.grape.service.ai.SparkEmbeddingClient;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    private final ObjectMapper objectMapper;
    
    public AiBizServiceImpl() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Map<String, Object> callSpark(String question) {
        return callSpark(question, "ai_chat");
    }
    
    /**
     * 调用科大讯飞Spark API，指定服务类型
     * @param question 用户问题
     * @param serviceType 服务类型
     * @return 响应结果
     */
    public Map<String, Object> callSpark(String question, String serviceType) {
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

            // 调用WebSocket初始化方法，指定服务类型
            bWsXModel.initWebSocket(roleContent, serviceType);

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

                    String searchResult = qdrantService.searchPoints("test_case_memory", searchRequest);
                    if (searchResult != null) {
                        // 解析搜索结果，获取top N条相似用例作为参考
                        referenceCases = parseSearchResult(searchResult);
                    }
                }
            }

            // 3. 构建AI提示词
            String prompt = buildPrompt(request, referenceCases);

            // 4. 调用AI模型API
            // 使用callSpark方法通过WebSocket发送提示词到AI，指定服务类型为test_case_generator
            Map<String, Object> aiResponse = callSpark(prompt, "test_case_generator");

            // 5. 解析AI返回的JSON结果
            // 由于WebSocket是异步的，这里返回AI的响应状态
            // 实际的测试用例结果会通过WebSocket推送给前端
            List<Map<String, Object>> generatedCases = new ArrayList<>();
            
            // 添加一个提示用例，说明结果会通过WebSocket推送
            Map<String, Object> infoCase = new HashMap<>();
            infoCase.put("case_id", "1");
            infoCase.put("title", "测试用例生成中");
            infoCase.put("steps", Arrays.asList("提示：测试用例正在生成中", "结果将通过WebSocket推送给前端"));
            infoCase.put("expected", "请等待WebSocket推送的测试用例结果");
            generatedCases.add(infoCase);

            // 6. 返回生成的用例列表
            response.put("code", 0);
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
        sb.append("你是一位专业的测试用例生成助手，根据以下需求生成高质量的测试用例。\n");
        sb.append("\n[需求信息]\n");
        sb.append("模块: " + request.getModule()).append("\n");
        sb.append("用户故事: " + request.getUserStory()).append("\n");
        sb.append("验收标准: " + request.getAcceptanceCriteria()).append("\n");
        sb.append("边界条件: " + request.getBoundaryConditions()).append("\n");
        sb.append("相关模块: " + request.getRelatedModules()).append("\n");
        sb.append("测试维度: " + String.join(", ", request.getTestDimensions())).append("\n");
        sb.append("用例类型: " + request.getCaseType()).append("\n");

        if (!referenceCases.isEmpty()) {
            sb.append("\n[参考用例]\n");
            for (int i = 0; i < referenceCases.size(); i++) {
                Map<String, Object> caseMap = referenceCases.get(i);
                sb.append("参考用例 " + (i + 1) + ": " + caseMap.get("content")).append("\n");
            }
        }

        sb.append("\n[生成要求]\n");
        sb.append("用例数量: " + request.getCaseCount()).append("\n");
        sb.append("生成模式: " + request.getGenerateMode()).append("\n");
        sb.append("用例模板: " + request.getCaseTemplate()).append("\n");
        sb.append("覆盖要求: " + String.join(", ", request.getCoverageRequirements())).append("\n");
        sb.append("\n重要要求：重要要求：请严格以纯文本格式返回生成的测试用例列表，不要添加任何额外的说明文字或格式。\n");
        sb.append("格式要求：\n");
        sb.append("必须按照以下示例格式进行组织：\n");
        sb.append("每个测试用例TC标记前都要换行 \n");
        sb.append("TC标记后直接接测试用例标题，无缩进 \n");
        sb.append("步骤字段使用2个英文空格缩进 \n");
        sb.append("步骤内容使用4个英文空格缩进 \n");
        sb.append("预期结果使用6个英文空格缩进 \n");
        sb.append("TC，步骤，预期结果，都要换行  \n");
        String str = """
                示例：
                
                TC：指定操作下拉单选功能                
                步骤
                1、进入自动化连接器-自动化操作配置页面
                    预期结果：页面加载正常，配置区域展示完整
                2、点击"指定操作"下拉框
                    预期结果：下拉框展开，显示两个枚举值选项
                3、分别选择两个枚举值选项
                    预期结果：两个选项均可正常选中，选中状态清晰
                
                TC：指定操作与指定视图联动（任务视图）              
                步骤
                1、选择指定操作为"指定任务视图催单"
                    预期结果：指定操作选择成功，无报错
                2、查看"指定视图"下拉框内容
                    预期结果：下拉框返回自定义任务的视图列表，内容准确
                
                TC：指定操作与指定视图联动（工作项视图）                
                步骤
                1、选择指定操作为"指定工作项视图催单"
                    预期结果：指定操作选择成功，无卡顿
                2、查看"指定视图"下拉框内容
                    预期结果：下拉框返回全部工作项（含自定义）的视图列表，无遗漏
            
            请严格按照上述纯文本格式生成测试用例，不要添加任何额外的内容。
                """;
        sb.append(str);

        

        // sb.append("\n示例：\n");
        // sb.append("{\n");
        // sb.append("  \"test_cases\": [\n");
        // sb.append("    {\n");
        // sb.append("      \"case_id\": \"1\",\n");
        // sb.append("      \"title\": \"正常创建月度规划单并添加明细\",\n");
        // sb.append("      \"steps\": [\n");
        // sb.append("        \"1. 登录系统，进入月度规划模块\",\n");
        // sb.append("        \"2. 点击【新建规划单】，填写有效月份（如2023-10）和负责人\",\n");
        // sb.append("        \"3. 保存规划单，确认状态变为【已生效】\",\n");
        // sb.append("        \"4. 在规划单详情页点击【添加明细】，输入符合时间范围的业务数据\",\n");
        // sb.append("        \"5. 重复步骤4添加多条明细，总数量不超过系统预设上限\"\n");
        // sb.append("      ],\n");
        // sb.append("      \"expected\": \"规划单保存成功，所有明细均显示在对应规划单下，无审批流程触发\"\n");
        // sb.append("    },\n");
        // sb.append("    {\n");
        // sb.append("      \"case_id\": \"2\",\n");
        // sb.append("      \"title\": \"明细数量达到规划单上限\",\n");
        // sb.append("      \"steps\": [\n");
        // sb.append("        \"1. 创建规划单时设置明细数量上限为10条\",\n");
        // sb.append("        \"2. 连续添加10条有效明细\",\n");
        // sb.append("        \"3. 尝试添加第11条明细\"\n");
        // sb.append("      ],\n");
        // sb.append("      \"expected\": \"前10条明细保存成功，第11条提交时提示'超出规划单容量限制'\"\n");
        // sb.append("    }\n");
        // sb.append("  ]\n");
        // sb.append("}\n");
        sb.append("\n请严格按照上述纯文本脑图格式生成测试用例，不要添加任何额外的内容。");
        return sb.toString();
    }

    /**
     * 解析搜索结果
     */
    private List<Map<String, Object>> parseSearchResult(String searchResult) {
        List<Map<String, Object>> cases = new ArrayList<>();
        try {
            // 使用ObjectMapper解析JSON结果
            Map<String, Object> resultMap = objectMapper.readValue(searchResult, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            
            // 提取result字段
            Object resultObj = resultMap.get("result");
            if (resultObj instanceof List) {
                List<?> resultList = (List<?>) resultObj;
                
                // 遍历结果列表
                for (Object item : resultList) {
                    if (item instanceof Map) {
                        Map<?, ?> itemMap = (Map<?, ?>) item;
                        
                        // 提取payload字段
                        Object payloadObj = itemMap.get("payload");
                        if (payloadObj instanceof Map) {
                            Map<?, ?> payloadMap = (Map<?, ?>) payloadObj;
                            
                            // 提取content字段
                            Object contentObj = payloadMap.get("content");
                            if (contentObj != null) {
                                Map<String, Object> caseMap = new HashMap<>();
                                caseMap.put("content", contentObj.toString());
                                cases.add(caseMap);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing search result: {}", e.getMessage(), e);
            // 如果解析失败，返回空列表
        }
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