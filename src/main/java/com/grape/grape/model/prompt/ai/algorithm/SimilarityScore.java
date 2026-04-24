package com.grape.grape.model.prompt.ai.algorithm;

import java.util.Map;

import com.grape.grape.model.prompt.ai.TestCase;

public class SimilarityScore {
    private TestCase testCase;
    private double finalScore;
    private double originalVectorScore;
    private Map<String, Double> fieldScores;
    private Map<String, Double> fieldWeights;
    private Map<String, FieldDetail> fieldDetails;
    private boolean passedHardFilter;
    private String filterFailureReason;
    private ScoringMetadata scoringMetadata;
    
    // Getters and Setters
    public TestCase getTestCase() {
        return testCase;
    }
    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }
    public double getFinalScore() {
        return finalScore;
    }
    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }
    public double getOriginalVectorScore() {
        return originalVectorScore;
    }
    public void setOriginalVectorScore(double originalVectorScore) {
        this.originalVectorScore = originalVectorScore;
    }
    public Map<String, Double> getFieldScores() {
        return fieldScores;
    }
    public void setFieldScores(Map<String, Double> fieldScores) {
        this.fieldScores = fieldScores;
    }
    public Map<String, Double> getFieldWeights() {
        return fieldWeights;
    }
    public void setFieldWeights(Map<String, Double> fieldWeights) {
        this.fieldWeights = fieldWeights;
    }
    public Map<String, FieldDetail> getFieldDetails() {
        return fieldDetails;
    }
    public void setFieldDetails(Map<String, FieldDetail> fieldDetails) {
        this.fieldDetails = fieldDetails;
    }
    public boolean isPassedHardFilter() {
        return passedHardFilter;
    }
    public void setPassedHardFilter(boolean passedHardFilter) {
        this.passedHardFilter = passedHardFilter;
    }
    public String getFilterFailureReason() {
        return filterFailureReason;
    }
    public void setFilterFailureReason(String filterFailureReason) {
        this.filterFailureReason = filterFailureReason;
    }
    public ScoringMetadata getScoringMetadata() {
        return scoringMetadata;
    }
    public void setScoringMetadata(ScoringMetadata scoringMetadata) {
        this.scoringMetadata = scoringMetadata;
    }
}
