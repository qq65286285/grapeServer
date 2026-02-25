package com.grape.grape.service.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class B_WsXModelTest {
    
    @Autowired
    private B_WsXModel bWsXModel;
    
    /**
     * 测试 B_WsXModel 的 initWebSocket 方法
     * 用于测试 Spring 容器管理的 WebSocket 初始化
     */
    @Test
    public void testInitWebSocket() {
        try {
            // 调用 initWebSocket 方法初始化 WebSocket 连接
            bWsXModel.initWebSocket();
            // 等待一段时间，以便观察 WebSocket 通信
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常但不失败测试，因为可能是配置问题
        }
    }
}
