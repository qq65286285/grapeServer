package com.grape.grape;

import com.grape.grape.config.FilterFieldConfig;
import com.grape.grape.model.prompt.ai.ReferenceType;
import com.grape.grape.model.prompt.ai.SirchmunkSearchResult;
import com.grape.grape.model.prompt.ai.SirchmunkSearchMode;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestCaseGeneratorRequest;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;
import com.grape.grape.model.prompt.PromptManager;
import com.grape.grape.service.ai.OllamaGeneralService;
import com.grape.grape.service.ai.OllamaVectorService;
import com.grape.grape.service.ai.PromptEngineeringService;
import com.grape.grape.service.ai.SirchmunkService;
import com.grape.grape.service.ai.VectorDatabaseService;
import com.grape.grape.service.ai.algorithm.MaxSimilarityAlgorithm;
import com.grape.grape.service.biz.AIGenerateTestCaseBizService;
import com.grape.grape.utils.ai.QueryBuilder;
import com.grape.grape.utils.ai.VectorUtils;
import cn.hutool.json.JSONUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@SpringBootTest
public class CompleteFlowTest {

    @Autowired
    private OllamaVectorService ollamaVectorService;

    @Autowired
    private VectorDatabaseService vectorDatabaseService;

    @Autowired
    private SirchmunkService sirchmunkService;

    @Autowired
    private PromptManager promptManager;

    @Autowired
    private OllamaGeneralService ollamaGeneralService;

    @Autowired
    MaxSimilarityAlgorithm maxSimilarityAlgorithm;

    @Autowired
    private PromptEngineeringService promptEngineeringService;

    /**
     * 生成测试场景模板
     * @param testObject 测试对象：Android/iOS/Web
     * @param loginMethod 登录方式：账号密码/手机验证码/第三方授权/生物识别
     * @param testType 测试类型：功能/性能/安全/兼容性
     * @param flowType 流程类型：正常流程/异常流程/边界条件
     * @return 测试场景字符串
     */
    public String generateTestScenario(String testObject, String loginMethod, String testType, String flowType) {
        return "测试" + testObject + " ，使用" + loginMethod + "方式，进行" + 
        testType + "测试，验证" + flowType + "下的登录行为是否符合预期";
    }

