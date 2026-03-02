package com.grape.grape.config;

import com.grape.grape.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 
 * 功能说明：
 * 配置WebSocket服务，支持服务器与客户端之间的双向实时通信
 * 
 * 主要功能：
 * 1. 启用WebSocket支持
 * 2. 注册WebSocket处理器
 * 3. 配置WebSocket访问路径
 * 4. 配置跨域访问策略
 * 
 * 配置说明：
 * - WebSocket路径：/ws
 * - 支持跨域：允许所有来源（*）
 * - 处理器：WebSocketController
 * 
 * 使用场景：
 * - 实时数据推送
 * - 在线状态更新
 * - 消息通知
 * - 实时日志监控
 * - 屏幕镜像传输
 * 
 * @author grape-team
 * @since 2025-01-01
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * WebSocket控制器
     * 
     * 功能说明：
     * 处理WebSocket连接建立、消息接收和发送的控制器
     * 
     * 调用说明：
     * - 该控制器处理所有WebSocket连接
     * - 管理WebSocket会话
     * - 处理客户端发送的消息
     * - 向客户端推送消息
     * 
     * 数据库调用：
     * - 无数据库调用，直接处理WebSocket连接
     */
    @Autowired
    private WebSocketController webSocketController;

    /**
     * 注册WebSocket处理器
     * 
     * 功能说明：
     * 1. 注册WebSocket处理器到指定的URL路径
     * 2. 配置跨域访问策略
     * 3. 启用WebSocket支持
     * 
     * 业务流程：
     * - 开始：应用启动时自动执行
     * - 处理器注册：将WebSocketController注册到指定路径
     * - 跨域配置：配置允许的跨域来源
     * - 结束：完成WebSocket配置
     * 
     * 调用服务：
     * - 调用 webSocketController 处理WebSocket连接
     *   该控制器会：
     *   1. 处理连接建立（afterConnectionEstablished）
     *   2. 处理消息接收（handleTextMessage）
     *   3. 处理连接关闭（afterConnectionClosed）
     *   4. 处理连接异常（handleTransportError）
     * 
     * 调用外部服务：
     * - 调用 Spring WebSocket框架
     *   提供WebSocket协议支持
     *   管理WebSocket连接和会话
     * 
     * 数据库调用：
     * - 无数据库调用
     * 
     * 配置说明：
     * - WebSocket路径：/ws
     *   客户端连接URL：ws://localhost:8080/ws
     *   或：wss://localhost:8080/ws（使用SSL）
     * - 跨域策略：允许所有来源（*）
     *   生产环境建议配置具体的允许来源
     *   示例：.setAllowedOrigins("https://example.com")
     * 
     * 使用示例：
     * - 客户端JavaScript代码：
     *   ```javascript
     *   const socket = new WebSocket('ws://localhost:8080/ws');
     *   socket.onopen = function() {
     *       console.log('WebSocket连接已建立');
     *   };
     *   socket.onmessage = function(event) {
     *       console.log('收到消息：', event.data);
     *   };
     *   socket.onclose = function() {
     *       console.log('WebSocket连接已关闭');
     *   };
     *   ```
     * 
     * 安全注意事项：
     * - 生产环境不要使用.setAllowedOrigins("*")
     * - 建议配置具体的允许来源
     * - 使用WSS（WebSocket Secure）加密传输
     * - 实现身份验证和授权机制
     * - 限制连接频率，防止DDoS攻击
     * 
     * @param registry WebSocket处理器注册表
     *                用于注册WebSocket处理器和配置
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 方法开始：注册WebSocket处理器
        
        // 将WebSocketController注册到/ws路径
        // 允许所有跨域请求（生产环境建议限制具体来源）
        registry.addHandler(webSocketController, "/ws")
                .setAllowedOrigins("*");
        
        // 方法结束：完成WebSocket处理器注册
    }
}
