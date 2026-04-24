package com.grape.grape.service.biz;

public interface AIGenerateTestCaseBizService {

    /**
     * 生成测试用例
     *
     * @param testObject 测试对象（比如安卓，PC）
     * @param testSubScenario 测试子场景（Joymaker的邮箱和手机号，谷歌登录，apple登录，游客登录）
     * @param testType 测试类型（功能，异常场景）
     * @param flowType 流程类型（异常场景，正常业务）
     * @return 生成的测试用例文本
     */
    String generateTestCases(String testObject, String testSubScenario, String testType, String flowType);
}
