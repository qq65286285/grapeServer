package com.grape.grape.utils.android;

import cn.hutool.json.JSONObject;
import com.android.ddmlib.AndroidDebugBridge;

import java.io.IOException;

public class DeviceTools {
        /**
     * 等待Android调试桥获取初始设备列表
     *
     * @param bridge Android调试桥实例，用于检查设备列表状态
     */
    public static void waitForDeviceList(AndroidDebugBridge bridge) {
        int maxWait = 30; // 最大等待 3 秒 (30*100ms)
        // 循环等待直到获取到初始设备列表或超时
        while (!bridge.hasInitialDeviceList())  {
            try {
                Thread.sleep(100);
                if (--maxWait <= 0) {
                    System.out.println(" 获取设备列表超时");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(" 获取设备列表失败");
            }
        }
    }


        /**
     * 启动scrcpy服务，用于投屏和控制Android设备
     * @param deviceSerial 设备序列号，用于指定要连接的Android设备
     * @throws IOException 当执行ADB命令失败时抛出此异常
     */
    public void startScrcpy(String deviceSerial) throws IOException {
        // 推送 scrcpy-server 到手机
        Runtime.getRuntime().exec("adb  -s " + deviceSerial +
                " push scrcpy-server.jar  /data/local/tmp/");

        // 启动服务端进程
        String cmd = "adb -s " + deviceSerial +
                " shell CLASSPATH=" +
//                "/data/local/tmp/scrcpy-server.jar" +
                "E:\\grape\\grape\\src\\main\\resources\\scrcpy-server.jar" +
                "  app_process / com.genymobile.scrcpy.Server  " +
                "1.25 800 0 30 false 80 0";
        Process process = Runtime.getRuntime().exec(cmd);
    }


        /**
     * 处理控制命令，根据命令类型执行相应的ADB操作
     *
     * @param serial 设备序列号，用于指定要操作的Android设备
     * @param command 控制命令对象，包含命令类型和相关参数
     * @throws IOException 当执行ADB命令时发生IO异常时抛出
     */
    public void handleControlCommand(String serial, JSONObject command) throws IOException {
        String type = command.getStr("type");
        // 处理点击命令
        if ("tap".equals(type)) {
            String cmd = "adb -s " + serial + " input tap " +
                    command.get("x")  + " " + command.get("y");
            Runtime.getRuntime().exec(cmd);
        }
    }

}
