package com.grape.grape.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Tools.DataAndLog;

/**
 * 坐标采集工具类
 * 负责从Android设备采集触摸坐标，并执行自动滑动操作
 * 
 * 主要功能：
 * 1. 通过ADB命令与Android设备交互
 * 2. 采集设备的屏幕分辨率和触摸坐标
 * 3. 执行自动滑动操作
 * 4. 支持多组坐标点的随机选择和滑动
 * 
 * @author grape-team
 * @since 2025-01-01
 */
public class CoordinateCollector implements Runnable {
    
    /**
     * 滑动命令前缀
     * ADB滑动命令的基本格式：adb -s <device> shell input swipe <x1> <y1> <x2> <y2>
     */
    private static final String SWIPE_COMMAND = " shell input swipe ";
    
    /**
     * 触摸事件代码 - 开始
     * Android触摸事件中的坐标开始标记
     */
    private static final String TOUCH_CODE_START = "0035";
    
    /**
     * 触摸事件代码 - 结束
     * Android触摸事件中的坐标结束标记
     */
    private static final String TOUCH_CODE_END = "0036";
    
    /**
     * 坐标采样数量
     * 每次采集起点和终点坐标时采集的样本数量
     */
    private static final int SAMPLING_COUNT = 3;
    
    /**
     * ADB命令ID - 分辨率查询
     * 用于标识获取设备屏幕分辨率的ADB命令
     */
    private static final int COMMAND_ID_RESOLUTION = 1;
    
    /**
     * ADB命令ID - 坐标查询
     * 用于标识获取触摸坐标的ADB命令
     */
    private static final int COMMAND_ID_COORDINATE = 2;
    
    /**
     * ADB命令ID - 触摸事件
     * 用于标识触发触摸事件的ADB命令
     */
    private static final int COMMAND_ID_TOUCH = 3;

    /**
     * ADB命令数组
     * 存储用于设备信息和坐标采集的ADB命令列表
     * 命令内容由外部传入，在初始化时设置
     */
    private final String[] commands;
    
    /**
     * 设备ID
     * Android设备的唯一标识符（如序列号或IP地址）
     * 用于ADB命令的设备选择参数
     */
    private final String device;
    
    /**
     * 当前坐标提示信息
     * 用于提示用户开始采集起点或终点坐标
     * 值为"无效"表示当前未在进行坐标采集
     */
    private static String location = "无效";
    
    /**
     * 设备屏幕宽度（像素）
     * 通过ADB命令获取的设备实际屏幕宽度
     */
    private int deviceWidth;
    
    /**
     * 设备屏幕高度（像素）
     * 通过ADB命令获取的设备实际屏幕高度
     */
    private int deviceHeight;
    
    /**
     * 窗口最大宽度
     * 通过ADB命令获取的显示窗口最大宽度
     */
    private int maxWindowWidth;
    
    /**
     * 窗口最大高度
     * 通过ADB命令获取的显示窗口最大高度
     */
    private int maxWindowHeight;
    
    /**
     * 起点坐标数组
     * 存储采集到的所有滑动起点坐标
     * 格式：["x1/y1", "x2/y2", ...]
     */
    private String[] startCoordinates = {};
    
    /**
     * 终点坐标数组
     * 存储采集到的所有滑动终点坐标
     * 格式：["x1/y1", "x2/y2", ...]
     */
    private String[] endCoordinates = {};

    /**
     * 构造函数
     * 
     * 功能说明：
     * 初始化坐标采集工具，设置ADB命令和设备ID
     * 
     * @param commands ADB命令数组，包含设备信息查询和坐标采集的命令
     * @param device Android设备ID（序列号或IP地址）
     */
    public CoordinateCollector(String[] commands, String device) {
        this.commands = commands;
        this.device = device;
    }

