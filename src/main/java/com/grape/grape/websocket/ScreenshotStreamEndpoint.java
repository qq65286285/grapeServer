package com.grape.grape.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用 ADB 截图实现屏幕镜像（简单可靠的方案）
 */
@ServerEndpoint("/screenshot/{serial}")
@Component
public class ScreenshotStreamEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotStreamEndpoint.class);
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @OnOpen
    public void onOpen(Session session, @PathParam("serial") String serial) {
        logger.info("截图流 WebSocket 连接建立，设备: {}", serial);

        // 启动截图循环
        executorService.submit(() -> {
            try {
                String adbPath = getAdbPath();
                int frameCount = 0;

                while (session.isOpen()) {
                    try {
                        // 使用 adb screencap 截图
                        ProcessBuilder pb = new ProcessBuilder(
                            adbPath, "-s", serial, "exec-out", "screencap", "-p"
                        );
                        Process process = pb.start();

                        // 读取截图数据
                        byte[] imageData = process.getInputStream().readAllBytes();
                        process.waitFor();

                        if (imageData.length > 0 && session.isOpen()) {
                            // 发送 PNG 图片数据
                            synchronized (session) {
                                session.getBasicRemote().sendBinary(ByteBuffer.wrap(imageData));
                            }
                            frameCount++;

                            if (frameCount % 30 == 0) {
                                logger.debug("已发送 {} 帧截图", frameCount);
                            }
                        }

                        // 控制帧率（约 10 FPS）
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        logger.info("截图流被中断");
                        break;
                    } catch (Exception e) {
                        logger.error("截图失败", e);
                        Thread.sleep(1000); // 出错后等待 1 秒
                    }
                }

                logger.info("截图流结束，共发送 {} 帧", frameCount);

            } catch (Exception e) {
                logger.error("截图流异常", e);
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("serial") String serial) {
        logger.info("截图流 WebSocket 连接关闭，设备: {}", serial);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("截图流 WebSocket 错误", error);
    }

    private String getAdbPath() {
        String scrcpyDir = "E:\\huanle-project\\grape-server\\grape\\src\\main\\resources\\scrcpy\\scrcpy-win64-v3.3.4";
        return scrcpyDir + "\\adb.exe";
    }
}
