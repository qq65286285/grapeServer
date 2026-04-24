package com.grape.grape.model.prompt;


import org.springframework.stereotype.Component;

@Component
public class PromptManager {

    /**
     * 生成测试场景描述优化的提示词
     * @param originalScenario 用户输入的原始测试场景
     * @return 生成的提示词
     */
    public String generateTestScenarioAnalysisPrompt(String originalScenario) {
        return "你是一个专业的测试分析专家，擅长解析测试场景并提取结构化信息。\n\n" +
                "请分析以下测试场景描述，提取关键信息并返回JSON格式：\n\n" +
                "【测试场景】\n" +
                originalScenario + "\n\n" +
                "【提取要求】\n" +
                "1. 严格按照JSON格式输出，不要有多余解释\n" +
                "2. 如果某个字段无法确定，使用空数组[]、空字符串\"\"或null\n" +
                "3. entities字段用于存储业务实体，采用{category, values}结构，支持任意业务场景\n" +
                "4. 功能模块支持3级层级，根据实际情况填写\n" +
                "5. 从描述中推理并补充完整的测试点\n\n" +
                "【输出格式必须严格按照以下结构，严禁修改任何属性名称：】\n" +
                "```json\n" +
                "{\n" +
                "  \"product\": {\n" +
                "    \"type\": \"\",\n" +
                "    \"name\": \"\",\n" +
                "    \"platforms\": [],\n" +
                "    \"version\": \"\"\n" +
                "  },\n" +
                "  \"function\": {\n" +
                "    \"level1\": \"\",\n" +
                "    \"level2\": \"\",\n" +
                "    \"level3\": null,\n" +
                "    \"subFunctions\": []\n" +
                "  },\n" +
                "  \"test\": {\n" +
                "    \"types\": [],\n" +
                "    \"priorities\": [],\n" +
                "    \"focuses\": [],\n" +
                "    \"testPoints\": []\n" +
                "  },\n" +
                "  \"entities\": [\n" +
                "    {\n" +
                "      \"category\": \"\",\n" +
                "      \"values\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"technical\": {\n" +
                "    \"terms\": [],\n" +
                "    \"protocols\": [],\n" +
                "    \"dependencies\": []\n" +
                "  },\n" +
                "  \"keywords\": {\n" +
                "    \"core\": [],\n" +
                "    \"business\": [],\n" +
                "    \"technical\": []\n" +
                "  },\n" +
                "  \"scenarios\": [\n" +
                "    {\n" +
                "      \"type\": \"\",\n" +
                "      \"description\": \"\",\n" +
                "      \"tags\": []\n" +
                "    }\n" +
                "  ],\n" +
                "  \"metadata\": {\n" +
                "    \"tags\": []\n" +
                "  }\n" +
                "}\n" +
                "```\n\n" +
                "【entities字段说明】\n" +
                "根据不同业务场景，category可以是：\n" +
                "- 登录场景：\"登录方式\"、\"认证方式\"、\"账号类型\"\n" +
                "- 支付场景：\"支付方式\"、\"支付渠道\"、\"支付类型\"\n" +
                "- 消息场景：\"消息类型\"、\"推送渠道\"、\"消息优先级\"\n" +
                "- 权限场景：\"权限类型\"、\"角色类型\"、\"资源类型\"\n" +
                "- 数据场景：\"数据源\"、\"同步方式\"、\"数据格式\"\n\n" +
                "【entities示例】\n" +
                "场景：\"测试Android SDK的邮箱、Google、Apple登录功能及异常场景\"\n" +
                "提取entities：\n" +
                "[\n" +
                "  {\"category\": \"登录方式\", \"values\": [\"邮箱登录\", \"Google登录\", \"Apple登录\"]},\n" +
                "  {\"category\": \"认证方式\", \"values\": [\"OAuth2.0\", \"Apple Sign In\"]},\n" +
                "  {\"category\": \"账号类型\", \"values\": [\"平台账号\", \"第三方账号\"]}\n" +
                "]\n\n" +
                "场景：\"测试支付宝和微信支付的在线支付流程\"\n" +
                "提取entities：\n" +
                "[\n" +
                "  {\"category\": \"支付方式\", \"values\": [\"支付宝\", \"微信支付\"]},\n" +
                "  {\"category\": \"支付类型\", \"values\": [\"在线支付\", \"第三方支付\"]}\n" +
                "]\n\n" +
                "【注意事项】\n" +
                "1. entities应从场景描述中识别业务实体并按维度分类，不要遗漏关键业务对象\n" +
                "2. 同一个业务对象可以属于多个category，从不同维度进行分类\n" +
                "3. subFunctions存储功能点名称，entities存储这些功能涉及的业务实体\n" +
                "4. 标准化命名：登录方式使用\"XX登录\"格式，支付方式使用\"XX支付\"格式\n" +
                "5. scenarios数组用于拆分复杂场景，如果场景简单可以为空数组\n" +
                "6. 所有数组字段都不要返回null，使用[]代替\n" +
                "7. 技术术语包括：OAuth、token、JWT、session、API、SDK等\n" +
                "8. 协议包括：HTTP、HTTPS、WebSocket、gRPC、OAuth2.0等\n" +
                "9. test.types可选值：功能测试、异常测试、性能测试、安全测试、兼容性测试、接口测试\n" +
                "10. test.focuses可选值：正常流程、异常处理、边界条件、性能、安全、数据一致性";
    }
}