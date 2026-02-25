package com.grape.grape.utils.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * WebSocket 消息发送工具类
 */
public class BytesTool {
    private static final Logger logger = LoggerFactory.getLogger(BytesTool.class);

    // WebSocket 超时时间（毫秒），默认 480 秒
    public static int remoteTimeout = 480000;

    /**
     * 发送二进制消息到 WebSocket
     *
     * @param session WebSocket 会话
     * @param data    二进制数据
     */
    public static void sendByte(Session session, byte[] data) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            synchronized (session) {
                if (session.isOpen()) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
                    session.getBasicRemote().sendBinary(byteBuffer);
                }
            }
        } catch (IOException e) {
            logger.error("发送二进制消息失败: {}", e.getMessage());
        }
    }

    /**
     * 发送文本消息到 WebSocket
     *
     * @param session WebSocket 会话
     * @param text    文本消息
     */
    public static void sendText(Session session, String text) {
        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            synchronized (session) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(text);
                }
            }
        } catch (IOException e) {
            logger.error("发送文本消息失败: {}", e.getMessage());
        }
    }
}
