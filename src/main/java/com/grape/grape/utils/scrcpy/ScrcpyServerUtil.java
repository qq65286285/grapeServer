package com.grape.grape.utils.scrcpy;

import com.android.ddmlib.IDevice;
import com.grape.grape.utils.android.AndroidDeviceBridgeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Scrcpy 服务工具类（基于 screen-tools 简化版）
 */
public class ScrcpyServerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScrcpyServerUtil.class);

    // 是否使用 ADB screenrecord 作为备选方案（支持 Android 15）
    private static final boolean USE_ADB_SCREENRECORD = true;

    public Thread start(String udId, int tor, Session session) {
        return start(udId, tor, session, new AndroidTestTaskBootThread().setUdId(udId));
    }

    public Thread start(String udId, int tor, Session session, AndroidTestTaskBootThread androidTestTaskBootThread) {
        IDevice iDevice = AndroidDeviceBridgeTool.getIDeviceByUdId(udId);
        if (iDevice == null) {
            logger.error("设备 {} 未连接", udId);
            return null;
        }

        String key = androidTestTaskBootThread.formatThreadName(AndroidTestTaskBootThread.ANDROID_TEST_TASK_BOOT_PRE);

        // 优先使用 ADB screenrecord（支持 Android 15）
        if (USE_ADB_SCREENRECORD) {
            logger.info("使用 ADB screenrecord 模式（支持 Android 15）");
            AdbScreenRecordThread recordThread = new AdbScreenRecordThread(iDevice, session);
            TaskManager.startChildThread(key, recordThread);
            logger.info("ADB screenrecord 启动完成，设备: {}", udId);
            return recordThread;
        }

        // 原来的 scrcpy-server 方式（不支持 Android 15）
        int s;
        if (tor == -1) {
            s = AndroidDeviceBridgeTool.getScreen(iDevice);
        } else {
            s = tor;
        }

        // 启动 scrcpy 服务
        ScrcpyLocalThread scrcpyThread = new ScrcpyLocalThread(iDevice, s, session, androidTestTaskBootThread);
        TaskManager.startChildThread(key, scrcpyThread);

        // 等待启动
        int wait = 0;
        while (!scrcpyThread.getIsFinish().tryAcquire()) {
            wait++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error("等待 scrcpy 启动被中断", e);
                Thread.currentThread().interrupt();
            }
            // 启动失败了，强行跳过
            if (wait > 8) {
                logger.warn("scrcpy 启动超时");
                break;
            }
        }

        // 启动输入流
        ScrcpyInputSocketThread scrcpyInputSocketThread = new ScrcpyInputSocketThread(
                iDevice, new LinkedBlockingQueue<>(), scrcpyThread, session);

        // 启动输出流
        ScrcpyOutputSocketThread scrcpyOutputSocketThread = new ScrcpyOutputSocketThread(
                scrcpyInputSocketThread, session);

        TaskManager.startChildThread(key, scrcpyInputSocketThread, scrcpyOutputSocketThread);

        logger.info("Scrcpy 服务启动完成，设备: {}", udId);
        return scrcpyThread; // server 线程
    }
}
