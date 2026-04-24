package com.grape.grape.config.ai;


import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorDatabaseConfig {

    @Value("${milvus.host}")
    private String milvusHost;

    @Value("${milvus.port}")
    private int milvusPort;

    @Value("${milvus.username}")
    private String milvusUsername;

    @Value("${milvus.password}")
    private String milvusPassword;

    @Bean
    public MilvusClient milvusClient() {
        try {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(milvusHost)
                    .withPort(milvusPort)
                    .build();
            MilvusClient client = new MilvusServiceClient(connectParam);
            System.out.println("Milvus client initialized successfully");
            return client;
        } catch (Exception e) {
            System.out.println("Failed to connect to Milvus: " + e.getMessage());
            // 创建一个默认的连接参数，确保bean能够注册
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost("localhost")
                    .withPort(19530)
                    .build();
            MilvusClient client = new MilvusServiceClient(connectParam);
            return client;
        }
    }
}
