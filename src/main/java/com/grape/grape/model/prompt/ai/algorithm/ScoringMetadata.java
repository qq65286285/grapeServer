package com.grape.grape.model.prompt.ai.algorithm;

public class ScoringMetadata {
    private String timestamp;
    private String version;
    private int processingTimeMs;
    private String embeddingModel;
    private double vectorDbScore;
    private String finalScoreFormula;
    
    // Getters and Setters
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public int getProcessingTimeMs() {
        return processingTimeMs;
    }
    public void setProcessingTimeMs(int processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    public String getEmbeddingModel() {
        return embeddingModel;
    }
    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }
    public double getVectorDbScore() {
        return vectorDbScore;
    }
    public void setVectorDbScore(double vectorDbScore) {
        this.vectorDbScore = vectorDbScore;
    }
    public String getFinalScoreFormula() {
        return finalScoreFormula;
    }
    public void setFinalScoreFormula(String finalScoreFormula) {
        this.finalScoreFormula = finalScoreFormula;
    }
}
