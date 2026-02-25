package com.grape.grape.service.biz;

import com.grape.grape.model.vo.TestCaseGenerateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试用例生成服务测试类
 */
@SpringBootTest
public class AiTestCaseGenerateTest {

    @Autowired
    private AiBizService aiBizService;

    /**
     * 测试生成测试用例功能
     */
    @Test
    public void testGenerateTestCase() {
        // 创建测试请求参数
        TestCaseGenerateRequest request = new TestCaseGenerateRequest();
        request.setModule("向量数据库");
        request.setUserStory("作为测试人员，我希望测试向量数据库的功能，以便测试完成后能满足向量数据库的基本使用");
        request.setAcceptanceCriteria("测试完成后能满足向量数据库的基本使用");
        request.setBoundaryConditions("正常基础用例");
        request.setRelatedModules("");
        
        List<String> testDimensions = new ArrayList<>();
        testDimensions.add("功能测试");
        request.setTestDimensions(testDimensions);
        
        request.setCaseType("Functional Test Case");
        request.setCaseCount(5);
        request.setReferenceMode(true);
        request.setSimilarityThreshold(0.7f);
        request.setGenerateMode("Detailed Mode");
        request.setCaseTemplate("Standard Test Case Template");
        
        List<String> coverageRequirements = new ArrayList<>();
        coverageRequirements.add("Functional Coverage");
        coverageRequirements.add("Boundary Coverage");
        coverageRequirements.add("Exception Coverage");
        request.setCoverageRequirements(coverageRequirements);

        System.out.println("=== 开始测试测试用例生成功能 ===");
        System.out.println("测试模块: " + request.getModule());
        System.out.println("测试维度: " + request.getTestDimensions());
        System.out.println("生成数量: " + request.getCaseCount());
        System.out.println("参考模式: " + request.isReferenceMode());
        System.out.println("相似度阈值: " + request.getSimilarityThreshold());
        System.out.println("===============================");

        try {
            // 调用生成测试用例服务
            Map<String, Object> result = aiBizService.generateTestCase(request);
            
            // 打印结果
            System.out.println("\n=== 测试结果 ===");
            System.out.println("状态码: " + result.get("code"));
            System.out.println("消息: " + result.get("message"));
            
            if (result.containsKey("data")) {
                Object data = result.get("data");
                if (data instanceof List) {
                    List<?> testCases = (List<?>) data;
                    System.out.println("生成的测试用例数量: " + testCases.size());
                    
                    // 打印每个测试用例的基本信息
                    for (int i = 0; i < testCases.size(); i++) {
                        Object testCase = testCases.get(i);
                        System.out.println("\n测试用例 " + (i + 1) + ":");
                        System.out.println(testCase.toString());
                    }
                }
            }
            
            System.out.println("\n=== 测试完成 ===");
            
        } catch (Exception e) {
            System.out.println("\n=== 测试异常 ===");
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            System.out.println("===============================");
        }
    }
}