    /**
     * 线程运行方法
     * 实现Runnable接口，在独立线程中执行坐标采集和滑动操作
     * 
     * 功能说明：
     * 1. 初始化设备参数（分辨率、窗口坐标等）
     * 2. 采集多组触摸坐标（起点和终点）
     * 3. 进入无限循环，随机选择坐标执行滑动操作
     * 
     * 业务流程：
     * - 开始：线程启动，准备执行任务
     * - 参数初始化：获取设备分辨率、窗口坐标等信息
     * - 坐标采集：采集指定数量的起点和终点坐标
     * - 滑动循环：无限循环执行以下操作
     *   1. 随机选择起点和终点坐标
     *   2. 随机选择滑动延时时间
     *   3. 执行ADB滑动命令
     *   4. 等待指定的延时时间
     * - 结束：循环继续，直到线程被中断
     * 
     * 调用外部服务：
     * - 调用 ADB命令行工具
     *   ADB是Android SDK提供的调试工具
     *   通过ADB可以执行shell命令、发送触摸事件等
     * 
     * 数据库调用：
     * - 无数据库调用，直接与设备交互
     */
    @Override
    public void run() {
        synchronized (this) {
            // 方法开始：初始化设备参数
            initializeDeviceParameters();
            
            // 检查设备参数是否有效
            if (deviceWidth > 0 && deviceHeight > 0) {
                // 方法中间：采集坐标并开始滑动循环
                collectCoordinates();
                startSwipeLoop();
            } else {
                // 设备参数无效，记录警告
                Tools.DataAndLog.log("设备参数无效，无法执行滑动操作");
            }
        }
    }

    /**
     * 初始化设备参数
     * 
     * 功能说明：
     * 1. 执行ADB命令获取设备分辨率
     * 2. 执行ADB命令获取窗口坐标
     * 3. 采集触摸坐标（起点和终点）
     * 
     * 业务流程：
     * - 开始：准备初始化设备参数
     * - 命令执行：按顺序执行ADB命令数组中的命令
     * - 参数解析：解析命令返回的结果，提取设备信息
     * - 结束：完成设备参数初始化
     * 
     * 调用外部服务：
     * - 调用 ADB命令行工具
     *   命令1: 获取设备分辨率
     *   命令2: 获取窗口坐标
     *   命令3: 采集触摸坐标
     * 
     * 数据库调用：
     * - 无数据库调用，直接与设备交互
     */
    private void initializeDeviceParameters() {
        // 方法开始：按顺序执行ADB命令
        for (int i = 0; i < commands.length; i++) {
            switch (i) {
                case COMMAND_ID_RESOLUTION:
                    // 解析设备分辨率
                    parseDeviceResolution();
                    break;
                case COMMAND_ID_COORDINATE:
                    // 解析窗口坐标
                    parseWindowCoordinates();
                    break;
                case COMMAND_ID_TOUCH:
                    // 采集触摸坐标
                    collectTouchCoordinates();
                    break;
            }
        }
    }

    /**
     * 解析设备分辨率
     * 
     * 功能说明：
     * 执行ADB命令获取设备屏幕分辨率，并解析返回结果
     * 
     * 业务流程：
     * - 开始：执行分辨率查询命令
     * - 命令执行：调用execShell()执行ADB命令
     * - 结果解析：解析命令返回的分辨率信息
     * - 参数设置：设置deviceWidth和deviceHeight
     * - 结束：完成分辨率解析
     * 
     * 调用外部服务：
     * - 调用 ADB命令：adb -s <device> shell wm size
     *   返回格式：Physical size: 1080x1920
     * 
     * 数据库调用：
     * - 无数据库调用
     */
    private void parseDeviceResolution() {
        // 方法开始：执行ADB命令获取分辨率
        String resolution = execShell(commands[COMMAND_ID_RESOLUTION], COMMAND_ID_RESOLUTION).split(" ")[2];
        
        // 解析分辨率字符串，格式为"1080x1920"
        deviceWidth = Integer.parseInt(resolution.split("x")[0]);
        deviceHeight = Integer.parseInt(resolution.split("x")[1]);
        
        // 方法结束：记录日志
        Tools.DataAndLog.log("设备分辨率 - 宽度: {} 高度: {}", deviceWidth, deviceHeight);
    }

