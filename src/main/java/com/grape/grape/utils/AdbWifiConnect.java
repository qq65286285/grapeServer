package com.grape.grape.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Tools.DataAndLog;

import static com.grape.grape.utils.Tools.method.dealSpace;

/**
 * ADB WiFi连接工具类
 * 负责Android设备的ADB WiFi连接管理和设备信息采集
 * 
 * 主要功能：
 * 1. 执行ADB命令连接Android设备
 * 2. 通过WiFi连接设备（替代USB连接）
 * 3. 采集设备信息（分辨率、品牌、型号、Android版本等）
 * 4. 将设备信息同步到数据库
 * 5. 更新设备的在线状态
 * 
 * @author grape-team
 * @since 2025-01-01
 */
public class AdbWifiConnect {

    /**
     * 日志记录器
     * 使用Log4j2记录工具类的执行日志和异常信息
     */
    private static final Logger logger = LogManager.getLogger(AdbWifiConnect.class);

    /**
     * ADB命令数组
     * 定义了连接设备所需执行的一系列ADB命令
     * 命令1: 获取设备的WiFi IP地址
     * 命令2: 设置ADB监听端口为5555
     * 命令3: 连接到指定IP地址的设备
     */
    private static final String[] ADB_COMMANDS = {
        " shell ip -f inet addr show wlan0",
        " tcpip 5555",
        " connect"
    };

    /**
     * 默认数据库连接URL
     * 当配置文件加载失败时使用的默认数据库连接地址
     */
    private static final String DEFAULT_DB_URL = "jdbc:mysql://192.168.22.140:3306/grape?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8";

    /**
     * 默认数据库用户名
     * 当配置文件加载失败时使用的默认数据库用户名
     */
    private static final String DEFAULT_DB_USER = "root";

    /**
     * 默认数据库密码
     * 当配置文件加载失败时使用的默认数据库密码
     */
    private static final String DEFAULT_DB_PASSWORD = "root";

    /**
     * ADB默认端口
     * WiFi连接使用的默认端口号
     */
    private static final String ADB_PORT = "5555";

    /**
     * 连接延迟时间（毫秒）
     * 在ADB连接设置成功后，等待设备连接完成的延迟时间
     */
    private static final int CONNECT_DELAY_MS = 2000;

    /**
     * 已连接设备数组
     * 存储当前通过ADB连接的所有设备ID（包括USB和WiFi连接）
     */
    private static String[] devices = {};

    /**
     * 当前设备的IP地址
     * 存储最近一次获取到的设备WiFi IP地址
     */
    private static String ipAddress;

    /**
     * 当前执行的ADB命令
     * 存储当前正在执行的ADB命令字符串
     */
    private static String command;

    /**
     * 设备信息JSON对象
     * 存储采集到的设备详细信息，用于后续数据库写入
     */
    private static JSONObject jsonObject = new JSONObject();

    /**
     * 数据库连接URL
     * 从配置文件中读取的数据库连接地址
     */
    private static String dbUrl;

    /**
     * 数据库用户名
     * 从配置文件中读取的数据库用户名
     */
    private static String dbUser;

    /**
     * 数据库密码
     * 从配置文件中读取的数据库密码
     */
    private static String dbPassword;

    /**
     * 数据库连接对象
     * 用于与MySQL数据库进行交互的JDBC连接
     */
    private static Connection connection = null;

    /**
     * 预编译语句对象
     * 用于执行SQL语句的PreparedStatement对象
     */
    private static PreparedStatement preparedStatement = null;

    /**
     * 静态初始化块
     * 在类加载时自动执行，用于从配置文件中加载数据库连接信息
     * 
     * 调用说明：
     * - 该方法在类加载时自动执行
     * - 读取application.yml配置文件
     * - 提取数据库连接URL、用户名、密码
     * - 如果配置文件不存在或读取失败，使用默认值
     * 
     * 调用外部服务：
     * - 读取application.yml配置文件
     *   配置文件位于classpath下的application.yml
     * 
     * 数据库调用：
     * - 无数据库调用，仅加载配置信息
     */
    static {
        loadDatabaseConfig();
    }

