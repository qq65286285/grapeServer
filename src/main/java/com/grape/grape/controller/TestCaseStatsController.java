package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.dashboard.TestCaseStatsVO;
import com.grape.grape.service.biz.TestCaseStatsBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 驾驶舱用例TAB控制器
 */
@RestController
@RequestMapping("/spec/testcase")
public class TestCaseStatsController {

    private static final Logger log = LoggerFactory.getLogger(TestCaseStatsController.class);

    @Resource
    private TestCaseStatsBizService testCaseStatsBizService;

    /**
     * 获取用例统计信息
     * 
     * @return Resp<TestCaseStatsVO> 用例统计信息
     */
    @GetMapping("/stats")
    public Resp getTestCaseStats() {
        log.info("获取用例统计信息");

        try {
            TestCaseStatsVO stats = testCaseStatsBizService.getTestCaseStats();
            return Resp.ok(stats);
        } catch (Exception e) {
            log.error("获取用例统计信息失败", e);
            return Resp.error();
        }
    }
}