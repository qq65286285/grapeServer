package com.grape.grape.model.prompt.ai.algorithm;

import java.util.List;

public class FieldDetail {
    private String strategy;
    private String testCaseText;
    private String bestMatch;
    private int bestMatchIndex;
    private double bestScore;
    private int totalCompared;
    private List<Match> allMatches;
    private ThresholdCheck thresholdCheck;
    private double averageScore;
    private List<Match> matches;
    private List<String> testCaseTexts;
    private double sumScore;
    private double normalizedSumScore;
    private String normalizationMethod;
    private double maxPossibleScore;
    private String expected;
    private String actual;
    private boolean isMatch;
    private List<String> expectedTags;
    private List<String> actualTags;
    private List<String> matchedTags;
    private int matchCount;
    private int totalExpected;
    private double matchRatio;
    private double score;
    
    // Getters and Setters
    public String getStrategy() {
        return strategy;
    }
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
    public String getTestCaseText() {
        return testCaseText;
    }
    public void setTestCaseText(String testCaseText) {
        this.testCaseText = testCaseText;
    }
    public String getBestMatch() {
        return bestMatch;
    }
    public void setBestMatch(String bestMatch) {
        this.bestMatch = bestMatch;
    }
    public int getBestMatchIndex() {
        return bestMatchIndex;
    }
    public void setBestMatchIndex(int bestMatchIndex) {
        this.bestMatchIndex = bestMatchIndex;
    }
    public double getBestScore() {
        return bestScore;
    }
    public void setBestScore(double bestScore) {
        this.bestScore = bestScore;
    }
    public int getTotalCompared() {
        return totalCompared;
    }
    public void setTotalCompared(int totalCompared) {
        this.totalCompared = totalCompared;
    }
    public List<Match> getAllMatches() {
        return allMatches;
    }
    public void setAllMatches(List<Match> allMatches) {
        this.allMatches = allMatches;
    }
    public ThresholdCheck getThresholdCheck() {
        return thresholdCheck;
    }
    public void setThresholdCheck(ThresholdCheck thresholdCheck) {
        this.thresholdCheck = thresholdCheck;
    }
    public double getAverageScore() {
        return averageScore;
    }
    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
    public List<Match> getMatches() {
        return matches;
    }
    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
    public List<String> getTestCaseTexts() {
        return testCaseTexts;
    }
    public void setTestCaseTexts(List<String> testCaseTexts) {
        this.testCaseTexts = testCaseTexts;
    }
    public double getSumScore() {
        return sumScore;
    }
    public void setSumScore(double sumScore) {
        this.sumScore = sumScore;
    }
    public double getNormalizedSumScore() {
        return normalizedSumScore;
    }
    public void setNormalizedSumScore(double normalizedSumScore) {
        this.normalizedSumScore = normalizedSumScore;
    }
    public String getNormalizationMethod() {
        return normalizationMethod;
    }
    public void setNormalizationMethod(String normalizationMethod) {
        this.normalizationMethod = normalizationMethod;
    }
    public double getMaxPossibleScore() {
        return maxPossibleScore;
    }
    public void setMaxPossibleScore(double maxPossibleScore) {
        this.maxPossibleScore = maxPossibleScore;
    }
    public String getExpected() {
        return expected;
    }
    public void setExpected(String expected) {
        this.expected = expected;
    }
    public String getActual() {
        return actual;
    }
    public void setActual(String actual) {
        this.actual = actual;
    }
    public boolean isMatch() {
        return isMatch;
    }
    public void setMatch(boolean match) {
        isMatch = match;
    }
    public List<String> getExpectedTags() {
        return expectedTags;
    }
    public void setExpectedTags(List<String> expectedTags) {
        this.expectedTags = expectedTags;
    }
    public List<String> getActualTags() {
        return actualTags;
    }
    public void setActualTags(List<String> actualTags) {
        this.actualTags = actualTags;
    }
    public List<String> getMatchedTags() {
        return matchedTags;
    }
    public void setMatchedTags(List<String> matchedTags) {
        this.matchedTags = matchedTags;
    }
    public int getMatchCount() {
        return matchCount;
    }
    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }
    public int getTotalExpected() {
        return totalExpected;
    }
    public void setTotalExpected(int totalExpected) {
        this.totalExpected = totalExpected;
    }
    public double getMatchRatio() {
        return matchRatio;
    }
    public void setMatchRatio(double matchRatio) {
        this.matchRatio = matchRatio;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
}