    /**
     * 解析窗口坐标
     * 
     * 功能说明：
     * 执行ADB命令获取显示窗口的坐标范围
     * 
     * 业务流程：
     * - 开始：执行窗口坐标查询命令
     * - 命令执行：调用execShell()执行ADB命令
     * - 结果解析：解析命令返回的窗口坐标信息
     * - 参数设置：设置maxWindowWidth和maxWindowHeight
     * - 结束：完成窗口坐标解析
     * 
     * 调用外部服务：
     * - 调用 ADB命令（具体命令内容由外部传入）
     *   返回格式：1080,1920（逗号分隔的坐标）
     * 
     * 数据库调用：
     * - 无数据库调用
     */
    private void parseWindowCoordinates() {
        // 方法开始：执行ADB命令获取窗口坐标
        String coordinates = execShell(commands[COMMAND_ID_COORDINATE], COMMAND_ID_COORDINATE);
        
        // 解析坐标字符串，格式为"1080,1920"
        maxWindowWidth = Integer.parseInt(coordinates.split(",")[0]);
        maxWindowHeight = Integer.parseInt(coordinates.split(",")[1]);
        
        // 方法结束：记录日志
        Tools.DataAndLog.log("窗口坐标 - 最大宽度: {} 最大高度: {}", maxWindowWidth, maxWindowHeight);
    }

    /**
     * 采集触摸坐标
     * 
     * 功能说明：
     * 采集指定数量的起点和终点坐标用于滑动操作
     * 
     * 业务流程：
     * - 开始：准备采集坐标
     * - 循环采集：按采样数量循环执行以下操作
     *   1. 提示用户开始采集起点坐标
     *   2. 执行ADB命令获取起点坐标
     *   3. 提示用户开始采集终点坐标
     *   4. 执行ADB命令获取终点坐标
     *   5. 将坐标保存到数组中
     * - 结束：完成坐标采集
     * 
     * 调用外部服务：
     * - 调用 ADB命令触发触摸事件
     *   用户需要在设备上触摸屏幕，采集坐标
     * 
     * 数据库调用：
     * - 无数据库调用
     */
    private void collectTouchCoordinates() {
        // 方法开始：按采样数量循环采集坐标
        for (int i = 0; i <= SAMPLING_COUNT; i++) {
            // 采集起点坐标
            String startCoord = collectSingleCoordinate("起点");
            
            // 采集终点坐标
            String endCoord = collectSingleCoordinate("终点");
            
            // 记录采集结果
            Tools.DataAndLog.log("坐标采集 - 起点: {} 终点: {}", startCoord, endCoord);
            
            // 保存坐标到数组
            startCoordinates = Tools.method.insert(startCoordinates, startCoord);
            endCoordinates = Tools.method.insert(endCoordinates, endCoord);
        }
    }

    /**
     * 采集单个坐标点
     * 
     * 功能说明：
     * 提示用户采集指定类型的坐标点（起点或终点）
     * 
     * 业务流程：
     * - 开始：设置提示信息
     * - 提示用户：更新location提示用户开始采集
     * - 执行命令：调用execShell()执行ADB命令
     * - 结束：返回采集到的坐标
     * 
     * 调用外部服务：
     * - 调用 ADB命令（具体命令内容由外部传入）
     *   触发触摸事件，采集用户点击的坐标
     * 
     * 数据库调用：
     * - 无数据库调用
     * 
     * @param type 坐标类型（起点或终点）
     * @return 采集到的坐标，格式为"x/y"
     */
    private String collectSingleCoordinate(String type) {
        // 方法开始：设置提示信息
        location = "请开始 " + device + " 的 ---" + type + "---坐标";
        
        // 执行ADB命令采集坐标
        String coordinate = execShell(commands[COMMAND_ID_TOUCH], COMMAND_ID_TOUCH);
        
        // 方法结束：返回采集的坐标
        return coordinate;
    }

