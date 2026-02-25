package com.grape.grape.utils.android;

import com.android.ddmlib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Android ADB 设备工具类
 * 移植自 Sonic 项目，用于管理 Android 设备连接和操作
 */
public class AndroidDeviceBridgeTool {
    private static final Logger logger = LoggerFactory.getLogger(AndroidDeviceBridgeTool.class);

    private static AndroidDebugBridge androidDebugBridge = null;
    private static final Map<String, Integer> forwardPortMap = new ConcurrentHashMap<>();

    /**
     * 初始化 Android Debug Bridge
     */
    public static void init() {
        if (androidDebugBridge != null) {
            return;
        }

        AndroidDebugBridge.initIfNeeded(false);
        String adbPath = getADBPathFromSystemEnv();
        if (adbPath == null) {
            logger.error("未找到 ADB 路径，请设置 ANDROID_HOME 环境变量");
            return;
        }

        androidDebugBridge = AndroidDebugBridge.createBridge(adbPath, false);
        if (androidDebugBridge == null) {
            logger.error("无法创建 Android Debug Bridge");
            return;
        }

        // 等待设备连接
        int count = 0;
        while (!androidDebugBridge.hasInitialDeviceList() && count < 50) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Android Debug Bridge 初始化完成");
    }

    /**
     * 获取系统 ADB 路径
     */
    private static String getADBPathFromSystemEnv() {
        // 尝试从 resource 目录获取 adb
        String resourceAdbPath = getResourceADBPath();
        if (resourceAdbPath != null) {
            logger.info("从 resource 目录找到 ADB: {}", resourceAdbPath);
            return resourceAdbPath;
        }
        
        String path = System.getenv("ANDROID_HOME");
        if (path != null) {
            path += File.separator + "platform-tools" + File.separator + "adb";
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                path += ".exe";
            }
            if (new File(path).exists()) {
                logger.info("找到 ADB 路径: {}", path);
                return path;
            }
        }

        // 尝试使用系统 PATH 中的 adb
        String adbCommand = System.getProperty("os.name").toLowerCase().contains("win") ? "adb.exe" : "adb";
        File adbFile = new File(adbCommand);
        if (adbFile.exists()) {
            return adbCommand;
        }

        // 尝试常见路径
        String[] commonPaths = {
            "/usr/bin/adb",
            "/usr/local/bin/adb",
            "C:\\Android\\sdk\\platform-tools\\adb.exe",
            "C:\\Program Files\\Android\\sdk\\platform-tools\\adb.exe"
        };

        for (String commonPath : commonPaths) {
            if (new File(commonPath).exists()) {
                logger.info("在常见路径找到 ADB: {}", commonPath);
                return commonPath;
            }
        }

