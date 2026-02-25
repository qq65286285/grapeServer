package com.grape.grape.controller;

import com.grape.grape.model.vo.TestCaseGenerateRequest;
import com.grape.grape.service.biz.AiBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AI控制器
 * 用于处理前端对AI模型的调用请求
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiBizService aiBizService;

    /**
     * 调用科大讯飞Spark API
     * @param request 请求参数，包含用户的问题
     * @return 响应结果
     */
    @PostMapping("/spark")
    public Map<String, Object> callSpark(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        return aiBizService.callSpark(question);
    }

    /**
     * 测试用例AI生成接口
     * @param request 测试用例生成请求参数
     * @return 生成的测试用例列表
     */
    @PostMapping("/testcase/generate")
    public Map<String, Object> generateTestCase(@RequestBody TestCaseGenerateRequest request) {
        return aiBizService.generateTestCase(request);
    }
}
