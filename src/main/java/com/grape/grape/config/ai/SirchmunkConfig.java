package com.grape4j.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SirchmunkConfig {

    @Value("${sirchmunk.endpoint}")
    private String sirchmunkEndpoint;

    @Bean
    public SirchmunkClient sirchmunkClient() {
        return new SirchmunkClient(sirchmunkEndpoint);
    }

    // 简单的Sirchmunk客户端实现
    public static class SirchmunkClient {
        private final String endpoint;

        public SirchmunkClient(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void indexDocument(String document) {
            // 实现文档索引逻辑
            System.out.println("Indexing document: " + document);
            System.out.println("Using HTTP endpoint: " + endpoint);
        }
    }
}