    /**
     * 开始滑动循环
     * 
     * 功能说明：
     * 无限循环执行滑动操作，直到线程被中断
     * 
     * 业务流程：
     * - 开始：进入无限循环
     * - 循环执行：
     *   1. 记录当前坐标信息
     *   2. 随机选择起点和终点坐标
     *   3. 随机选择滑动延时时间
     *   4. 构建并执行ADB滑动命令
     *   5. 等待指定的延时时间
     * - 异常处理：捕获并记录异常
     * - 结束：循环继续，直到线程被中断
     * 
     * 调用外部服务：
     * - 调用 ADB命令：adb -s <device> shell input swipe <x1> <y1> <x2> <y2>
     *   执行滑动操作，从起点坐标滑动到终点坐标
     * 
     * 数据库调用：
     * - 无数据库调用
     */
    private void startSwipeLoop() {
        // 方法开始：进入无限循环
        while (true) {
            // 记录当前坐标信息
            logCurrentCoordinates();
            
            // 执行随机滑动
            performRandomSwipe();
        }
    }

    /**
     * 记录当前坐标信息
     * 
     * 功能说明：
     * 将采集到的所有起点和终点坐标记录到日志中
     * 
     * 业务流程：
     * - 开始：准备记录坐标
     * - 遍历起点：遍历起点数组，记录每个起点坐标
     * - 遍历终点：遍历终点数组，记录每个终点坐标
     * - 结束：完成坐标记录
     * 
     * 调用外部服务：
     * - 无外部服务调用
     * 
     * 数据库调用：
     * - 无数据库调用
     */
    private void logCurrentCoordinates() {
        // 记录起点坐标
        for (String coord : startCoordinates) {
            Tools.DataAndLog.log("起点坐标 - 设备: {} 坐标: {}", device, coord);
        }
        
        // 记录终点坐标
        for (String coord : endCoordinates) {
            Tools.DataAndLog.log("终点坐标 - 设备: {} 坐标: {}", device, coord);
        }
    }

