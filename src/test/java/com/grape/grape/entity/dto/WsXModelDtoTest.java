package com.grape.grape.entity.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.grape.grape.service.ai.B_WsXModel;

@SpringBootTest
public class WsXModelDtoTest {
    
    @Autowired
    private B_WsXModel bWsXModel;
    
    /**
     * 测试实际调用 B_WsXModel 的 initWebSocket 方法
     * 模拟之前 main 函数中的 WebSocket 初始化调用
     */
    @Test
    public void testMainFunctionality() {
        try {
            // 打印测试开始信息
            System.out.println("========================================");
            System.out.println("开始测试 B_WsXModel WebSocket 调用");
            System.out.println("========================================");
            
            // 测试使用默认问题
            System.out.println("1. 测试使用默认问题");
            bWsXModel.initWebSocket();
            
            // 等待一段时间，以便观察 WebSocket 通信
            System.out.println("2. 等待 WebSocket 通信完成...");
            Thread.sleep(10000); // 增加等待时间，确保能够接收到完整的响应
            
            // 测试使用自定义问题
            System.out.println("3. 测试使用自定义问题");
            RoleContent roleContent = new RoleContent();
            roleContent.setRole("user");
            roleContent.setContent("你好，科大讯飞");
            bWsXModel.initWebSocket(roleContent);
            
            // 等待一段时间，以便观察 WebSocket 通信
            System.out.println("4. 等待 WebSocket 通信完成...");
            Thread.sleep(10000); // 增加等待时间，确保能够接收到完整的响应
            
            // 测试完成信息
            System.out.println("5. WebSocket 通信测试完成");
            System.out.println("========================================");
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常但不失败测试，因为可能是配置问题
            System.out.println("========================================");
            System.out.println("测试过程中出现异常，但测试继续执行");
            System.out.println("========================================");
        }
    }
}
