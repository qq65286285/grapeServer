package com.grape.grape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 控制器
 * 用于处理WebSocket连接
 */
@Controller
@RequestMapping("/ws")
public class WebSocketController extends TextWebSocketHandler {
    // 存储所有活跃的 WebSocket 会话
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

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
            } else if (payload.contains("ai_chat") || payload.contains("test_case_generator") || payload.contains("ask_question")) {
                // 处理AI问题请求（AI功能已移除）
                handleAskQuestion(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理AI问题请求（AI功能已移除）
     * @param session WebSocket会话
     */
    private void handleAskQuestion(WebSocketSession session) {
        try {
            // 发送AI功能已移除的消息
            session.sendMessage(new TextMessage("{\"service\": \"test_case_generator\", \"type\": \"ai_error\", \"data\": \"AI功能已移除\"}"));
            System.out.println("发送AI功能已移除消息: " + session.getId());
        } catch (Exception e) {
            e.printStackTrace();
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
