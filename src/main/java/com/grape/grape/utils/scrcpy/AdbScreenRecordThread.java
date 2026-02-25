package com.grape.grape.utils.scrcpy;

import com.alibaba.fastjson.JSONObject;
import com.android.ddmlib.IDevice;
import com.grape.grape.utils.android.AndroidDeviceBridgeTool;
import com.grape.grape.utils.common.BytesTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 使用 ADB Screenrecord 进行投屏（支持所有 Android 版本）
 */
public class AdbScreenRecordThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(AdbScreenRecordThread.class);

    private final IDevice iDevice;
    private final Session session;
    private final String serial;
    private final BlockingQueue<byte[]> dataQueue = new LinkedBlockingQueue<>();
    private Process recordProcess;
    private volatile boolean running = true;

    // 可配置参数（可从配置文件读取）
    private String videoSize = "720x1280";      // 视频分辨率
    private int bitRate = 6000000;              // 比特率 6Mbps（USB连接可以更高）
    private int bufferSize = 4096;              // 缓冲区大小 4KB（降低延迟）

    public AdbScreenRecordThread(IDevice iDevice, Session session) {
        this.iDevice = iDevice;
        this.session = session;
        this.serial = iDevice.getSerialNumber();
        this.setDaemon(false);
        this.setName("adb-record-" + serial);
    }

    // 设置视频参数的构造函数
    public AdbScreenRecordThread(IDevice iDevice, Session session, String videoSize, int bitRate, int bufferSize) {
        this(iDevice, session);
        this.videoSize = videoSize;
        this.bitRate = bitRate;
        this.bufferSize = bufferSize;
    }

    public BlockingQueue<byte[]> getDataQueue() {
        return dataQueue;
    }

    @Override
    public void run() {
        try {
            // 发送屏幕尺寸
            String sizeTotal = AndroidDeviceBridgeTool.getScreenSize(iDevice);
            JSONObject size = new JSONObject();
            size.put("msg", "size");
            size.put("width", sizeTotal.split("x")[0]);
            size.put("height", sizeTotal.split("x")[1]);
            BytesTool.sendText(session, size.toJSONString());
            logger.info("发送屏幕尺寸: {}", sizeTotal);

            // 使用 adb exec-out screenrecord 直接输出到 stdout
            String adbPath = getAdbPath();
            if (adbPath == null) {
                logger.error("未找到 ADB");
                return;
            }

            // 构建命令：adb exec-out screenrecord
            String[] command = {
                    adbPath,
                    "-s", serial,
                    "exec-out",
                    "screenrecord",
                    "--output-format=h264",
                    "--size", videoSize,
                    "--bit-rate", String.valueOf(bitRate),
                    "-"  // 输出到 stdout
            };

            logger.info("启动 screenrecord: {}", String.join(" ", command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(false);
            recordProcess = pb.start();

            // 读取错误输出
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(recordProcess.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("screenrecord stderr: {}", line);
                    }
                } catch (IOException e) {
                    logger.error("读取错误输出异常", e);
                }
            }).start();

            // 读取视频流并发送
            try (InputStream inputStream = recordProcess.getInputStream()) {
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                int frameCount = 0;

                logger.info("开始读取 screenrecord 视频流（分辨率: {}, 比特率: {}, 缓冲: {}）",
                    videoSize, bitRate, bufferSize);

                while (running && (bytesRead = inputStream.read(buffer)) > 0) {
                    byte[] data = new byte[bytesRead];
                    System.arraycopy(buffer, 0, data, 0, bytesRead);

                    // 直接发送数据
                    BytesTool.sendByte(session, data);

                    frameCount++;
                    if (frameCount % 100 == 0) {
                        logger.info("已发送 {} 个数据包", frameCount);
                    }
                }

                logger.info("screenrecord 读取结束，共 {} 个数据包", frameCount);
            }

        } catch (Exception e) {
            logger.error("screenrecord 异常", e);
        } finally {
            cleanup();
        }
    }

    /**
     * 获取 ADB 路径（跨平台）
     */
    private String getAdbPath() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        String adbExecutable = isWindows ? "adb.exe" : "adb";

        // 1. 优先使用项目自带的 adb（按 OS 类型优先查找对应平台的版本）
        String basePath = System.getProperty("user.dir");
        String[] projectPaths;
        if (isWindows) {
            // Windows 优先查找 Windows 版本
            projectPaths = new String[] {
                    basePath + "/grape/src/main/resources/scrcpy/scrcpy-win64-v3.3.4/adb.exe",
                    basePath + "/src/main/resources/scrcpy/scrcpy-win64-v3.3.4/adb.exe",
                    basePath + "/grape/src/main/resources/scrcpy/scrcpy-linux-x86_64-v3.3.4/adb",
                    basePath + "/src/main/resources/scrcpy/scrcpy-linux-x86_64-v3.3.4/adb"
            };
        } else {
            // Linux/Mac 优先查找 Linux 版本
            projectPaths = new String[] {
                    basePath + "/grape/src/main/resources/scrcpy/scrcpy-linux-x86_64-v3.3.4/adb",
                    basePath + "/src/main/resources/scrcpy/scrcpy-linux-x86_64-v3.3.4/adb",
                    basePath + "/grape/src/main/resources/scrcpy/scrcpy-win64-v3.3.4/adb.exe",
                    basePath + "/src/main/resources/scrcpy/scrcpy-win64-v3.3.4/adb.exe"
            };
        }

        for (String path : projectPaths) {
            File adbFile = new File(path);
            if (adbFile.exists()) {
                logger.info("使用项目 ADB: {}", adbFile.getAbsolutePath());
                return adbFile.getAbsolutePath();
            }
        }

        // 2. 尝试 ANDROID_HOME 环境变量
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null) {
            String adb = androidHome + File.separator + "platform-tools" + File.separator + adbExecutable;
            if (new File(adb).exists()) {
                logger.info("使用 ANDROID_HOME ADB: {}", adb);
                return adb;
            }
        }

        // 3. 尝试系统 PATH（按 OS 类型）
        String[] systemPaths;
        if (isWindows) {
            // Windows 系统路径
            systemPaths = new String[] {
                    "C:\\Android\\sdk\\platform-tools\\adb.exe",
                    "adb.exe"  // 尝试直接调用（如果在 PATH 中）
            };
        } else {
            // Linux/Mac 系统路径
            systemPaths = new String[] {
                    "/usr/bin/adb",
                    "/usr/local/bin/adb",
                    "adb"  // 尝试直接调用（如果在 PATH 中）
            };
        }

        for (String path : systemPaths) {
            File adbFile = new File(path);
            if (adbFile.exists()) {
                logger.info("使用系统 ADB: {}", path);
                return path;
            }
        }

        // 4. 最后尝试直接使用命令名（依赖系统 PATH）
        logger.warn("未找到 ADB 文件，尝试使用系统 PATH 中的 adb 命令");
        return adbExecutable;
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        running = false;
        cleanup();
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        if (recordProcess != null && recordProcess.isAlive()) {
            recordProcess.destroy();
            try {
                recordProcess.waitFor(3, java.util.concurrent.TimeUnit.SECONDS);
                if (recordProcess.isAlive()) {
                    recordProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("screenrecord 进程已终止");
        }
    }
}
