package com.grape.grape.service.biz;

import cn.hutool.json.JSONUtil;
import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestCaseGeneratorRequest;
import com.grape.grape.model.prompt.ai.SirchmunkSearchResult;
import com.grape.grape.model.prompt.ai.SirchmunkSearchMode;
import com.grape.grape.model.prompt.PromptManager;
import com.grape.grape.service.ai.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * AI生成测试用例业务服务实现类
 * <p>
 * 集成Ollama向量服务、Milvus向量数据库、Sirchmunk文档搜索服务和Ollama通用AI服务
 * 实现从测试场景分析到测试用例生成的完整流程
 */
@Service
public class AIGenerateTestCaseBizServiceImpl implements AIGenerateTestCaseBizService {

    private static final Logger log = LoggerFactory.getLogger(AIGenerateTestCaseBizServiceImpl.class);

    @Resource
    private OllamaVectorService ollamaVectorService;

    @Resource
    private VectorDatabaseService vectorDatabaseService;

    @Resource
    private SirchmunkService sirchmunkService;

    @Resource
    private PromptManager promptManager;

    @Resource
    private OllamaGeneralService ollamaGeneralService;

    @Resource
    private PromptEngineeringService promptEngineeringService;

    /**
     * 生成测试用例
     * 
     * @param testObject 测试对象（比如安卓，PC）
     * @param testSubScenario 测试子场景（Joymaker的邮箱和手机号，谷歌登录，apple登录，游客登录）
     * @param testType 测试类型（功能，异常场景）
     * @param flowType 流程类型（异常场景，正常业务）
     * @return 生成的测试用例文本
     */
    @Override
    public String generateTestCases(String testObject, String testSubScenario, String testType, String flowType) {
        TestCaseGeneratorRequest request = buildTestCaseGeneratorRequest(testObject, testSubScenario, testType, flowType);
        return generateTestCasesFromRequest(request);
    }

    /**
     * 生成测试场景描述
     * 
     * @param testObject 测试对象
     * @param testSubScenario 测试子场景
     * @param testType 测试类型
     * @param flowType 流程类型
     * @return 测试场景描述字符串
     */
    private String generateTestScenario(String testObject, String testSubScenario, String testType, String flowType) {
        return String.format("测试%s ，使用%s方式，进行%s测试，验证%s下的行为是否符合预期",
                testObject, testSubScenario, testType, flowType);
    }

    /**
     * 构建测试用例生成请求
     * 
     * @param testObject 测试对象
     * @param testSubScenario 测试子场景
     * @param testType 测试类型
     * @param flowType 流程类型
     * @return 测试用例生成请求对象
     */
    private TestCaseGeneratorRequest buildTestCaseGeneratorRequest(String testObject, String testSubScenario, String testType, String flowType) {
        TestCaseGeneratorRequest request = new TestCaseGeneratorRequest();

        String testScenario = generateTestScenario(testObject, testSubScenario, testType, flowType);
        request.setTestScenario(testScenario);

        String sirchMunkSearchPrompt = String.format("""
                当前测试需求场景如下：%s,
                请根据以上测试需求场景，搜索相关文档信息，总结出与测试需求场景相关的文档内容。
                仅返回总结内容，不要多余的文字。
                """, testScenario);

        String sirchmunkDocument = "";
        try {
            SirchmunkSearchResult searchResult = sirchmunkService.searchForObject(sirchMunkSearchPrompt, SirchmunkSearchMode.DEEP);
            if (searchResult != null && searchResult.getData() != null && searchResult.getData().getSummary() != null) {
                sirchmunkDocument = searchResult.getData().getSummary();
            } else {
                sirchmunkDocument = "无相关的文档信息";
            }
        } catch (Exception e) {
            log.warn("Sirchmunk搜索失败，使用默认文档", e);
            sirchmunkDocument = "无相关的文档信息";
        }
        request.setSirchmunkDocument(sirchmunkDocument);

        List<TestCase> similarTestCases = searchSimilarTestCases(testScenario);
        request.setSimilarTestCases(similarTestCases);
        request.setSimilarTestCasesString(convertTestCasesToString(similarTestCases));

        request.setOutputRequirements("");

        return request;
    }

    /**
     * 从请求生成测试用例
     * 
     * @param request 测试用例生成请求
     * @return 生成的测试用例文本
     */
    private String generateTestCasesFromRequest(TestCaseGeneratorRequest request) {
        String prompt = promptEngineeringService.generateTestCaseGenerationPrompt(request);

        try {
            String response = ollamaGeneralService.generateText(prompt);
            return response;
        } catch (Exception e) {
            log.error("生成测试用例失败", e);
            return "";
        }
    }

    /**
     * 搜索相似测试用例
     * 
     * @param testScenario 测试场景描述
     * @return 相似测试用例列表
     */
    private List<TestCase> searchSimilarTestCases(String testScenario) {
        try {
            float[] queryVector = ollamaVectorService.embed(testScenario);
            List<float[]> queryVectors = new ArrayList<>();
            queryVectors.add(queryVector);
            List<TestCase> similarTestCases = vectorDatabaseService.searchByVectors(queryVectors, 5);
            return similarTestCases;
        } catch (Exception e) {
            log.warn("搜索相似用例失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 将测试用例列表转换为字符串
     * 
     * @param testCases 测试用例列表
     * @return 测试用例字符串表示
     */
    private String convertTestCasesToString(List<TestCase> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            return "";
        }
        List<String> caseStrings = new ArrayList<>();
        int index = 1;
        for (TestCase testCase : testCases) {
            caseStrings.add(String.format("""
                    测试用例%s：
                    - caseName: %s
                    - precondition: %s
                    - testSteps: %s
                    - expectedResult: %s
                    - priority: %s
                    - remark: %s
                    """,
                    index++,
                    testCase.getCaseName() != null ? testCase.getCaseName() : "",
                    testCase.getPrecondition() != null ? testCase.getPrecondition() : "",
                    testCase.getTestSteps() != null ? testCase.getTestSteps() : "",
                    testCase.getExpectedResult() != null ? testCase.getExpectedResult() : "",
                    testCase.getPriority() != null ? testCase.getPriority() : "",
                    testCase.getRemark() != null ? testCase.getRemark() : ""
            ));
        }
        return String.join("\n", caseStrings);
    }




}
