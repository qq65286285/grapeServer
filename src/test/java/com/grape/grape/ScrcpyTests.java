// package com.grape.grape;

// import com.android.ddmlib.AndroidDebugBridge;
// import com.android.ddmlib.IDevice;
// import com.grape.grape.config.jwttools.JwtUtils;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.io.*;
// import java.nio.file.Files;
// import java.nio.file.StandardCopyOption;
// import java.util.concurrent.TimeUnit;

// import static com.grape.grape.utils.android.DeviceTools.waitForDeviceList;
// import static com.mysql.cj.conf.PropertyKey.logger;

// @SpringBootTest
// class ScrcpyTests {
//     // 设备连接参数（根据实际设备修改）
//     private static final String DEVICE_IP = "172.29.131.80";
//     private static final int PORT = 5555;
//     private static final String SCRCPY_PATH = "E:\\云真机\\run_java\\bin\\Tools\\scrcpy\\scrcpy-win64-v3.3.2\\scrcpy.exe";  // Scrcpy可执行文件路径

//     @Test
// 	void 投屏测试() throws Exception {
//         // 1. 连接设备（USB或WiFi）
//         connectDevice();

//         // 2. 启动Scrcpy投屏
//         startScrcpyMirroring();

//         // 3. 保持测试运行（实际使用中通过WebSocket传输视频流）
//         Thread.sleep(30000);  // 30秒测试时间
//     }

//     private void connectDevice() throws Exception {
//         // USB转WiFi连接（首次需要USB连接）
//         executeCommand("adb tcpip " + PORT);
//         executeCommand("adb connect " + DEVICE_IP + ":" + PORT);
//         System.out.println("✅  设备连接成功: " + DEVICE_IP);
//     }

//     private void startScrcpyMirroring() throws Exception {
//         // 构建Scrcpy启动命令
//         String command = String.format(
//                 "%s --tcpip=%s:%d --video-bit-rate=8M --max-size=800",
//                 SCRCPY_PATH, DEVICE_IP, PORT
//         );
//         System.out.println("🚀  启动命令: " + command);
//         executeCommand(command);
//         System.out.println("🚀  Scrcpy投屏已启动");
//     }
//     // 推送到设备（确保 resource 文件存在）
//     public void pushScrcpyServer(String deviceSerial) throws IOException {
//         InputStream is = this.getClass().getResourceAsStream ("/scrcpy-server.jar");
//         File tempFile = File.createTempFile("scrcpy-server",  ".jar");
//         Files.copy(is,  tempFile.toPath(),  StandardCopyOption.REPLACE_EXISTING);

//         String pushCmd = String.format("adb  -s %s push %s /data/local/tmp/",
//                 deviceSerial, tempFile.getAbsolutePath());
//         Runtime.getRuntime().exec(pushCmd);
//     }
//     public void startScrcpy(String deviceSerial) {
//         try {
//             // 1. 推送服务
//             pushScrcpyServer(deviceSerial);

//             // 2. 启动命令（带参数）
//             String cmd = "adb -s " + deviceSerial + " shell CLASSPATH=/data/local/tmp/scrcpy-server.jar  " +
//                     "app_process / com.genymobile.scrcpy.Server  1.25 800 0 30 false 80 0";

//             // 3. 执行并捕获日志
//             Process process = Runtime.getRuntime().exec(cmd);
//             logStream(process.getInputStream(),  "OUTPUT");
//             logStream(process.getErrorStream(),  "ERROR");

//         } catch (IOException e) {
//             System.out.println("Scrcpy 启动失败: "+ e.getMessage());
//         }
//     }

//     // 日志捕获方法
//     private void logStream(InputStream inputStream, String type) {
//         new Thread(() -> {
//             try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
//                 String line;
//                 while ((line = br.readLine())  != null) {
//                     System.out.println("SCRCPY " + type+" "+ line); // 关键！查看错误信息
//                 }
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }).start();
//     }

//     private void executeCommand(String command) throws Exception {
//         Process process = Runtime.getRuntime().exec(command);
//         try (BufferedReader reader = new BufferedReader(
//                 new InputStreamReader(process.getInputStream())))  {

//             String line;
//             while ((line = reader.readLine())  != null) {
//                 System.out.println("[CMD]  " + line); // 打印命令输出
//             }
//         }
//         process.waitFor();
//     }

// }
