package com.grape.grape.service.biz;

import com.grape.grape.model.vo.dashboard.TestCaseStatsVO;

/**
 * 驾驶舱用例TAB业务服务接口
 */
public interface TestCaseStatsBizService {

    /**
     * 获取用例统计信息
     * 
     * @return 用例统计VO，包含总数、本周新增、趋势、分布等
     */
    TestCaseStatsVO getTestCaseStats();
}