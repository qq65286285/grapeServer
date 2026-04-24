package com.grape.grape.model.prompt.ai.algorithm;

public class Match {
    private String analysisText;
    private double similarity;
    private String testCaseText;
    private double contribution;
    
    // Getters and Setters
    public String getAnalysisText() {
        return analysisText;
    }
    public void setAnalysisText(String analysisText) {
        this.analysisText = analysisText;
    }
    public double getSimilarity() {
        return similarity;
    }
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
    public String getTestCaseText() {
        return testCaseText;
    }
    public void setTestCaseText(String testCaseText) {
        this.testCaseText = testCaseText;
    }
    public double getContribution() {
        return contribution;
    }
    public void setContribution(double contribution) {
        this.contribution = contribution;
    }
}
