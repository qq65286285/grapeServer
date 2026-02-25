package com.grape.grape.service.phone;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class MyDevice {
    /*
     * 获取当前所连接设备的信息
     */
    public IDevice[] iDevice() {
        //初始化ddmlib
        AndroidDebugBridge.init(false);
        //创建debug bridge
        AndroidDebugBridge adb = AndroidDebugBridge.createBridge();//等待获取到设备
//		AndroidDebugBridge adb = AndroidDebugBridge.createBridge(
//				"F:\\Android\\android-sdk\\platform-tools\\adb.exe",false);

        waitForDevice(adb);

        return adb.getDevices();
    }
    /*
     * 设置等待时间，直到获取到设备信息。
     * 等待超过0.3秒，抛出异常
     */
    private static void waitForDevice(AndroidDebugBridge bridge) {
        int count = 0;
        while(!bridge.hasInitialDeviceList()) {
            try {
                Thread.sleep(100);
                count++;
            } catch(InterruptedException ignored) {
            }
            if(count>300) {
                System.out.println("Time out");
                break;
            }
        }
    }
}
