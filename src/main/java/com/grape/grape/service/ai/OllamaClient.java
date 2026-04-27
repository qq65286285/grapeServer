package com.grape.grape.service.ai;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class OllamaClient {

    private final String ollamaBaseUrl;
    private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;
    private final int maxRetries;

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Autowired
    public OllamaClient(
            @Value("${ollama.base.url}") String ollamaBaseUrl,
            @Value("${ollama.timeout.connect}") int connectTimeout,
            @Value("${ollama.timeout.read}") int readTimeout,
            @Value("${ollama.timeout.write}") int writeTimeout,
            @Value("${ollama.max.retries}") int maxRetries) {
        this.ollamaBaseUrl = ollamaBaseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        this.maxRetries = maxRetries;

        // 配置RestTemplate的超时设置
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        this.restTemplate = new RestTemplate(factory);

        // 配置WebClient的超时设置
        this.webClient = WebClient.builder()
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                        .responseTimeout(Duration.ofMillis(readTimeout))
                ))
                .build();
    }

    /**
     * 发送同步请求到Ollama API
     * @param endpoint API端点
     * @param requestBody 请求体
     * @return 响应字符串
     */
    public String sendSyncRequest(String endpoint, Map<String, Object> requestBody) {
        try {
            String url = ollamaBaseUrl + endpoint;

            // 发送POST请求
            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity =
                new org.springframework.http.HttpEntity<>(requestBody, getHeaders());

            org.springframework.http.ResponseEntity<String> responseEntity =
                restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, requestEntity, String.class);
            System.out.println("-----------------");
            System.out.println("Response: " + JSONUtil.toJsonPrettyStr(responseEntity));
            System.out.println("-----------------");
            return responseEntity.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error sending sync request to Ollama API: " + e.getMessage(), e);
        }
    }

    /**
     * 获取请求头
     * @return HttpHeaders对象
     */
    private org.springframework.http.HttpHeaders getHeaders() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 发送异步请求到Ollama API
     * @param endpoint API端点
     * @param requestBody 请求体
     * @param responseType 响应类型
     * @param <T> 响应类型泛型
     * @return 响应对象
     */
    public <T> T sendAsyncRequest(String endpoint, Map<String, Object> requestBody, Class<T> responseType) {
        try {
            String url = ollamaBaseUrl + endpoint;
            Mono<T> responseMono = webClient.post()
                    .uri(url)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(Duration.ofMillis(readTimeout));
            return responseMono.block();
        } catch (Exception e) {
            throw new RuntimeException("Error sending async request to Ollama API", e);
        }
    }

    /**
     * 解析JSON响应
     * @param response 响应字符串
     * @return JSONObject对象
     */
    public JSONObject parseJsonResponse(String response) {
        return JSONUtil.parseObj(response);
    }

    /**
     * 重试机制包装方法
     * @param supplier 要执行的操作
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T withRetry(Supplier<T> supplier) {
        int retryCount = 0;
        while (retryCount <= maxRetries) {
            try {
                return supplier.get();
            } catch (Exception e) {
                retryCount++;
                if (retryCount > maxRetries) {
                    throw new RuntimeException("Failed after " + maxRetries + " retries", e);
                }
                System.out.println("Retry " + retryCount + " for Ollama API call");
                try {
                    Thread.sleep(1000 * retryCount); // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        throw new RuntimeException("Failed to execute operation");
    }

    /**
     * 函数式接口，用于withRetry方法
     * @param <T> 返回类型
     */
    @FunctionalInterface
    public interface Supplier<T> {
        T get() throws Exception;
    }
}
