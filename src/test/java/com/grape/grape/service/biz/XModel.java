package com.black;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class XModel {
    // 获取地址 https://console.xfyun.cn/services/cbm
    public static String APIPassword = "";

    public static void main(String[] args) {
        String userId = "10284711用户";
        try {
            String url = "https://spark-api-open.xf-yun.com/v2/chat/completions";

            // 创建最外层的JSON对象
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user", userId);
            jsonObject.put("model", "x1");
            // 创建messages数组
            JSONArray messagesArray = new JSONArray();
            // 创建单个消息的JSON对象
            JSONObject messageObject = new JSONObject();
            String temp = "介绍一下科大讯飞";
            System.err.println(temp);
            messageObject.put("role", "user");
            messageObject.put("content", temp);
            messageObject.put("temperature", "0.5");
            // 将单个消息对象添加到messages数组中
            messagesArray.put(messageObject);
            // 将messages数组添加到最外层的JSON对象中
            jsonObject.put("messages", messagesArray);
            // 设置stream属性为true
            jsonObject.put("stream", true);
            jsonObject.put("max_tokens", 4096);
            // System.err.println(jsonObject);

            String header = "Authorization: Bearer" + APIPassword; // 注意此处替换自己的key和secret

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", header);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                System.out.println(inputLine);
            }
            in.close();
            //  System.err.println(userId + " 的文章生成任务内容如下：\n" + response);
            // 流式返回结果打印
            String jsonStr = response.substring(5);
            if (!jsonStr.isEmpty()) {
                // System.out.println(jsonStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
