package com.grape.grape.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.ddmlib.IDevice;
import com.grape.grape.utils.android.AndroidDeviceBridgeTool;
import com.grape.grape.utils.common.BytesTool;
import com.grape.grape.utils.common.ScreenMap;
import com.grape.grape.utils.scrcpy.ScrcpyServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 屏幕投屏 WebSocket 端点（基于 screen-tools 简化版）
 */
@ServerEndpoint("/screen/{serial}")
@Component
public class ScreenStreamEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ScreenStreamEndpoint.class);

    private static final Map<String, String> typeMap = new ConcurrentHashMap<>();
    private static final Map<String, String> picMap = new ConcurrentHashMap<>();
    private static final Map<String, Integer> rotationMap = new ConcurrentHashMap<>();
    // 存储每个会话的上一次触摸坐标（用于滑动）
    private static final Map<String, int[]> lastTouchMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("serial") String serial) {
        logger.info("=== WebSocket 连接建立 ===");
        logger.info("设备序列号: {}", serial);
        logger.info("Session ID: {}", session.getId());

        // 初始化 AndroidDebugBridge
        AndroidDeviceBridgeTool.init();

        // 获取设备
        IDevice iDevice = AndroidDeviceBridgeTool.getIDeviceByUdId(serial);
        if (iDevice == null) {
            logger.error("设备未连接: {}", serial);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "设备未连接"));
            } catch (IOException e) {
                logger.error("关闭 session 失败", e);
            }
            return;
        }

        // 中止之前的投屏
        AndroidDeviceBridgeTool.screen(iDevice, "abort");

        // 存储 session 信息
        session.getUserProperties().put("serial", serial);
        session.getUserProperties().put("iDevice", iDevice);

        // 默认使用 scrcpy 模式
        typeMap.putIfAbsent(serial, "scrcpy");
        rotationMap.putIfAbsent(serial, -1);

        logger.info("WebSocket 连接就绪，等待客户端发送 switch 消息");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject msg = JSON.parseObject(message);
            logger.info("收到消息: {}", msg);

            String serial = (String) session.getUserProperties().get("serial");
            String type = msg.getString("type");

            switch (type) {
                case "switch":
                    String mode = msg.getString("detail");
                    typeMap.put(serial, mode);
                    logger.info("切换投屏模式: {} -> {}", serial, mode);
                    startScreen(session);
                    break;

                case "pic":
                    String quality = msg.getString("detail");
                    picMap.put(serial, quality);
                    logger.info("调整画质: {} -> {}", serial, quality);
                    startScreen(session);
                    break;

                case "touch":
                    // 触摸事件：down x y, move x y, up x y
                    handleTouchEvent(session, msg.getString("detail"));
                    break;

                default:
                    logger.warn("未知消息类型: {}", type);
            }
        } catch (Exception e) {
            logger.error("处理消息异常", e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String serial = (String) session.getUserProperties().get("serial");
        logger.info("=== WebSocket 连接关闭 ===");
        logger.info("设备序列号: {}", serial);
        logger.info("Session ID: {}", session.getId());
        logger.info("关闭原因: {} - {}", reason.getCloseCode(), reason.getReasonPhrase());
        exit(session, serial);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("=== WebSocket 发生错误 ===");
        logger.error("Session ID: {}", session.getId());
        logger.error("错误信息: {}", error.getMessage(), error);

        String serial = (String) session.getUserProperties().get("serial");
        if (serial != null) {
            exit(session, serial);
        }
    }

    /**
     * 启动投屏（来自 screen-tools）
     */
    private void startScreen(Session session) {
        IDevice iDevice = (IDevice) session.getUserProperties().get("iDevice");
        String serial = (String) session.getUserProperties().get("serial");

        if (iDevice == null) {
            logger.error("设备信息丢失: {}", serial);
            return;
        }

        // 停止旧的投屏线程
        Thread old = ScreenMap.getMap().get(session);
        if (old != null) {
            old.interrupt();
            // 等待线程结束
            int waitCount = 0;
            while (ScreenMap.getMap().get(session) != null && waitCount < 10) {
                try {
                    Thread.sleep(100);
                    waitCount++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        typeMap.putIfAbsent(iDevice.getSerialNumber(), "scrcpy");
        String mode = typeMap.get(iDevice.getSerialNumber());
        logger.info("启动投屏，设备: {}, 模式: {}", serial, mode);

        switch (mode) {
            case "scrcpy":
                ScrcpyServerUtil scrcpyServerUtil = new ScrcpyServerUtil();
                int rotation = rotationMap.getOrDefault(iDevice.getSerialNumber(), -1);
                Thread scrcpyThread = scrcpyServerUtil.start(iDevice.getSerialNumber(), rotation, session);
                if (scrcpyThread != null) {
                    ScreenMap.getMap().put(session, scrcpyThread);
                }
                break;

            case "minicap":
                logger.warn("Minicap 模式暂未实现");
                break;

            default:
                logger.warn("未知投屏模式: {}", mode);
                return;
        }

        // 发送投屏就绪消息
        JSONObject picFinish = new JSONObject();
        picFinish.put("msg", "picFinish");
        BytesTool.sendText(session, picFinish.toJSONString());
        logger.info("投屏启动完成");
    }

    /**
     * 处理触摸事件（优化：异步执行，不阻塞视频流）
     */
    private void handleTouchEvent(Session session, String detail) {
        IDevice iDevice = (IDevice) session.getUserProperties().get("iDevice");
        String serial = (String) session.getUserProperties().get("serial");
        if (iDevice == null || serial == null) {
            return;
        }

        try {
            // detail 格式：down x y\n, move x y\n, up x y\n
            String[] parts = detail.trim().split("\\s+");
            if (parts.length < 3) {
                return;
            }

            String action = parts[0];  // down, move, up
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            String sessionId = session.getId();

            switch (action) {
                case "down":
                    // 只记录按下位置，不执行命令
                    lastTouchMap.put(sessionId, new int[]{x, y});
                    logger.debug("触摸按下: ({}, {})", x, y);
                    break;

                case "move":
                    // 只更新位置，不执行命令（避免大量 ADB 调用阻塞）
                    lastTouchMap.put(sessionId, new int[]{x, y});
                    break;

                case "up":
                    // 抬起时才执行命令（异步，不阻塞）
                    int[] downPos = lastTouchMap.get(sessionId);
                    if (downPos != null) {
                        final int startX = downPos[0];
                        final int startY = downPos[1];
                        final int endX = x;
                        final int endY = y;
                        final IDevice device = iDevice;

                        // 异步执行 ADB 命令，不阻塞视频流
                        new Thread(() -> {
                            try {
                                int distance = (int) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                                String command;

                                if (distance < 15) {
                                    // 点击（距离小，识别为点击）
                                    command = String.format("input tap %d %d", endX, endY);
                                    logger.info("执行点击: ({}, {})", endX, endY);
                                } else {
                                    // 滑动（快速滑动，50ms 完成）
                                    command = String.format("input swipe %d %d %d %d 50", startX, startY, endX, endY);
                                    logger.info("执行滑动: ({},{}) -> ({},{})", startX, startY, endX, endY);
                                }

                                // 使用带超时的触摸命令方法
                                AndroidDeviceBridgeTool.executeTouchCommand(device, command);
                            } catch (Exception e) {
                                logger.error("执行触摸命令异常", e);
                            }
                        }, "touch-handler").start();

                        lastTouchMap.remove(sessionId);
                    }
                    break;

                default:
                    return;
            }

        } catch (Exception e) {
            logger.error("处理触摸事件异常", e);
        }
    }

    /**
     * 清理资源
     */
    private void exit(Session session, String serial) {
        if (session == null) {
            return;
        }

        synchronized (session) {
            // 停止投屏线程
            Thread screenThread = ScreenMap.getMap().get(session);
            if (screenThread != null && screenThread.isAlive()) {
                screenThread.interrupt();
                logger.info("停止投屏线程: {}", serial);
            }
            ScreenMap.getMap().remove(session);

            // 清理映射
            if (serial != null) {
                typeMap.remove(serial);
                picMap.remove(serial);
                rotationMap.remove(serial);
            }
            lastTouchMap.remove(session.getId());

            // 关闭 session
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException e) {
                    logger.error("关闭 session 失败", e);
                }
            }

            logger.info("投屏资源清理完成: {}", serial);
        }
    }
}
