package com.grape.grape.service.ai;

import com.grape.grape.config.ai.OllamaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OllamaVectorService {

    private final OllamaClient ollamaClient;
    private final OllamaConfig ollamaConfig;

    @Autowired
    public OllamaVectorService(OllamaConfig ollamaConfig, OllamaClient ollamaClient) {
        this.ollamaConfig = ollamaConfig;
        this.ollamaClient = ollamaClient;
    }

    /**
     * 单文本向量化方法
     * @param text 输入文本
     * @return 向量表示
     */
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        List<String> texts = new ArrayList<>();
        texts.add(text);
        List<float[]> vectors = embedBatch(texts);
        return vectors.get(0);
    }

    /**
     * 批量文本向量化方法
     * @param texts 文本列表
     * @return 向量列表
     */
    public List<float[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("Texts cannot be null or empty");
        }

        List<float[]> result = new ArrayList<>();
        int batchSize = ollamaConfig.getBatchSize();

        // 分批处理
        for (int i = 0; i < texts.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, endIndex);
            List<float[]> batchResult = processBatch(batch);
            result.addAll(batchResult);
        }

        return result;
    }

    /**
     * 处理单个批次的文本
     * @param texts 文本列表
     * @return 向量列表
     */
    private List<float[]> processBatch(List<String> texts) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                    "model", ollamaConfig.getEmbeddingModel(),
                    "prompt", String.join("\n", texts)
            );
            // System.out.println("Request body: " + JSONUtil.toJsonStr(requestBody));
            
            // 发送请求并处理响应
            OllamaEmbeddingResponse response = ollamaClient.withRetry(() -> 
                ollamaClient.sendAsyncRequest("/api/embeddings", requestBody, OllamaEmbeddingResponse.class)
            );

            // 解析结果
            if (response != null && response.getEmbedding() != null) {
                List<Double> embeddingList = response.getEmbedding();
                float[] embedding = new float[embeddingList.size()];
                for (int i = 0; i < embeddingList.size(); i++) {
                    embedding[i] = embeddingList.get(i).floatValue();
                }

                // 验证向量维度
                if (embedding.length != ollamaConfig.getEmbeddingDimension()) {
                    throw new IllegalStateException("Embedding dimension mismatch: expected " + 
                            ollamaConfig.getEmbeddingDimension() + ", got " + embedding.length);
                }

                // 为每个文本生成向量（这里假设返回的是所有文本的组合向量，实际应用中需要根据API返回格式调整）
                List<float[]> result = new ArrayList<>();
                for (int i = 0; i < texts.size(); i++) {
                    result.add(embedding);
                }
                return result;
            } else {
                throw new IllegalStateException("Invalid response from Ollama API");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing batch", e);
        }
    }

    /**
     * Ollama API响应类
     */
    private static class OllamaEmbeddingResponse {
        private List<Double> embedding;

        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }
    }
}