    /**
     * 执行随机滑动
     * 
     * 功能说明：
     * 随机选择起点和终点坐标，并执行滑动操作
     * 
     * 业务流程：
     * - 开始：准备执行滑动
     * - 获取配置：从配置文件中获取滑动延时时间数组
     * - 随机选择：
     *   1. 随机选择起点坐标
     *   2. 随机选择终点坐标
     *   3. 随机选择延时时间
     * - 执行命令：构建并执行ADB滑动命令
     * - 等待延时：根据选择的延时时间等待
     * - 异常处理：捕获并记录IO异常和中断异常
     * - 结束：完成一次滑动操作
     * 
     * 调用外部服务：
     * - 调用 ADB命令：adb -s <device> shell input swipe <x1> <y1> <x2> <y2>
     *   执行滑动操作，从起点坐标滑动到终点坐标
     * - 调用 工具类方法：method.retrunConfig()
     *   从配置文件中读取滑动延时时间数组
     * 
     * 数据库调用：
     * - 无数据库调用
     */
    private void performRandomSwipe() {
        try {
            // 获取滑动延时时间配置
            String[] timeConfig = Tools.method.retrunConfig();
            
            // 创建随机数生成器
            Random random = new Random();
            
            // 随机选择延时时间
            int randomTimeIndex = random.nextInt(timeConfig.length);
            int delaySeconds = Integer.parseInt(timeConfig[randomTimeIndex]);
            
            // 随机选择起点坐标
            String startPoint = startCoordinates[random.nextInt(startCoordinates.length)];
            
            // 随机选择终点坐标
            String endPoint = endCoordinates[random.nextInt(endCoordinates.length)];
            
            // 解析起点坐标
            int x1 = Integer.parseInt(startPoint.split("/")[0]);
            int y1 = Integer.parseInt(startPoint.split("/")[1]);
            
            // 解析终点坐标
            int x2 = Integer.parseInt(endPoint.split("/")[0]);
            int y2 = Integer.parseInt(endPoint.split("/")[1]);
            
            // 构建ADB滑动命令
            String swipeCommand = "adb -s " + device + SWIPE_COMMAND + x1 + " " + y1 + " " + x2 + " " + y2;
            
            // 记录日志
            Tools.DataAndLog.log("执行滑动 - 命令: {} 延时: {}秒", swipeCommand, delaySeconds);
            
            // 执行ADB滑动命令
            Runtime.getRuntime().exec(swipeCommand);
            
            // 等待指定的延时时间
            Thread.sleep(1000 * delaySeconds);
            
        } catch (IOException e) {
            // IO异常处理
            Tools.DataAndLog.log("IO异常: {}", e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            // 线程中断异常处理
            Tools.DataAndLog.log("线程中断异常: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            initializeDeviceParameters();
            
            if (deviceWidth > 0 && deviceHeight > 0) {
                collectCoordinates();
                startSwipeLoop();
            }
        }
    }

    private void initializeDeviceParameters() {
        for (int i = 0; i < commands.length; i++) {
            switch (i) {
                case COMMAND_ID_RESOLUTION:
                    parseDeviceResolution();
                    break;
                case COMMAND_ID_COORDINATE:
                    parseWindowCoordinates();
                    break;
                case COMMAND_ID_TOUCH:
                    collectTouchCoordinates();
                    break;
            }
        }
    }

    private void parseDeviceResolution() {
        String resolution = execShell(commands[COMMAND_ID_RESOLUTION], COMMAND_ID_RESOLUTION).split(" ")[2];
        deviceWidth = Integer.parseInt(resolution.split("x")[0]);
        deviceHeight = Integer.parseInt(resolution.split("x")[1]);
        DataAndLog.log("设备分辨率 - 宽度: {} 高度: {}", deviceWidth, deviceHeight);
    }

    private void parseWindowCoordinates() {
        String coordinates = execShell(commands[COMMAND_ID_COORDINATE], COMMAND_ID_COORDINATE);
        maxWindowWidth = Integer.parseInt(coordinates.split(",")[0]);
        maxWindowHeight = Integer.parseInt(coordinates.split(",")[1]);
        DataAndLog.log("窗口坐标 - 最大宽度: {} 最大高度: {}", maxWindowWidth, maxWindowHeight);
    }

    private void collectTouchCoordinates() {
        for (int i = 0; i <= SAMPLING_COUNT; i++) {
            String startCoord = collectSingleCoordinate("起点");
            String endCoord = collectSingleCoordinate("终点");
            
            DataAndLog.log("坐标采集 - 起点: {} 终点: {}", startCoord, endCoord);
            
            startCoordinates = Tools.method.insert(startCoordinates, startCoord);
            endCoordinates = Tools.method.insert(endCoordinates, endCoord);
        }
    }

    private String collectSingleCoordinate(String type) {
        location = "请开始 " + device + " 的 ---" + type + "---坐标";
        String coordinate = execShell(commands[COMMAND_ID_TOUCH], COMMAND_ID_TOUCH);
        return coordinate;
    }

    private void startSwipeLoop() {
        while (true) {
            logCurrentCoordinates();
            performRandomSwipe();
        }
    }

    private void logCurrentCoordinates() {
        for (String coord : startCoordinates) {
            DataAndLog.log("起点坐标 - 设备: {} 坐标: {}", device, coord);
        }
        for (String coord : endCoordinates) {
            DataAndLog.log("终点坐标 - 设备: {} 坐标: {}", device, coord);
        }
    }

    private void performRandomSwipe() {
        try {
            String[] timeConfig = Tools.method.retrunConfig();
            Random random = new Random();
            
            int randomTimeIndex = random.nextInt(timeConfig.length);
            int delaySeconds = Integer.parseInt(timeConfig[randomTimeIndex]);
            
            String startPoint = startCoordinates[random.nextInt(startCoordinates.length)];
            String endPoint = endCoordinates[random.nextInt(endCoordinates.length)];
            
            int x1 = Integer.parseInt(startPoint.split("/")[0]);
            int y1 = Integer.parseInt(startPoint.split("/")[1]);
            int x2 = Integer.parseInt(endPoint.split("/")[0]);
            int y2 = Integer.parseInt(endPoint.split("/")[1]);
            
            String swipeCommand = "adb -s " + device + SWIPE_COMMAND + x1 + " " + y1 + " " + x2 + " " + y2;
            
            DataAndLog.log("执行滑动 - 命令: {} 延时: {}秒", swipeCommand, delaySeconds);
            
            Runtime.getRuntime().exec(swipeCommand);
            Thread.sleep(1000 * delaySeconds);
            
        } catch (IOException e) {
            DataAndLog.log("IO异常: {}", e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            DataAndLog.log("线程中断异常: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static String execShell(String command, int commandId) {
        Process process = null;
        BufferedReader succResult = null;
        BufferedReader errResult = null;
        StringBuffer succMsg = new StringBuffer();
        StringBuffer result = new StringBuffer();
        StringBuffer errMsg = new StringBuffer();
        DataOutputStream out = null;
        String message;
        
        try {
            DataAndLog.log(command);
            if (commandId >= COMMAND_ID_TOUCH) {
                DataAndLog.log(location);
            }
            
            process = Runtime.getRuntime().exec(command);
            out = new DataOutputStream(process.getOutputStream());
            out.flush();
            
            succResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            while ((message = succResult.readLine()) != null) {
                succMsg.append(message).append(" ");
                
                if (commandId == COMMAND_ID_TOUCH || commandId == COMMAND_ID_COORDINATE) {
                    Pattern pattern = Pattern.compile(TOUCH_CODE_END);
                    Matcher matcher = pattern.matcher(Tools.method.dealSpace(succMsg.toString()));
                    if (matcher.find()) {
                        process.destroy();
                    }
                }
            }
            
            while ((message = errResult.readLine()) != null) {
                errMsg.append(message).append(" ");
            }
            
            if (commandId == 0 || commandId == COMMAND_ID_RESOLUTION) {
                result.append(succMsg.toString());
            } else if (commandId == COMMAND_ID_COORDINATE) {
                result.append(parseWindowCoordinate(succMsg.toString()));
            } else if (commandId == COMMAND_ID_TOUCH) {
                result.append(parseTouchCoordinate(succMsg.toString()));
            }
            
        } catch (Exception e) {
            DataAndLog.log("执行命令异常: {}", e.getMessage());
            e.printStackTrace();
        }
        
        return result.toString();
    }

    private static String parseWindowCoordinate(String message) {
        String x = null;
        String y = null;
        
        try {
            if (message != null && !message.trim().isEmpty()) {
                String[] tokens = Tools.method.dealSpace(message).split(" ");
                
                for (int i = 0; i < tokens.length; i++) {
                    if (TOUCH_CODE_START.equals(tokens[i])) {
                        x = tokens[i + 7];
                    } else if (TOUCH_CODE_END.equals(tokens[i])) {
                        y = tokens[i + 7];
                    }
                }
                
                DataAndLog.log("窗口坐标 - X: {} Y: {}", x, y);
                
                if (x != null && y != null) {
                    return x + y;
                }
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            DataAndLog.log("解析窗口坐标异常，请连接设备或授权USB连接: {}", e.getMessage());
        }
        
        return "";
    }

    private static String parseTouchCoordinate(String message) {
        String x = null;
        String y = null;
        
        try {
            if (message != null && !message.trim().isEmpty()) {
                String[] tokens = Tools.method.dealSpace(message).split(" ");
                
                for (int i = 0; i < tokens.length; i++) {
                    if (TOUCH_CODE_START.equals(tokens[i])) {
                        x = tokens[i + 1];
                    } else if (TOUCH_CODE_END.equals(tokens[i])) {
                        y = tokens[i + 1];
                    }
                }
                
                if (x != null && y != null) {
                    int screenX = Integer.parseInt(x, 16) * deviceWidth / maxWindowWidth;
                    int screenY = Integer.parseInt(y, 16) * deviceHeight / maxWindowHeight;
                    
                    DataAndLog.log("触摸坐标 - 原始X: {} 原始Y: {} 屏幕X: {} 屏幕Y: {}", x, y, screenX, screenY);
                    
                    return screenX + "," + screenY;
                }
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            DataAndLog.log("解析触摸坐标异常，请连接设备或授权USB连接: {}", e.getMessage());
        }
        
        return "";
    }
}
