package com.grape.grape.service.biz;

import com.grape.grape.model.vo.TestCaseGenerateRequest;
import java.util.Map;

/**
 * AI业务服务接口
 * 定义AI相关的业务逻辑方法
 */
public interface AiBizService {

    /**
     * 调用科大讯飞Spark API
     * @param question 用户的问题
     * @return 响应结果
     */
    Map<String, Object> callSpark(String question);

    /**
     * 生成测试用例
     * @param request 测试用例生成请求参数
     * @return 生成的测试用例列表
     */
    Map<String, Object> generateTestCase(TestCaseGenerateRequest request);
}
