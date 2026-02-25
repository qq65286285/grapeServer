package com.grape.grape;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@SpringBootTest
class ScrcpyLinuxTests {

    @Test
	void 投屏测试() throws Exception {

    }



    // 日志捕获方法

    private void executeCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())))  {

            String line;
            while ((line = reader.readLine())  != null) {
                System.out.println("[CMD]  " + line); // 打印命令输出
            }
        }
        process.waitFor();
    }


    // 设备检测工具类
    public static boolean isDeviceConnected(String serial) {
        try {
            Process process = Runtime.getRuntime().exec("adb  devices");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine())  != null) {
                if (line.contains(serial))  return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