    /**
     * 从application.yml配置文件中加载数据库连接信息
     * 
     * 功能说明：
     * 1. 读取application.yml配置文件
     * 2. 解析配置文件内容，提取数据库连接信息
     * 3. 如果配置文件不存在或读取失败，使用默认值
     * 
     * 业务流程：
     * - 开始：类加载时自动执行
     * - 文件读取：读取classpath下的application.yml文件
     * - 内容解析：逐行解析配置文件，查找datasource配置节
     * - 信息提取：提取url、username、password字段值
     * - 默认处理：如果读取失败，使用硬编码的默认值
     * - 结束：完成配置加载
     * 
     * 调用外部服务：
     * - 读取文件系统中的application.yml配置文件
     *   使用Java的ClassLoader获取配置文件输入流
     * 
     * 数据库调用：
     * - 无数据库调用，仅加载配置信息
     * 
     * 配置文件格式（application.yml）：
     * spring:
     *   datasource:
     *     type: com.alibaba.druid.pool.DruidDataSource
     *     driverClassName: com.mysql.cj.jdbc.Driver
     *     url: jdbc:mysql://localhost:3306/grape
     *     username: root
     *     password: root
     */
    private static void loadDatabaseConfig() {
        try (InputStream input = AdbWifiConnect.class.getClassLoader().getResourceAsStream("application.yml")) {
            if (input != null) {
                // 配置文件存在，解析配置内容
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                boolean inDatasource = false;
                
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    // 检查是否进入datasource配置节
                    if (line.startsWith("datasource:")) {
                        inDatasource = true;
                    } else if (inDatasource && line.startsWith("url:")) {
                        // 提取数据库连接URL
                        dbUrl = line.substring(line.indexOf(':') + 1).trim();
                    } else if (inDatasource && line.startsWith("username:")) {
                        // 提取数据库用户名
                        dbUser = line.substring(line.indexOf(':') + 1).trim();
                    } else if (inDatasource && line.startsWith("password:")) {
                        // 提取数据库密码
                        dbPassword = line.substring(line.indexOf(':') + 1).trim();
                    } else if (inDatasource && !line.isEmpty() && !line.startsWith("#") && !line.endsWith(":")) {
                        // 检查是否退出datasource配置节
                        if (!line.startsWith("type:") && !line.startsWith("driverClassName:") && 
                            !line.startsWith("url:") && !line.startsWith("username:") && !line.startsWith("password:")) {
                            inDatasource = false;
                        }
                    }
                }
                
                logger.info("从配置文件加载数据库连接信息成功");
                logger.info("URL: {}", dbUrl);
                logger.info("USER: {}", dbUser);
            } else {
                // 配置文件不存在，使用默认值
                logger.warn("无法找到application.yml配置文件，使用默认值");
                setDefaultDatabaseConfig();
            }
        } catch (Exception e) {
            // 配置文件读取失败，使用默认值
            logger.error("加载数据库配置失败，使用默认值：{}", e.getMessage(), e);
            setDefaultDatabaseConfig();
        }
    }

    /**
     * 设置默认数据库连接配置
     * 当配置文件读取失败时使用
     * 
     * 调用说明：
     * - 该方法在配置文件读取失败时被调用
     * - 设置硬编码的默认数据库连接信息
     * 
     * 数据库调用：
     * - 无数据库调用，仅设置默认配置
     */
    private static void setDefaultDatabaseConfig() {
        dbUrl = DEFAULT_DB_URL;
        dbUser = DEFAULT_DB_USER;
        dbPassword = DEFAULT_DB_PASSWORD;
    }

    public static void adbConnect() {
        devices = Tools.method.dealMoreDevices(shell("adb devices", 0));
        logger.info("设备: {}", Arrays.toString(devices));
        logger.info("设备数量: {}", devices.length);
        
        for (String deviceId : devices) {
            if (!deviceId.contains(":") || !deviceId.contains(".")) {
                processUsbDevice(deviceId);
            } else {
                processWifiDevice(deviceId);
            }
        }
    }

    private static void processUsbDevice(String deviceId) {
        logger.info("执行USB设备: {}", deviceId);
        for (int i = 0; i < ADB_COMMANDS.length; i++) {
            if (i == 2) {
                command = "adb " + ADB_COMMANDS[i] + " " + ipAddress;
                logger.info("执行命令: {}", command);
                shell(command, i);
                
                jsonObject.set("devices_serial_number", deviceId);
                jsonObject.set("wifi_address", ipAddress);
                
                try {
                    Thread.sleep(CONNECT_DELAY_MS);
                } catch (InterruptedException e) {
                    logger.error("线程休眠异常", e);
                }
                
                String wifiDeviceId = ipAddress + ":" + ADB_PORT;
                getDevicesMessage(wifiDeviceId);
                writeToDatabase(deviceId);
            } else {
                command = "adb -s " + deviceId + ADB_COMMANDS[i];
                shell(command, i);
            }
        }
    }

    private static void processWifiDevice(String deviceId) {
        logger.info("执行IP设备: {}", deviceId);
        String ipAddress = deviceId.split(":")[0];
        
        getDevicesMessage(deviceId);
        updateDeviceByWifiAddress(ipAddress, deviceId);
    }

    private static void updateDeviceByWifiAddress(String wifiAddress, String deviceId) {
        String findWifiSql = "SELECT devices_serial_number FROM grape.device_info WHERE wifi_address = ? LIMIT 1";
        String updateSql = "UPDATE grape.device_info SET updated_time=?, is_online=1 WHERE wifi_address = ?";
        
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("数据库连接成功");
            
            preparedStatement = connection.prepareStatement(findWifiSql);
            preparedStatement.setString(1, wifiAddress);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                String serialNumber = resultSet.getString("devices_serial_number");
                logger.info("通过WiFi地址找到设备: {}", serialNumber);
                
                preparedStatement = connection.prepareStatement(updateSql);
                preparedStatement.setString(1, String.valueOf(System.currentTimeMillis()));
                preparedStatement.setString(2, wifiAddress);
                int rows = preparedStatement.executeUpdate();
                
                if (rows > 0) {
                    logger.info("设备状态更新成功: {}", serialNumber);
                } else {
                    logger.info("设备状态更新失败: {}", serialNumber);
                }
            } else {
                logger.info("未找到WiFi地址对应的设备，创建新记录: {}", wifiAddress);
                
                String generatedSerial = "IP_" + wifiAddress.replace(".", "_");
                logger.info("为IP设备生成序列号: {}", generatedSerial);
                
                jsonObject.set("devices_serial_number", generatedSerial);
                jsonObject.set("wifi_address", wifiAddress);
                writeToDatabase(generatedSerial);
            }
            
        } catch (SQLException e) {
            logger.error("数据库操作失败", e);
        } finally {
            closeResources();
        }
    }

    private static String shell(String cmd, int id) {
        Process process = null;
        BufferedReader succResult = null;
        BufferedReader errResult = null;
        StringBuffer succMsg = new StringBuffer();
        StringBuffer errMsg = new StringBuffer();
        DataOutputStream out = null;
        String message;
        
        try {
            logger.info("执行命令: {}", cmd);
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            
            out = new DataOutputStream(process.getOutputStream());
            out.flush();
            
            succResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            while ((message = succResult.readLine()) != null) {
                succMsg.append(message).append(" ");
            }
            while ((message = errResult.readLine()) != null) {
                errMsg.append(message).append(" ");
            }
            
            if (id == 0) {
                ipAddress = dealSpace(succMsg.toString().substring(succMsg.indexOf("inet") + 5, succMsg.indexOf("/")));
                logger.info("IP地址: {}", ipAddress);
            }
            
        } catch (Exception e) {
            logger.error("执行命令异常: {}", e.getMessage(), e);
        }
        
        return succMsg.toString();
    }

    private static void getDevicesMessage(String deviceName) {
        String[] deviceMessageCommands = {
            "resolution=  shell wm size",
            "devices_name= shell settings get global device_name",
            "devices_manufacturer= shell getprop ro.product.manufacturer",
            "devices_brand= shell getprop ro.product.brand",
            "android_version= shell getprop ro.build.version.release",
            "cpu_info= shell cat /proc/cpuinfo",
            "memory_info= shell cat /proc/meminfo",
        };
        
        for (String data : deviceMessageCommands) {
            logger.info("执行: adb -s {} {}", deviceName, data.split("=")[1]);
            logger.info("命令数据: {}", JSONUtil.toJsonStr(data));
            
            String message = shell("adb -s " + deviceName + data.split("=")[1], 999999);
            logger.info("返回消息: {}", message);
            
            if (data.contains("cpu_info")) {
                String[] cpuInfo = message.split("processor\t:");
                jsonObject.set(data.split("=")[0], cpuInfo.length - 1);
            } else if (data.contains("memory_info")) {
                processMemoryInfo(data, message);
            } else if (data.contains("resolution")) {
                jsonObject.set(data.split("=")[0], message.split(": ")[1]);
            } else {
                jsonObject.set(data.split("=")[0], message);
            }
        }
        
        logger.info("设备信息JSON: {}", jsonObject.toString());
    }

    private static void processMemoryInfo(String data, String message) {
        String memTotalPattern = "MemTotal:\\s+(\\d+)\\s+kB";
        String memAvailablePattern = "MemAvailable:\\s+(\\d+)\\s+kB";
        
        Pattern pattern = Pattern.compile(memTotalPattern);
        Pattern pattern1 = Pattern.compile(memAvailablePattern);
        
        Matcher matcher = pattern.matcher(message);
        Matcher matcher1 = pattern1.matcher(message);
        
        String memTotal = "0";
        String memAvailable = "0";
        
        if (matcher.find()) {
            memTotal = matcher.group(1);
        }
        if (matcher1.find()) {
            memAvailable = matcher1.group(1);
        }
        
        String memoryInfo = String.format("总存储：%dMB 可用存储：%dMB", 
            Integer.parseInt(memTotal) / 1024, Integer.parseInt(memAvailable) / 1024);
        jsonObject.put(data.split("=")[0], memoryInfo);
    }

    private static void writeToDatabase(String deviceSerial) {
        String fields = "devices_serial_number,wifi_address,devices_name,devices_manufacturer,devices_brand,android_version,cpu_info,memory_info,resolution,is_online,created_time,updated_time";
        String insertSql = "INSERT INTO grape.device_info (" + fields + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectSql = "SELECT devices_serial_number FROM grape.device_info WHERE devices_serial_number = ? LIMIT 1";
        String updateSql = "UPDATE grape.device_info SET wifi_address=?, updated_time=?, is_online=? WHERE devices_serial_number = ?";
        
        logger.info("查询设备是否存在SQL: {}", selectSql);
        
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("数据库连接成功");
            
            preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, deviceSerial);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                logger.info("设备已存在，更新数据");
                preparedStatement = connection.prepareStatement(updateSql);
                preparedStatement.setString(1, jsonObject.get("wifi_address").toString());
                preparedStatement.setString(2, String.valueOf(System.currentTimeMillis()));
                preparedStatement.setString(3, String.valueOf(1));
                preparedStatement.setString(4, deviceSerial);
                
                int rows = preparedStatement.executeUpdate();
                if (rows > 0) {
                    logger.info("数据更新成功，设备信息：{}", deviceSerial);
                } else {
                    logger.info("数据更新失败，设备信息：{}", deviceSerial);
                }
            } else {
                logger.info("设备不存在，插入新数据");
                preparedStatement = connection.prepareStatement(insertSql);
                
                String[] fieldArray = fields.split(",");
                for (int i = 0; i < fieldArray.length; i++) {
                    if (i >= fieldArray.length - 2) {
                        preparedStatement.setString(i + 1, String.valueOf(System.currentTimeMillis()));
                    } else if (fieldArray[i].contains("is_online")) {
                        preparedStatement.setString(i + 1, String.valueOf(1));
                    } else {
                        preparedStatement.setString(i + 1, jsonObject.getStr(fieldArray[i]));
                    }
                }
                
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("数据插入成功，新设备 '{}' 已添加到数据库", deviceSerial);
                } else {
                    logger.info("数据插入失败");
                }
            }
            
        } catch (SQLException e) {
            logger.error("数据库操作失败", e);
        } finally {
            closeResources();
        }
    }

    public static void setOnlineDevices() {
        devices = Tools.method.dealMoreDevices(shell("adb devices", 0));
        logger.info("设备: {}", Arrays.toString(devices));
        
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("数据库连接成功");
            
            String updateAllOfflineSql = "UPDATE grape.device_info SET is_online=0";
            preparedStatement = connection.prepareStatement(updateAllOfflineSql);
            int rowsOffline = preparedStatement.executeUpdate();
            logger.info("所有设备已标记为离线，影响行数：{}", rowsOffline);
            
            String updateOnlineSql = "UPDATE grape.device_info SET is_online=1 WHERE wifi_address = ? OR devices_serial_number = ?";
            
            for (String deviceId : devices) {
                preparedStatement = connection.prepareStatement(updateOnlineSql);
                
                if (deviceId.contains(":") || deviceId.contains(".")) {
                    String ipAddress = deviceId.split(":")[0];
                    preparedStatement.setString(1, ipAddress);
                    preparedStatement.setString(2, ipAddress);
                } else {
                    preparedStatement.setString(1, deviceId);
                    preparedStatement.setString(2, deviceId);
                }
                
                int rowsOnline = preparedStatement.executeUpdate();
                if (rowsOnline > 0) {
                    logger.info("设备设置上线成功，设备信息：{}", deviceId);
                } else {
                    logger.info("设备设置上线失败，设备信息：{}", deviceId);
                }
            }
            
        } catch (SQLException e) {
            logger.error("数据库操作失败", e);
        } finally {
            closeResources();
        }
    }

    private static void closeResources() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
                logger.info("数据库连接已关闭");
            }
        } catch (SQLException e) {
            logger.error("关闭数据库资源失败", e);
        }
    }

    public static void main(String[] args) {
        adbConnect();
        setOnlineDevices();
    }
}
