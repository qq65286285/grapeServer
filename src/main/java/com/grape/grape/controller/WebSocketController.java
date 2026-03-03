package com.grape.grape.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.grape.grape.service.ai.B_WsXModel;

/**
 * WebSocket 控制器
 * 用于向前端推送 AI 回复
 */
@Controller
@RequestMapping("/ws")
public class WebSocketController extends TextWebSocketHandler {
    // 存储所有活跃的 WebSocket 会话
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 通过 Spring 容器注入 B_WsXModel 实例
    @Autowired
    private B_WsXModel wsXModel;

    /**
     * 当 WebSocket 连接建立时调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 保存会话
        sessions.put(session.getId(), session);
        System.out.println("WebSocket 连接建立: " + session.getId());
    }

    /**
     * 当收到 WebSocket 消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("收到 WebSocket 消息: " + message.getPayload());
        try {
            // 处理前端发送的消息
            String payload = message.getPayload();
            if (payload.contains("ping") || payload.contains("heartbeat")) {
                // 响应心跳消息
                session.sendMessage(new TextMessage("{\"type\": \"pong\"}"));
                System.out.println("响应心跳消息: " + session.getId());
            } else if (payload.contains("ai_chat") || payload.contains("test_case_generator") || payload.contains("ask_question")) {
                // 处理AI问题请求
                handleAskQuestion(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理AI问题请求
     * @param payload 消息内容
     */
    private void handleAskQuestion(String payload) {
        try {
            // 解析消息，获取问题内容和服务类型
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(payload);
            String serviceType = jsonObject.getString("type");
            String question = jsonObject.getString("question");
            
            // 验证服务类型
            if ("ask_question".equals(serviceType)) {
                serviceType = "ai_chat";
            } else if (!"ai_chat".equals(serviceType) && !"test_case_generator".equals(serviceType)) {
                serviceType = "test_case_generator";
            }
            
            // 处理test_case_generator类型的消息，从data字段获取参数
            if ("test_case_generator".equals(serviceType) && question == null && jsonObject.containsKey("data")) {
                // 从data字段获取测试用例生成参数
                com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
                // 构建提示词
                question = buildTestCasePrompt(data);
            }
            
            if (question != null && !question.isEmpty()) {
                System.out.println("处理AI问题: " + question);
                System.out.println("服务类型: " + serviceType);
                
                // 创建角色内容对象
                com.grape.grape.entity.dto.RoleContent roleContent = new com.grape.grape.entity.dto.RoleContent();
                roleContent.setRole("user");
                roleContent.setContent(question);
                
                // 调用B_WsXModel处理AI请求，传递服务类型
                wsXModel.initWebSocket(roleContent, serviceType);
                
                // 不发送处理中消息，直接等待AI返回结果
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 发送错误消息
            broadcast("{\"service\": \"test_case_generator\", \"type\": \"ai_error\", \"data\": \"处理问题时出错: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * 构建测试用例生成提示词
     * @param data 测试用例生成参数
     * @return 构建好的提示词
     */
    private String buildTestCasePrompt(com.alibaba.fastjson.JSONObject data) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位专业的测试用例生成助手，根据以下需求生成高质量的测试用例。\n");
        sb.append("\n[需求信息]\n");
        sb.append("模块: " + data.getString("module") + "\n");
        sb.append("用户故事: " + data.getString("userStory") + "\n");
        sb.append("验收标准: " + data.getString("acceptanceCriteria") + "\n");
        sb.append("边界条件: " + data.getString("boundaryConditions") + "\n");
        sb.append("相关模块: " + data.getString("relatedModules") + "\n");
        
        // 处理测试维度数组
        if (data.containsKey("testDimensions")) {
            com.alibaba.fastjson.JSONArray testDimensions = data.getJSONArray("testDimensions");
            sb.append("测试维度: " + String.join(", ", testDimensions.toJavaList(String.class)) + "\n");
        }
        
        sb.append("用例类型: " + data.getString("caseType") + "\n");
        sb.append("\n[生成要求]\n");
        sb.append("用例数量: " + data.getInteger("caseCount") + "\n");
        sb.append("生成模式: " + data.getString("generateMode") + "\n");
        sb.append("用例模板: " + data.getString("caseTemplate") + "\n");
        
        // 处理覆盖要求数组
        if (data.containsKey("coverageRequirements")) {
            com.alibaba.fastjson.JSONArray coverageRequirements = data.getJSONArray("coverageRequirements");
            sb.append("覆盖要求: " + String.join(", ", coverageRequirements.toJavaList(String.class)) + "\n");
        }
        
        sb.append("\n重要要求：请严格以纯文本（plaintext）脑图格式返回生成的测试用例列表，不要添加任何额外的说明文字或格式。\n");

        sb.append("格式要求：\n");
        sb.append("必须按照以下示例格式进行组织：\n");
        // sb.append("2. test_cases字段是一个数组，包含多个测试用例对象\n");
        // sb.append("3. 每个测试用例对象必须包含case_id、title、steps和expected四个字段\n");
        // sb.append("4. case_id字段是测试用例的唯一标识，格式为字符串\n");
        // sb.append("4. title字段是测试用例的标题，格式为字符串\n");
        // sb.append("6. steps字段是测试步骤的数组，每个元素是一个字符串\n");
        // sb.append("7. expected字段是预期结果，格式为字符串\n");
        sb.append("\n示例：\n");
        sb.append("├─ TC：指定操作下拉单选功能 \n");
        sb.append("│  ├─ 优先级：P0 \n");
        sb.append("│  ├─ 迭代：v1.49 \n");
        sb.append("│  ├─ 前提：无 \n");
        sb.append("│  ├─ 步骤 \n");
        sb.append("│  │  ├─ 1、进入自动化连接器-自动化操作配置页面 \n");
        sb.append("│  │  │  └─ 预期结果1：页面加载正常，配置区域展示完整 \n");
        sb.append("│  │  ├─ 2、点击\"指定操作\"下拉框 \n");
        sb.append("│  │  │  └─ 预期结果2：下拉框展开，显示两个枚举值选项 \n");
        sb.append("│  │  └─ 3、分别选择两个枚举值选项 \n");
        sb.append("│  │      └─ 预期结果3：两个选项均可正常选中，选中状态清晰 \n");
        sb.append("├─ TC：指定操作与指定视图联动（任务视图） \n");
        sb.append("│  ├─ 优先级：P0 \n");
        sb.append("│  ├─ 迭代：v1.49 \n");
        sb.append("│  ├─ 前提：无 \n");
        sb.append("│  ├─ 步骤 \n");
        sb.append("│  │  ├─ 1、选择指定操作为\"指定任务视图催单\" \n");
        sb.append("│  │  │  └─ 预期结果1：指定操作选择成功，无报错 \n");
        sb.append("│  │  └─ 2、查看\"指定视图\"下拉框内容 \n");
        sb.append("│  │      └─ 预期结果2：下拉框返回自定义任务的视图列表，内容准确 \n");
        sb.append("├─ TC：指定操作与指定视图联动（工作项视图） \n");
        sb.append("│  ├─ 优先级：P0 \n");
        sb.append("│  ├─ 迭代：v1.49 \n");
        sb.append("│  ├─ 前提：无 \n");
        sb.append("│  ├─ 步骤 \n");
        sb.append("│  │  ├─ 1、选择指定操作为\"指定工作项视图催单\" \n");
        sb.append("│  │  │  └─ 预期结果1：指定操作选择成功，无卡顿 \n");
        sb.append("│  │  └─ 2、查看\"指定视图\"下拉框内容 \n");
        sb.append("│  │      └─ 预期结果2：下拉框返回全部工作项（含自定义）的视图列表，无遗漏 \n");
        sb.append("\n请严格按照上述纯文本脑图格式生成测试用例，不要添加任何额外的内容。");
        
        return sb.toString();
    }

    /**
     * 当 WebSocket 连接关闭时调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除会话
        sessions.remove(session.getId());
        System.out.println("WebSocket 连接关闭: " + session.getId());
    }

    /**
     * 发送消息给所有客户端
     * @param message 消息内容
     */
    public static void broadcast(String message) {
        for (WebSocketSession session : sessions.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    System.out.println("推送消息给客户端: " + session.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息给指定客户端
     * @param sessionId 会话 ID
     * @param message 消息内容
     */
    public static void sendTo(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("推送消息给指定客户端: " + sessionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前活跃的会话数
     * @return 会话数
     */
    public static int getActiveSessionCount() {
        return sessions.size();
    }
}