    @Test
    public void testCompleteFlow() throws Exception {
        // 打印测试开始信息
        // System.out.println("Starting testCompleteFlow");
        TestCaseGeneratorRequest testCaseGeneratorRequest = new TestCaseGeneratorRequest();
        
        // 步骤耗时记录
        Map<String, Long> stepTimings = new HashMap<>();

        // 步骤1：生成测试场景
        long step1Start = System.currentTimeMillis();
        String testScenario = generateTestScenario(
            "Android，PC",
            "Joymaker的邮箱和手机号，谷歌登录，apple登录，游客登录",
             "功能，异常场景",
              "异常场景，正常业务");
        // System.out.println("Test Scenario Over" );
        long step1End = System.currentTimeMillis();
        stepTimings.put("步骤1:生成测试场景", step1End - step1Start);
        testCaseGeneratorRequest.setTestScenario(testScenario);
        // 步骤2：启动Sirchmunk搜索（异步）
        
        // 从SirchmunkService中搜索相关文档信息总结（异步执行）
        String sirchMunkSearchPromt = """
                当前测试需求场景如下：%s,
                请根据以上测试需求场景，搜索相关文档信息，总结出与测试需求场景相关的文档内容。
                仅返回总结内容，不要多余的文字。
                """.formatted(testScenario);
        
        // 执行异步搜索并获取future
        CompletableFuture<SirchmunkResult> sirchmunkFuture = searchWithSirchmunk(sirchMunkSearchPromt);
        
        // 继续执行后续步骤，不等待SirchMunk搜索完成
        // System.out.println("步骤2已启动异步处理");

        // 步骤3：生成测试场景分析提示词
        long step3Start = System.currentTimeMillis();
        String prompt = promptManager.generateTestScenarioAnalysisPrompt(testScenario);
        // System.out.println("Prompt generated " );
        long step3End = System.currentTimeMillis();
        stepTimings.put("步骤3:生成测试场景分析提示词", step3End - step3Start);

        // 步骤4：调用AI提取测试场景关键信息
        long step4Start = System.currentTimeMillis();
        // 使用OllamaVectorService的embed方法将测试场景转换为向量
        // 调用AI，提取测试场景中的关键信息
        // System.out.println("Calling extractKeyInfo");
        String testScenarioSummary = ollamaGeneralService.extractKeyInfo(testScenario, prompt);
        // System.out.println("Test Scenario Summary over " );
        long step4End = System.currentTimeMillis();
        stepTimings.put("步骤4:调用AI提取测试场景关键信息", step4End - step4Start);

        // 步骤5：解析测试场景分析结果
        long step5Start = System.currentTimeMillis();
        // 将JSON字符串转换为TestScenarioAnalysis对象
        // System.out.println("Calling parseTestScenarioAnalysis");

        TestScenarioAnalysis analysis = ollamaGeneralService.parseTestScenarioAnalysis(testScenarioSummary);
        if (analysis == null) {
            throw new RuntimeException("Failed to parse test scenario analysis");
        }
        // System.out.println("Analysis parsed successfully");
        long step5End = System.currentTimeMillis();
        stepTimings.put("步骤5:解析测试场景分析结果", step5End - step5Start);

        // 步骤6：构建查询策略并生成向量
        long step6Start = System.currentTimeMillis();
         //策略1 TOP-30
        //策略1：完整语义查询（主查询，推荐）
        // 拼接方式： 平台 + 功能模块 + 实体类别:实体值 + 测试类型
        List<String> queriesFullSemanticQueries = QueryBuilder.buildFullSemanticQueries(analysis);
        List<float[]> embeddingsFullSemanticQueries = ollamaVectorService.embedBatch(queriesFullSemanticQueries);

        // System.out.println("Full Semantic Queries: " + queriesFullSemanticQueries);
        //策略2：实体中心查询（精确匹配，推荐） TOP-20
        // 拼接方式： 为每个实体值生成独立查询
        List<String> queriesEntityCenterQueries = QueryBuilder.buildEntityCentricQueries(analysis);
        List<float[]> embeddingsEntityCenterQueries = ollamaVectorService.embedBatch(queriesEntityCenterQueries);


        // 策略3：测试点直接查询（细粒度） TOP-20
        // System.out.println("Building test points queries");
        List<String> queriesTestPointsQueries = QueryBuilder.buildTestPointQueries(analysis);
        System.out.println("Test Points Queries: " + queriesTestPointsQueries);
        // System.out.println("Calling embedBatch");
        List<float[]> embeddingsTestPointsQueries = ollamaVectorService.embedBatch(queriesTestPointsQueries);
        // System.out.println("Test Points Embeddings size: " + embeddingsTestPointsQueries.size());

        //策略4：场景描述查询（自然语言） TOP-10
        // 拼接方式： 直接使用 scenarios 中的 description
        List<String> queriesScenarioDescriptionQueries = QueryBuilder.buildScenarioDescriptionQueries(analysis);
        List<float[]> embeddingsScenarioDescriptionQueries = ollamaVectorService.embedBatch(queriesScenarioDescriptionQueries);
        long step6End = System.currentTimeMillis();
        stepTimings.put("步骤6:构建查询策略并生成向量", step6End - step6Start);

        // 步骤7：向量查询
        long step7Start = System.currentTimeMillis();
        // 步骤3：向量后的数据进行Milvus查询，返回 K=5-10
        System.out.println("Calling searchByVectors");
        //策略1 TOP-30
        List<TestCase> similarTestPoints = vectorDatabaseService.searchByVectors(embeddingsFullSemanticQueries, 30);
        //策略2 TOP-20
        List<TestCase> similarEntityCenter = vectorDatabaseService.searchByVectors(embeddingsEntityCenterQueries, 20);
        //策略3  TOP-20
        List<TestCase> similarTestPoints20 = vectorDatabaseService.searchByVectors(embeddingsTestPointsQueries, 20);
        //策略4 TOP-10
        List<TestCase> similarScenarioDescription = vectorDatabaseService.searchByVectors(embeddingsScenarioDescriptionQueries, 10);
        long step7End = System.currentTimeMillis();
        stepTimings.put("步骤7:向量查询", step7End - step7Start);

        // 输出4个list的caseId
        // System.out.println("策略1 Full Semantic Queries (TOP-30) - Case IDs:");
        // similarTestPoints.forEach(tc -> System.out.println("  " + tc.getId()));
        // System.out.println("策略2 Entity Center Queries (TOP-20) - Case IDs:");
        // similarEntityCenter.forEach(tc -> System.out.println("  " + tc.getId()));
        // System.out.println("策略3 Test Points Queries (TOP-20) - Case IDs:");
        // similarTestPoints20.forEach(tc -> System.out.println("  " + tc.getId()));
        // System.out.println("策略4 Scenario Description Queries (TOP-10) - Case IDs:");
        // similarScenarioDescription.forEach(tc -> System.out.println("  " + tc.getId()));
        
        // 步骤8：合并去重
        long step8Start = System.currentTimeMillis();
        //汇总     合并去重 + 过滤 + 重排序
        // 第一阶段：合并去重
        Map<String, TestCase> allSimilarTestCases = new HashMap<>();
        allSimilarTestCases = vectorDatabaseService.addTestCasesToMap(similarTestPoints, allSimilarTestCases);
        allSimilarTestCases = vectorDatabaseService.addTestCasesToMap(similarEntityCenter, allSimilarTestCases);
        allSimilarTestCases = vectorDatabaseService.addTestCasesToMap(similarTestPoints20, allSimilarTestCases);
        allSimilarTestCases = vectorDatabaseService.addTestCasesToMap(similarScenarioDescription, allSimilarTestCases);
        List<TestCase> allSimilarTestCasesList = new ArrayList<>(allSimilarTestCases.values());
        long step8End = System.currentTimeMillis();
        stepTimings.put("步骤8:合并去重", step8End - step8Start);
        // System.out.println("合并去重后测试用例数量: " + allSimilarTestCasesList.size());
        // System.out.println("SearchByVectors end");
        // 步骤9：提取参考文本
        long step9Start = System.currentTimeMillis();
        // 第二阶段：(按条件)过滤

        //设置条件
        List<ReferenceType> referenceTypes = Arrays.asList(
            ReferenceType.ENTITIES,
            ReferenceType.SCENARIOS,
            ReferenceType.PRODUCT
        );
        // System.out.println("Extracting text for embedding");
        Map<String,String> filterConditions = vectorDatabaseService.extractTextForEmbedding(allSimilarTestCasesList, analysis, referenceTypes);
        // System.out.println("Extracting text for embedding end");
        
        //  System.out.println(JSONUtil.toJsonStr(filterConditions));
        long step9End = System.currentTimeMillis();
        stepTimings.put("步骤9:提取参考文本", step9End - step9Start);

        // 步骤10：参考文本向量化
        long step10Start = System.currentTimeMillis();
        //调用向量模型进行向量化
        List<String> filterConditionsList = new ArrayList<>(filterConditions.values());
        // 添加测试用例的fullText到向量化列表
        for (TestCase testCase : allSimilarTestCasesList) {
            String fullText = testCase.getFullText();
            if (fullText != null && !fullText.isEmpty() && !filterConditionsList.contains(fullText)) {
                filterConditionsList.add(fullText);
            }
        }
        int batchSize = 50;  // 每批50条
        Map<String, float[]> embeddingCache = new HashMap<>();
        for (int i = 0; i < filterConditionsList.size(); i += batchSize) {
            // 取当前批次
            int end = Math.min(i + batchSize, filterConditionsList.size());
            List<String> batch = filterConditionsList.subList(i, end);

            // 调用向量模型API
            List<float[]> batchEmbeddings = ollamaVectorService.embedBatch(batch);

            // 存入缓存（注意对应关系）
            for (int j = 0; j < batch.size(); j++) {
                String text = batch.get(j);
                float[] embedding = batchEmbeddings.get(j);
                embeddingCache.put(text, embedding);
            }
        }
        // System.out.println("Embedding cache : " + JSONUtil.toJsonStr(embeddingCache));
        long step10End = System.currentTimeMillis();
        stepTimings.put("步骤10:参考文本向量化", step10End - step10Start);

        // 步骤11：配置权重并计算相似度
        long step11Start = System.currentTimeMillis();
        // 第三阶段：重排序
        // 1. 配置权重分配：testPoints 60%, function.subFunctions 30%, technical 10%
        Map<String, FilterFieldConfig> filterConfigs = new HashMap<>();

        // testPoints 配置：权重60%
        FilterFieldConfig testPointsConfig = new FilterFieldConfig();
        testPointsConfig.setName("testPoints");
        testPointsConfig.setWeight(0.6);
        testPointsConfig.setTestCaseField("fullText");  // 测试用例
        testPointsConfig.setAnalysisField("test.testPoints");  // 分析结果的测试点字段
        testPointsConfig.setThreshold(null);
        filterConfigs.put("testPoints", testPointsConfig);

        // function.subFunctions 配置：权重30%
        FilterFieldConfig subFunctionsConfig = new FilterFieldConfig();
        subFunctionsConfig.setName("function.subFunctions");
        subFunctionsConfig.setWeight(0.3);
        subFunctionsConfig.setTestCaseField("fullText");  // 测试用例
        subFunctionsConfig.setAnalysisField("function.subFunctions");  // 分析结果的子功能字段
        subFunctionsConfig.setThreshold(null);
        filterConfigs.put("function.subFunctions", subFunctionsConfig);

        // technical 配置：权重10%
        FilterFieldConfig technicalConfig = new FilterFieldConfig();
        technicalConfig.setName("technical");
        technicalConfig.setWeight(0.1);
        technicalConfig.setTestCaseField("fullText");  // 测试用例名称
        technicalConfig.setAnalysisField("technical.terms");  // 分析结果的技术术语字段
        technicalConfig.setThreshold(null);
        filterConfigs.put("technical", technicalConfig);

        // 2. 调用算法计算相似度并收集结果
        List<Map.Entry<TestCase, Double>> scoredTestCases = new ArrayList<>();

        // 使用多线程并发处理测试用例
        scoredTestCases = maxSimilarityAlgorithm.calculateTestCaseScoresParallel(
                allSimilarTestCasesList, analysis, filterConfigs,
                VectorUtils.convertToDoubleListCache(embeddingCache), 4
        );

        // 从 scoredTestCases 中提取 TestCase 对象并设置到请求中
        List<TestCase> testCases = scoredTestCases.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        testCaseGeneratorRequest.setSimilarTestCases(testCases);
        
        // 手动转换为JSON，排除embedding字段
        List<Map<String, Object>> testCasesWithoutEmbedding = new ArrayList<>();
        for (TestCase testCase : testCases) {
            Map<String, Object> testCaseMap = new HashMap<>();
            testCaseMap.put("id", testCase.getId());
            testCaseMap.put("caseId", testCase.getCaseId());
            testCaseMap.put("caseName", testCase.getCaseName());
            testCaseMap.put("fullText", testCase.getFullText());
            testCaseMap.put("precondition", testCase.getPrecondition());
            testCaseMap.put("testSteps", testCase.getTestSteps());
            testCaseMap.put("expectedResult", testCase.getExpectedResult());
            testCaseMap.put("priority", testCase.getPriority());
            testCaseMap.put("iteration", testCase.getIteration());
            testCaseMap.put("isSmokeTest", testCase.getIsSmokeTest());
            testCaseMap.put("automationFlag", testCase.getAutomationFlag());
            testCaseMap.put("screenshot", testCase.getScreenshot());
            testCaseMap.put("remark", testCase.getRemark());
            testCaseMap.put("createdAt", testCase.getCreatedAt());
            // 不添加embedding字段
            testCasesWithoutEmbedding.add(testCaseMap);
        }
        testCaseGeneratorRequest.setSimilarTestCasesString(JSONUtil.toJsonStr(testCasesWithoutEmbedding));

        long step11End = System.currentTimeMillis();
        stepTimings.put("步骤11:配置权重并计算相似度", step11End - step11Start);

        // 步骤12：排序并提取Top15
        long step12Start = System.currentTimeMillis();
        // 3. 按相似度分数降序排序
        scoredTestCases.sort(Comparator.comparingDouble((Map.Entry<TestCase, Double> entry) -> entry.getValue()).reversed());

        //todo end

        // 4. 提取排序后的前15条测试用例
        List<TestCase> top15TestCases = new ArrayList<>();
        int topCount = Math.min(15, scoredTestCases.size());
        for (int i = 0; i < topCount; i++) {
            top15TestCases.add(scoredTestCases.get(i).getKey());
        }

        // 输出前15条测试用例
        // System.out.println("重排序后的前15条测试用例:");
        // for (int i = 0; i < top15TestCases.size(); i++) {
        //     TestCase tc = top15TestCases.get(i);
        //     Double score = scoredTestCases.get(i).getValue();
        //     System.out.println((i + 1) + ". " + tc.getFullText() + " (Score: " + score + ")");
        // }
        long step12End = System.currentTimeMillis();
        stepTimings.put("步骤12:排序并提取Top15", step12End - step12Start);
        // System.out.println("排序后提取Top15测试用例数量: " + top15TestCases.size());

        // 步骤13：等待异步操作完成
        // 等待步骤2的异步搜索完成并记录耗时
        try {
            SirchmunkResult sirchmunkResult = sirchmunkFuture.get(); // 等待异步搜索完成
            long step2Duration = sirchmunkResult.getDuration();
            SirchmunkSearchResult searchResult = sirchmunkResult.getSearchResult();
            
            // 设置Sirchmunk文档到请求对象
            if (searchResult != null && searchResult.getSuccess() && searchResult.getData() != null) {
                testCaseGeneratorRequest.setSirchmunkDocument(searchResult.getData().getSummary());
            }
            
            stepTimings.put("步骤2:Sirchmunk搜索（异步）", step2Duration);
        } catch (Exception e) {
            System.err.println("等待Sirchmunk搜索完成时出错: " + e.getMessage());
            stepTimings.put("步骤2:Sirchmunk搜索（异步）", 0L);
        }
        
        // 打印测试结束信息
        // long endTime = System.currentTimeMillis();
        // System.out.println("\n=== 测试完成 ===");
        // System.out.println("总耗时: " + (endTime - startTime) + "ms");
        
        // 打印步骤耗时信息
        // System.out.println("\n=== 步骤耗时详情 ===");
        // 按步骤编号排序输出
        // testCaseGeneratorRequest.setStepTimings(stepTimings);
        // stepTimings.entrySet().stream()
        //     .sorted((e1, e2) -> {
        //         // 提取步骤编号进行排序
        //         int num1 = Integer.parseInt(e1.getKey().replaceAll("[^0-9]", ""));
        //         int num2 = Integer.parseInt(e2.getKey().replaceAll("[^0-9]", ""));
        //         return Integer.compare(num1, num2);
        //     })
        //     .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + "ms"));
        

        String genTestCaseRequest = promptEngineeringService.generateTestCaseSummary(testCaseGeneratorRequest);
        // System.out.println("genTestCaseRequest: " + genTestCaseRequest);
        String genTestCaseResult = ollamaGeneralService.generateText(genTestCaseRequest);
        System.out.println("genTestCaseResult: " + genTestCaseResult);
        System.out.println("testCompleteFlow completed");
    }


