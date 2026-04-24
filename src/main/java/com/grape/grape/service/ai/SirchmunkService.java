package com.grape.grape.service.ai;

import com.grape.grape.model.prompt.ai.SirchmunkSearchMode;
import com.grape.grape.model.prompt.ai.SirchmunkSearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class SirchmunkService {

    @Value("${sirchmunk.endpoint}")
    private String sirchmunkEndpoint;

    @Value("${sirchmunk.default.path}")
    private String defaultPath;

    private final RestTemplate restTemplate;

    public SirchmunkService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 搜索接口
     * @param query 搜索查询
     * @param mode 搜索模式：DEEP 或 FAST
     * @param paths 搜索路径
     * @return 搜索结果的JSON字符串
     */
    public String search(String query, String mode, Object paths) {
        try {
            // 如果paths为空，使用配置文件中的默认路径
            if (paths == null || (paths instanceof String && ((String) paths).isEmpty())) {
                paths = defaultPath;
            }
            
            // 构建请求URL
            String url = sirchmunkEndpoint + "search";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("paths", paths);
            requestBody.put("mode", mode.toUpperCase());
            requestBody.put("max_depth", 0);
            requestBody.put("top_k_files", 0);
            requestBody.put("max_loops", 0);
            requestBody.put("max_token_budget", 0);
            requestBody.put("enable_dir_scan", true);
            requestBody.put("include_patterns", new String[]{});
            requestBody.put("exclude_patterns", new String[]{});
            requestBody.put("return_context", false);
            
            // 发送POST请求
            String response = restTemplate.postForObject(url, requestBody, String.class);
            
            return response;
        } catch (Exception e) {
            System.out.println("Error calling Sirchmunk API: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 搜索接口（默认路径）
     * @param query 搜索查询
     * @param mode 搜索模式：DEEP 或 FAST
     * @return 搜索结果的JSON字符串
     */
    public String search(String query, String mode) {
        // 使用从配置文件中读取的默认搜索路径
        return search(query, mode, defaultPath);
    }

    /**
     * 搜索接口（默认 DEEP 模式）
     * @param query 搜索查询
     * @return 搜索结果的JSON字符串
     */
    public String search(String query) {
        // 使用默认的 DEEP 模式
        return search(query, SirchmunkSearchMode.DEEP.getValue(), defaultPath);
    }

    /**
     * 搜索接口（使用枚举类型）
     * @param query 搜索查询
     * @param mode 搜索模式枚举
     * @return 搜索结果的JSON字符串
     */
    public String search(String query, SirchmunkSearchMode mode) {
        return search(query, mode.getValue(), defaultPath);
    }

    /**
     * 搜索接口（使用枚举类型，自定义路径）
     * @param query 搜索查询
     * @param mode 搜索模式枚举
     * @param paths 搜索路径
     * @return 搜索结果的JSON字符串
     */
    public String search(String query, SirchmunkSearchMode mode, Object paths) {
        return search(query, mode.getValue(), paths);
    }

    /**
     * 搜索接口（返回对象，默认 DEEP 模式）
     * @param query 搜索查询
     * @return SirchmunkSearchResult 对象
     */
    public SirchmunkSearchResult searchForObject(String query) {
        return searchForObject(query, SirchmunkSearchMode.DEEP.getValue(), defaultPath);
    }

    /**
     * 搜索接口（返回对象）
     * @param query 搜索查询
     * @param mode 搜索模式：DEEP 或 FILENAME_ONLY
     * @return SirchmunkSearchResult 对象
     */
    public SirchmunkSearchResult searchForObject(String query, String mode) {
        return searchForObject(query, mode, defaultPath);
    }

    /**
     * 搜索接口（返回对象）
     * @param query 搜索查询
     * @param mode 搜索模式枚举
     * @return SirchmunkSearchResult 对象
     */
    public SirchmunkSearchResult searchForObject(String query, SirchmunkSearchMode mode) {
        return searchForObject(query, mode.getValue(), defaultPath);
    }

    /**
     * 搜索接口（返回对象）
     * @param query 搜索查询
     * @param mode 搜索模式枚举
     * @param paths 搜索路径
     * @return SirchmunkSearchResult 对象
     */
    public SirchmunkSearchResult searchForObject(String query, SirchmunkSearchMode mode, Object paths) {
        return searchForObject(query, mode.getValue(), paths);
    }

    /**
     * 搜索接口（内部方法，返回对象）
     * @param query 搜索查询
     * @param mode 搜索模式
     * @param paths 搜索路径
     * @return SirchmunkSearchResult 对象
     */
    private SirchmunkSearchResult searchForObject(String query, String mode, Object paths) {
        try {
            if (paths == null || (paths instanceof String && ((String) paths).isEmpty())) {
                paths = defaultPath;
            }

            String url = sirchmunkEndpoint + "search";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("paths", paths);
            requestBody.put("mode", mode.toUpperCase());
            requestBody.put("max_depth", 0);
            requestBody.put("top_k_files", 0);
            requestBody.put("max_loops", 0);
            requestBody.put("max_token_budget", 0);
            requestBody.put("enable_dir_scan", true);
            requestBody.put("include_patterns", new String[]{});
            requestBody.put("exclude_patterns", new String[]{});
            requestBody.put("return_context", false);
            // System.out.println("Request Body: " + JSONUtil.toJsonPrettyStr(requestBody));
            // System.out.println("URL: " + url);
            
            // 设置请求头为JSON格式
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(requestBody), headers);
            
            SirchmunkSearchResult result = restTemplate.postForObject(url, entity, SirchmunkSearchResult.class);
            return result;
        } catch (Exception e) {
            System.out.println("Error calling Sirchmunk API: " + e.getMessage());
            e.printStackTrace();
            SirchmunkSearchResult errorResult = new SirchmunkSearchResult();
            errorResult.setSuccess(false);
            errorResult.setError(e.getMessage());
            return errorResult;
        }
    }
}