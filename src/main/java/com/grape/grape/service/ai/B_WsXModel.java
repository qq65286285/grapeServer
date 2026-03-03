package com.grape.grape.service.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.grape.grape.controller.WebSocketController;
import com.grape.grape.entity.dto.JsonParse;
import com.grape.grape.entity.dto.RoleContent;
import com.grape.grape.entity.dto.WsXModelDto;


@Component
public class B_WsXModel extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(B_WsXModel.class);
    // 具体可以参考接口文档 https://www.xfyun.cn/doc/spark/Web.html
    @Value("${xfyun.spark.host-url}")
    private String hostUrl;
    
    @Value("${xfyun.spark.domain}")
    private String domain;
    
    // 获取地址：https://console.xfyun.cn/services/cbm
    @Value("${xfyun.spark.appid}")
    private String appid;
    
    @Value("${xfyun.spark.api-secret}")
    private String apiSecret;
    
    @Value("${xfyun.spark.api-key}")
    private String apiKey;

    private Boolean wsCloseFlag = false;
    private static final String NEW_QUESTION = "介绍下科大讯飞";


    // Spring 容器管理的初始化方法
    public void initWebSocket() throws Exception {
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = client.newWebSocket(request, this); // 调用WS入口
    }

    // 重载方法，支持传入自定义问题
    public void initWebSocket(RoleContent roleContent) throws Exception {
        initWebSocket(roleContent, "test_case_generator");
    }
    
    // 重载方法，支持传入自定义问题和服务类型
    public void initWebSocket(RoleContent roleContent, String serviceType) throws Exception {
        // 输出请求日志
        System.out.println("**********************");
        System.out.println("请求url：" + hostUrl);
        System.out.println("请求入参：" + roleContent.getContent());
        System.out.println("服务类型：" + serviceType);
        
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                System.out.print("大模型：");
                MyThread myThread = new MyThread(webSocket, roleContent);
                myThread.start();
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                // 输出响应日志
                System.out.println("请求结果：code = " + WsXModelDto.GSON.fromJson(text, JsonParse.class).header.code);
                System.out.println("请求返回：" + text);
                System.out.println("*******************");
                
                JsonParse myJsonParse = WsXModelDto.GSON.fromJson(text, JsonParse.class);
                
                // 推送消息给前端，添加service字段标识
                try {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(text);
                    jsonObject.put("service", serviceType);
                    
                    // 检查是否是测试用例生成的响应
                    if ("test_case_generator".equals(serviceType) && jsonObject.containsKey("payload")) {
                        com.alibaba.fastjson.JSONObject payload = jsonObject.getJSONObject("payload");
                        if (payload.containsKey("choices")) {
                            com.alibaba.fastjson.JSONObject choices = payload.getJSONObject("choices");
                            if (choices.containsKey("text")) {
                                com.alibaba.fastjson.JSONArray textArray = choices.getJSONArray("text");
                                if (!textArray.isEmpty()) {
                                    com.alibaba.fastjson.JSONObject textObj = textArray.getJSONObject(0);
                                    if (textObj.containsKey("content")) {
                                        String content = textObj.getString("content");
                                        // 检查内容是否是JSON格式的测试用例
                                        if (content.trim().startsWith("{") && content.trim().endsWith("}")) {
                                            try {
                                                // 尝试解析为JSON
                                                com.alibaba.fastjson.JSONObject testCasesJson = com.alibaba.fastjson.JSONObject.parseObject(content);
                                                if (testCasesJson.containsKey("test_cases")) {
                                                    // 是JSON格式的测试用例，添加type字段
                                                    jsonObject.put("type", "test_cases");
                                                    // 将测试用例内容作为单独字段
                                                    jsonObject.put("test_cases", testCasesJson.get("test_cases"));
                                                }
                                            } catch (Exception e) {
                                                // 不是有效的JSON，保持原样
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    WebSocketController.broadcast(jsonObject.toJSONString());
                } catch (Exception e) {
                    // 如果解析失败，直接发送原始消息
                    WebSocketController.broadcast(text);
                }
                
                if (myJsonParse.header.code != 0) {
                    System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
                    System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
                    webSocket.close(1000, "");
                }
                if (myJsonParse.header.status == 2) {
                    // 可以关闭连接，释放资源
                    System.out.println("*************************************************************************************");
                    wsCloseFlag = true; // 打开释放信号
                }
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                try {
                    if (null != response) {
                        int code = response.code();
                        System.out.println("onFailure code:" + code);
                        assert response.body() != null;
                        System.out.println("onFailure body:" + response.body().string());
                        if (101 != code) {
                            System.out.println("connection failed");
                            // 移除System.exit(0)，避免服务崩溃
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 不发送失败消息到前端，避免在聊天框中显示
            }
        }); // 调用WS入口
    }

    // 主函数（保留用于测试）
    public static void main(String[] args) throws Exception {
        System.out.println("请使用 Spring 容器初始化 B_WsXModel");
    }

    // 线程来发送音频与参数
    class MyThread extends Thread {
        private final WebSocket webSocket;
        private final RoleContent roleContent;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
            // 默认问题
            this.roleContent = new RoleContent();
            this.roleContent.setRole("user");
            this.roleContent.setContent(NEW_QUESTION);
        }

        public MyThread(WebSocket webSocket, RoleContent roleContent) {
            this.webSocket = webSocket;
            this.roleContent = roleContent;
        }

        public void run() {
            try {
                JSONObject requestJson = new JSONObject();

                JSONObject header = new JSONObject();  // header参数
                header.put("app_id", B_WsXModel.this.appid);
                header.put("uid", UUID.randomUUID().toString().substring(0, 10));

                JSONObject parameter = new JSONObject(); // parameter参数
                JSONObject chat = new JSONObject();
                chat.put("domain", B_WsXModel.this.domain);
                chat.put("temperature", 0.5);
                chat.put("max_tokens", 4096);
                parameter.put("chat", chat);

                JSONObject payload = new JSONObject(); // payload参数
                JSONObject message = new JSONObject();
                JSONArray text = new JSONArray();

                // 使用传入的 roleContent 或默认值
                text.add(JSON.toJSON(roleContent));

                message.put("text", text);
                payload.put("message", message);

                requestJson.put("header", header); // 组合
                requestJson.put("parameter", parameter);
                requestJson.put("payload", payload);
                // System.err.println(requestJson); // 可以打印看每次的传参明细
                webSocket.send(requestJson.toString());
                // 等待服务端返回完毕后关闭
                while (!wsCloseFlag) {
                    Thread.sleep(200);
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        System.out.print("大模型：");
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        // 输出响应日志
        System.out.println("请求结果：code = " + WsXModelDto.GSON.fromJson(text, JsonParse.class).header.code);
        System.out.println("请求返回：" + text);
        System.out.println("*******************");
        
        JsonParse myJsonParse = WsXModelDto.GSON.fromJson(text, JsonParse.class);
        
        // 推送消息给前端，添加service字段标识
        try {
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(text);
            jsonObject.put("service", "ai_chat");
            WebSocketController.broadcast(jsonObject.toJSONString());
        } catch (Exception e) {
            // 如果解析失败，直接发送原始消息
            WebSocketController.broadcast(text);
        }
        
        if (myJsonParse.header.code != 0) {
            System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
            System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
            webSocket.close(1000, "");
        }
        if (myJsonParse.header.status == 2) {
            // 可以关闭连接，释放资源
            System.out.println("*************************************************************************************");
            wsCloseFlag = true; // 打开释放信号
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                assert response.body() != null;
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    // 移除System.exit(0)，避免服务崩溃
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 不发送失败消息到前端，避免在聊天框中显示
    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }
}