    @Autowired
    AIGenerateTestCaseBizService aIGenerateTestCaseBizService;

    @Test
    public void testGenerateTestCase() {
        String testObject = "Joymaker";
        String testSubScenario = "邮箱和手机号";
        String testType = "功能";
        String flowType = "正常业务";
        String testCasesText = aIGenerateTestCaseBizService.generateTestCases(testObject, testSubScenario, testType, flowType);
        System.out.println("-----------------");
        System.out.println("testCasesText: " + testCasesText);
    }
    /**
     * 用于存储Sirchmunk搜索结果和耗时
     */
    private static class SirchmunkResult {
        private final long duration;
        private final SirchmunkSearchResult searchResult;
        
        public SirchmunkResult(long duration, SirchmunkSearchResult searchResult) {
            this.duration = duration;
            this.searchResult = searchResult;
        }
        
        public long getDuration() {
            return duration;
        }
        
        public SirchmunkSearchResult getSearchResult() {
            return searchResult;
        }
    }

    /**
     * 异步执行Sirchmunk搜索
     * 
     * @param searchPrompt 搜索提示词
     * @return CompletableFuture 用于等待搜索完成并获取结果
     */
    private CompletableFuture<SirchmunkResult> searchWithSirchmunk(String searchPrompt) {
        return CompletableFuture.supplyAsync(() -> {
            long step2Start = System.currentTimeMillis();
            // System.out.println("[异步] 开始SirchMunk搜索... (步骤2)");
            
            SirchmunkSearchResult sirchMunkSearchResult = sirchmunkService.searchForObject(searchPrompt, SirchmunkSearchMode.DEEP);
            if (sirchMunkSearchResult != null && sirchMunkSearchResult.getSuccess()) {
                // System.out.println("[异步] SirchMunk搜索成功");
            } else {
                System.out.println("[异步] SirchMunk搜索失败");
            }
            
            long step2End = System.currentTimeMillis();
            long duration = step2End - step2Start;
            // System.out.println("[异步] 步骤2耗时: " + duration + "ms");
            return new SirchmunkResult(duration, sirchMunkSearchResult);
        });
    }




    // @Test
    // public void testExtractKeyInfo() throws Exception {
    //     // 测试提取关键信息
    //     String testScenario = "测试Android SDK的用户认证-登录功能，使用邮箱登录方式，进行功能测试，验证正常流程下的登录行为是否符合预期";
    //     String prompt = "请分析以下测试场景描述，提取关键信息并返回JSON格式：" + testScenario;

    //     String result = ollamaGeneralService.extractKeyInfo(testScenario, prompt);
    //     // System.out.println("Extract Key Info Result: " + result);
    // }

}
