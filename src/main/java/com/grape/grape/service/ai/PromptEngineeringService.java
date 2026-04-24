package com.grape.grape.service.ai;

import com.grape.grape.model.prompt.ai.TestCaseGeneratorRequest;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PromptEngineeringService {


    /**
     * 生成测试用例生成提示词
     *
     * @param request 测试用例生成请求
     * @return 提示词字符串
     */
    public String generateTestCaseGenerationPrompt(TestCaseGeneratorRequest request) {
        return """
                # 测试用例生成任务
                
                # 禁令 
                严禁输出任何测试用例以外的内容，
                包括但不限于前置说明、思考过程、解释、结束语、结束总结、引导提问
                不要任何说明语句，仅返回用例
                直接输出测试用例即可。 
                回复必须从测试用例正文开始。 
                结束也必须是测试用例正文结束。
                
                ## 角色定义
                %s
                
                ## 任务目标
                请根据以下信息，生成符合TestCase类结构的测试用例。
                
                ## 输入信息
                
                ### 上下文信息
                %s
                
                ### 测试场景
                %s
                
                ### 用户场景描述
                %s
                
                ### Milvus 向量数据库相似用例
                %s
                
                ### Sirchmunk 文档
                %s
                
                ## 输出要求
                %s
                
                ## 生成格式
                请严格按照以下格式生成测试用例，每个测试用例包含以下字段：
                
                1. **caseName**：用例名称
                2. **precondition**：前置条件
                3. **testSteps**：测试步骤（详细描述每一步操作）
                4. **expectedResult**：期望结果（详细描述预期行为）
                5. **priority**：用例分级 (P0/P1/P2)
                6. **remark**：备注
                7. **fullText**：完整原文（自动生成，格式为："用例名称：{caseName}，前置条件：{precondition}，测试步骤：{testSteps}，期望结果：{expectedResult}，用例分级：{priority}，备注：{remark}"）
                
                ## 格式示例
                
                测试用例1：
                - caseName: 邮箱登录功能测试
                - precondition: 1. 网络连接正常 2. 用户已注册邮箱账号
                - testSteps: 1. 打开应用 2. 点击登录按钮 3. 选择邮箱登录 4. 输入邮箱和密码 5. 点击登录
                - expectedResult: 成功登录并进入主页
                - priority: P1
                - remark: 测试正常登录流程
                - fullText: 用例名称：邮箱登录功能测试，前置条件：1. 网络连接正常 2. 用户已注册邮箱账号，测试步骤：1. 打开应用 2. 点击登录按钮 3. 选择邮箱登录 4. 输入邮箱和密码 5. 点击登录，期望结果：成功登录并进入主页，用例分级：P1，备注：测试正常登录流程
                
                测试用例2：
                - caseName: 手机号登录功能测试
                - precondition: 1. 网络连接正常 2. 用户已绑定手机号
                - testSteps: 1. 打开应用 2. 点击登录按钮 3. 选择手机号登录 4. 输入手机号 5. 获取验证码 6. 输入验证码 7. 点击登录
                - expectedResult: 成功登录并进入主页
                - priority: P1
                - remark: 测试手机号验证码登录流程
                - fullText: 用例名称：手机号登录功能测试，前置条件：1. 网络连接正常 2. 用户已绑定手机号，测试步骤：1. 打开应用 2. 点击登录按钮 3. 选择手机号登录 4. 输入手机号 5. 获取验证码 6. 输入验证码 7. 点击登录，期望结果：成功登录并进入主页，用例分级：P1，备注：测试手机号验证码登录流程
                
                ## 质量要求
                1. **完整性**：每个测试用例必须包含所有必填字段
                2. **详细性**：测试步骤和期望结果必须详细具体，可执行
                3. **准确性**：测试用例必须符合测试场景和用户需求
                4. **覆盖性**：测试用例应覆盖正常流程和异常场景
                5. **专业性**：使用专业的测试术语和描述
                
                
                请生成完整的测试用例，不要给任何的多余的信息
                不要给任何的解释或说明。
                """.formatted(
                request.getRoleDefinition(),
                String.join("\n", request.getContextInfo()),
                request.getTestScenario(),
                request.getUserScenario(),
                request.getSimilarTestCasesString(),
                request.getSirchmunkDocument(),
                request.getOutputRequirements()
        );
    }

    /**
     * 生成测试用例优化提示词
     *
     * @param testCases 测试用例列表
     * @param optimizationRequirements 优化要求
     * @return 提示词字符串
     */
    public String generateTestCaseOptimizationPrompt(List<String> testCases, String optimizationRequirements) {
        return """
                你是一名专业的测试工程师，负责优化现有的测试用例。
                
                现有测试用例：
                %s
                
                优化要求：
                %s
                
                请对以上测试用例进行优化，确保测试覆盖更全面、测试步骤更清晰、测试预期更明确。
                """.formatted(
                String.join("\n", testCases),
                optimizationRequirements
        );
    }
    
    /**
     * 生成测试用例简述
     *
     * @param request 测试用例生成请求
     * @return 提示词字符串
     */
    public String generateTestCaseSummary(TestCaseGeneratorRequest request) {
        return """
                # 测试用例简述生成任务
                
                # 禁令 
                严禁输出任何测试用例以外的内容，
                包括但不限于前置说明、思考过程、解释、结束语、结束总结、引导提问
                不要任何说明语句，仅返回用例简述
                直接输出测试用例简述即可。
                回复必须从测试用例简述开始。
                结束也必须是测试用例简述结束。
                
                ## 角色定义
                %s
                
                ## 任务目标
                请根据以下信息，生成测试用例简述，仅包含测试用例名称和预期结果。
                
                ## 输入信息
                
                ### 上下文信息
                %s
                
                ### 测试场景
                %s
                
                ### 用户场景描述
                %s
                
                ### Milvus 向量数据库相似用例
                %s
                
                ### Sirchmunk 文档
                %s
                
                ## 输出要求
                %s
                
                ## 生成格式
                请严格按照以下格式生成测试用例简述，每个简述包含以下字段：
                
                1. **caseName**：用例名称
                2. **expectedResult**：预期结果
                
                ## 格式示例
                
                测试用例1：
                - caseName: 邮箱登录功能测试
                - expectedResult: 验证用户使用正确邮箱和密码能够成功登录
                
                测试用例2：
                - caseName: 手机号登录功能测试
                - expectedResult: 验证用户使用正确手机号和验证码能够成功登录
                
                ## 质量要求
                1. **简洁性**：每个简述只包含用例名称和预期结果
                2. **明确性**：预期结果必须清晰明确，说明要预期的具体功能
                3. **覆盖性**：覆盖不同的登录方式和场景
                4. **专业性**：使用专业的测试术语和描述
                
                请生成大量测试用例简述，不要给任何多余的信息
                不要给任何的解释或说明。
                一定要完整的覆盖场景。
                """.formatted(
                request.getRoleDefinition(),
                String.join("\n", request.getContextInfo()),
                request.getTestScenario(),
                request.getUserScenario(),
                request.getSimilarTestCasesString(),
                request.getSirchmunkDocument(),
                request.getOutputRequirements()
        );
    }
}