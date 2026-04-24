package com.grape.grape.model.prompt.ai;

public class ThresholdCheck {
    private double threshold;
    private double actualScore;
    private boolean passed;
    
    // Getters and Setters
    public double getThreshold() {
        return threshold;
    }
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    public double getActualScore() {
        return actualScore;
    }
    public void setActualScore(double actualScore) {
        this.actualScore = actualScore;
    }
    public boolean isPassed() {
        return passed;
    }
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
