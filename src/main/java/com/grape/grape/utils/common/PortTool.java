package com.grape.grape.utils.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * 端口管理工具类
 */
public class PortTool {
    private static final Logger logger = LoggerFactory.getLogger(PortTool.class);
    private static final Random random = new Random();

    /**
     * 获取一个可用的端口
     *
     * @return 可用端口号
     */
    public static int getPort() {
        // 尝试在 20000-30000 范围内获取随机端口
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            int port = 20000 + random.nextInt(10000);
            if (isPortAvailable(port)) {
                logger.debug("获取可用端口: {}", port);
                return port;
            }
        }

        // 如果随机尝试失败，使用系统自动分配
        try (ServerSocket socket = new ServerSocket(0)) {
            int port = socket.getLocalPort();
            logger.debug("使用系统分配端口: {}", port);
            return port;
        } catch (IOException e) {
            logger.error("无法获取可用端口", e);
            throw new RuntimeException("无法获取可用端口", e);
        }
    }

    /**
     * 检查端口是否可用
     *
     * @param port 端口号
     * @return true 表示可用，false 表示不可用
     */
    private static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
