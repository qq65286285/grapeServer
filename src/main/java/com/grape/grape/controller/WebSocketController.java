package com.grape.grape.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.grape.grape.service.ai.B_WsXModel;

/**
 * WebSocket 控制器
 * 用于向前端推送 AI 回复
 */
@Controller
@RequestMapping("/ws")
public class WebSocketController extends TextWebSocketHandler {
    // 存储所有活跃的 WebSocket 会话
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 通过 Spring 容器注入 B_WsXModel 实例
    @Autowired
    private B_WsXModel wsXModel;

    /**
     * 当 WebSocket 连接建立时调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 保存会话
        sessions.put(session.getId(), session);
        System.out.println("WebSocket 连接建立: " + session.getId());
    }

    /**
     * 当收到 WebSocket 消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("收到 WebSocket 消息: " + message.getPayload());
        try {
            // 处理前端发送的消息
            String payload = message.getPayload();
            if (payload.contains("ping") || payload.contains("heartbeat")) {
                // 响应心跳消息
                session.sendMessage(new TextMessage("{\"type\": \"pong\"}"));
                System.out.println("响应心跳消息: " + session.getId());
            } else if (payload.contains("ask_question")) {
                // 处理AI问题请求
                handleAskQuestion(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理AI问题请求
     * @param payload 消息内容
     */
    private void handleAskQuestion(String payload) {
        try {
            // 解析消息，获取问题内容
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(payload);
            String question = jsonObject.getString("question");
            
            if (question != null && !question.isEmpty()) {
                System.out.println("处理AI问题: " + question);
                
                // 创建角色内容对象
                com.grape.grape.entity.dto.RoleContent roleContent = new com.grape.grape.entity.dto.RoleContent();
                roleContent.setRole("user");
                roleContent.setContent(question);
                
                // 调用B_WsXModel处理AI请求
                wsXModel.initWebSocket(roleContent);
                
                // 发送处理中消息
                broadcast("{\"type\": \"ai_response\", \"data\": \"正在处理您的问题，请稍候...\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 发送错误消息
            broadcast("{\"type\": \"ai_error\", \"data\": \"处理问题时出错: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 当 WebSocket 连接关闭时调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除会话
        sessions.remove(session.getId());
        System.out.println("WebSocket 连接关闭: " + session.getId());
    }

    /**
     * 发送消息给所有客户端
     * @param message 消息内容
     */
    public static void broadcast(String message) {
        for (WebSocketSession session : sessions.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    System.out.println("推送消息给客户端: " + session.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息给指定客户端
     * @param sessionId 会话 ID
     * @param message 消息内容
     */
    public static void sendTo(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("推送消息给指定客户端: " + sessionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前活跃的会话数
     * @return 会话数
     */
    public static int getActiveSessionCount() {
        return sessions.size();
    }
}
