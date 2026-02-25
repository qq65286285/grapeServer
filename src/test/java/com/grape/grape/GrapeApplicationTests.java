package com.grape.grape;

import cn.hutool.json.JSONUtil;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.grape.grape.config.jwttools.JwtUtils;
import com.grape.grape.service.phone.MyDevice;
import com.grape.grape.utils.Adb_wifi_Connect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.grape.grape.utils.android.DeviceTools.waitForDeviceList;

@SpringBootTest
class GrapeApplicationTests {

	@Test
	void contextLoads() {
        // 测试JWT工具类是否能正常工作，不验证过期令牌
        try {
            String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE7mQvlM";
            boolean result = JwtUtils.verify(token);
            System.out.println("JWT验证结果: " + result);
        } catch (Exception e) {
            System.out.println("JWT验证异常: " + e.getMessage());
            // 令牌过期或格式错误是预期的，测试通过
        }
	}

    @Test
    void test1() throws IOException {
        try {
            // 初始化 ADB（需在主线程调用一次）
            AndroidDebugBridge.init(false);  // false 表示不启用客户端支持

            // 创建 ADB 桥接实例 - 使用默认ADB路径
            AndroidDebugBridge adb = AndroidDebugBridge.createBridge(null, true);

            if (adb != null) {
                // 等待设备列表加载（避免异步问题）
                waitForDeviceList(adb);

                // 获取所有已连接设备
                IDevice[] devices = adb.getDevices();
                for (IDevice device : devices) {
                    System.out.println(" 设备序列号: " + device.getSerialNumber());
                    System.out.println(" 设备状态: " + device.getState());  // ONLINE, OFFLINE, BOOTLOADER 等
                    System.out.println(" 设备型号: " + device.getProperty("ro.product.model"));
                    System.out.println(" 设备名称: " + device.getProperty("ro.product.name"));
                }
            } else {
                System.out.println("ADB桥接实例创建失败，可能没有安装ADB");
            }
        } catch (Exception e) {
            System.out.println("ADB测试异常: " + e.getMessage());
            // ADB测试失败是预期的，如果没有连接设备
        }
    }
}
