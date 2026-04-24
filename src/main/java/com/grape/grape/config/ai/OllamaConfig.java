package com.grape.grape.config.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OllamaConfig {
    @Value("${ollama.host}")
    private String host;

    @Value("${ollama.port}")
    private int port;

    @Value("${ollama.base.url}")
    private String baseUrl;

    @Value("${ollama.api.embeddings}")
    private String embeddingsApi;

    @Value("${ollama.api.generate}")
    private String generateApi;

    @Value("${ollama.embedding.model}")
    private String embeddingModel;

    @Value("${ollama.embedding.dimension}")
    private int embeddingDimension;

    @Value("${ollama.general.model}")
    private String generalModel;

    @Value("${ollama.timeout.connect}")
    private int connectTimeout;

    @Value("${ollama.timeout.read}")
    private int readTimeout;

    @Value("${ollama.timeout.write}")
    private int writeTimeout;

    @Value("${ollama.batch.size}")
    private int batchSize;

    @Value("${ollama.max.retries}")
    private int maxRetries;

    // Getters
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getEmbeddingsApi() {
        return embeddingsApi;
    }

    public String getGenerateApi() {
        return generateApi;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    public String getGeneralModel() {
        return generalModel;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getMaxRetries() {
        return maxRetries;
    }
}
