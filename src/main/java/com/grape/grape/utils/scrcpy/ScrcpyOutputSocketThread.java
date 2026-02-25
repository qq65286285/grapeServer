package com.grape.grape.utils.scrcpy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.BlockingQueue;

import static com.grape.grape.utils.common.BytesTool.sendByte;

/**
 * 视频流输出线程（来自 screen-tools）
 */
public class ScrcpyOutputSocketThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(ScrcpyOutputSocketThread.class);

    public final static String ANDROID_OUTPUT_SOCKET_PRE = "android-scrcpy-output-socket-task-%s-%s-%s";

    private ScrcpyInputSocketThread scrcpyInputSocketThread;
    private Session session;
    private AndroidTestTaskBootThread androidTestTaskBootThread;

    public ScrcpyOutputSocketThread(ScrcpyInputSocketThread scrcpyInputSocketThread, Session session) {
        this.scrcpyInputSocketThread = scrcpyInputSocketThread;
        this.session = session;
        this.androidTestTaskBootThread = scrcpyInputSocketThread.getAndroidTestTaskBootThread();
        this.setDaemon(true);
        this.setName(androidTestTaskBootThread.formatThreadName(ANDROID_OUTPUT_SOCKET_PRE));
    }

    @Override
    public void run() {
        log.info("scrcpy 输出线程启动");
        int frameCount = 0;
        while (scrcpyInputSocketThread.isAlive()) {
            BlockingQueue<byte[]> dataQueue = scrcpyInputSocketThread.getDataQueue();
            byte[] buffer = new byte[0];
            try {
                buffer = dataQueue.take();
            } catch (InterruptedException e) {
                log.debug("scrcpy was interrupted", e);
                break;
            }

            frameCount++;
            if (frameCount % 60 == 0) {
                log.info("已发送 {} 帧，大小: {} bytes", frameCount, buffer.length);
            }

            sendByte(session, buffer);
        }
        log.info("scrcpy 输出线程结束，共发送 {} 帧", frameCount);
    }
}