        logger.warn("未找到 ADB，请安装 Android SDK 并设置 ANDROID_HOME 环境变量");
        return null;
    }
    
    /**
     * 从 resource 目录获取 adb 路径
     */
    private static String getResourceADBPath() {
        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            // 构建 adb 路径
            String adbName = System.getProperty("os.name").toLowerCase().contains("win") ? "adb.exe" : "adb";
            String scrcpyDir = System.getProperty("os.name").toLowerCase().contains("win") ? "scrcpy-win64-v3.3.4" : "scrcpy-linux-x86_64-v3.3.4";
            
            // 尝试从 src/main/resources/scrcpy 目录获取
            String adbPath = projectRoot + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "scrcpy" + File.separator + scrcpyDir + File.separator + adbName;
            
            File adbFile = new File(adbPath);
            if (adbFile.exists()) {
                return adbPath;
            }
            
            // 尝试从 target/classes/scrcpy 目录获取
            adbPath = projectRoot + File.separator + "target" + File.separator + "classes" + File.separator + "scrcpy" + File.separator + scrcpyDir + File.separator + adbName;
            adbFile = new File(adbPath);
            if (adbFile.exists()) {
                return adbPath;
            }
            
        } catch (Exception e) {
            logger.error("获取 resource 目录 ADB 路径失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 获取所有在线设备
     */
    public static IDevice[] getRealOnLineDevices() {
        if (androidDebugBridge != null) {
            return androidDebugBridge.getDevices();
        } else {
            return null;
        }
    }

    /**
     * 根据序列号获取设备
     */
    public static IDevice getIDeviceByUdId(String udId) {
        IDevice[] iDevices = getRealOnLineDevices();
        if (iDevices == null || iDevices.length == 0) {
            return null;
        }

        for (IDevice device : iDevices) {
            if (device.getState().equals(IDevice.DeviceState.ONLINE)
                    && device.getSerialNumber().equals(udId)) {
                return device;
            }
        }

        logger.info("设备「{}」未连接", udId);
        return null;
    }

    /**
     * 获取屏幕大小
     */
    public static String getScreenSize(IDevice iDevice) {
        String size = "";
        try {
            size = executeCommand(iDevice, "wm size");
            if (size.contains("Override size")) {
                size = size.substring(size.indexOf("Override size"));
            } else {
                size = size.split(":")[1];
            }
            size = size.trim()
                    .replace(":", "")
                    .replace("Override size", "")
                    .replace("\r", "")
                    .replace("\n", "")
                    .replace(" ", "");
            if (size.length() > 20) {
                size = "unknown";
            }
        } catch (Exception e) {
            logger.info("获取屏幕大小失败: {}", e.getMessage());
        }
        return size;
    }

    /**
     * 获取屏幕旋转角度
     */
    public static int getScreen(IDevice iDevice) {
        try {
            return Integer.parseInt(executeCommand(iDevice, "settings get system user_rotation")
                    .trim().replaceAll("\n", "")
                    .replace("\t", ""));
        } catch (Exception e) {
            logger.error("获取屏幕旋转失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 执行 shell 命令
     */
    public static String executeCommand(IDevice iDevice, String command) {
        CollectingOutputReceiver output = new CollectingOutputReceiver();
        try {
            iDevice.executeShellCommand(command, output, 0, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.info("向设备 {} 发送命令 {} 失败", iDevice.getSerialNumber(), command);
            logger.error(e.getMessage());
        }
        return output.getOutput();
    }

    /**
     * 执行触摸命令（等待执行完成，确保触摸生效）
     */
    public static void executeTouchCommand(IDevice iDevice, String command) {
        CollectingOutputReceiver output = new CollectingOutputReceiver();
        try {
            logger.info("执行触摸命令: {} -> {}", iDevice.getSerialNumber(), command);
            // 等待最多 2 秒让命令执行完成
            iDevice.executeShellCommand(command, output, 2, TimeUnit.SECONDS);
            String result = output.getOutput();
            if (result != null && !result.trim().isEmpty()) {
                logger.info("触摸命令输出: {}", result);
            }
        } catch (Exception e) {
            logger.error("执行触摸命令失败: {} - {}", command, e.getMessage());
        }
    }

    /**
     * 端口转发
     */
    public static void forward(IDevice iDevice, int port, String service) {
        String name = String.format("process-%s-forward-%s", iDevice.getSerialNumber(), service);
        Integer oldP = forwardPortMap.get(name);
        if (oldP != null) {
            removeForward(iDevice, oldP, service);
        }
        try {
            logger.info("{} 设备端口 {} 转发到本地端口 {}", iDevice.getSerialNumber(), service, port);
            iDevice.createForward(port, service, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            forwardPortMap.put(name, port);
        } catch (Exception e) {
            logger.error("端口转发失败: {}", e.getMessage());
        }
    }

    /**
     * 移除端口转发
     */
    public static void removeForward(IDevice iDevice, int port, String serviceName) {
        try {
            logger.info("取消 {} 设备端口 {} 转发到本地端口 {}", iDevice.getSerialNumber(), serviceName, port);
            // 使用正确的方法重载，传入端口和服务名
            iDevice.removeForward(port, serviceName, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
            String name = String.format("process-%s-forward-%s", iDevice.getSerialNumber(), serviceName);
            forwardPortMap.remove(name);
        } catch (Exception e) {
            logger.error("移除端口转发失败: {}", e.getMessage());
        }
    }

    /**
     * 控制屏幕（用于中止投屏）
     */
    public static void screen(IDevice iDevice, String type) {
        if ("abort".equals(type)) {
            // 这里可以添加停止投屏的逻辑
            logger.info("中止设备 {} 的投屏", iDevice.getSerialNumber());
        }
    }
}
