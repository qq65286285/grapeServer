package com.grape.grape.model.vo;

import java.util.List;

/**
 * 测试用例生成请求参数
 */
public class TestCaseGenerateRequest {
    private String module;
    private String userStory;
    private String acceptanceCriteria;
    private String boundaryConditions;
    private String relatedModules;
    private List<String> testDimensions;
    private String caseType;
    private int caseCount;
    private boolean referenceMode;
    private float similarityThreshold;
    private String generateMode;
    private String caseTemplate;
    private List<String> coverageRequirements;

    // getter和setter方法
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getUserStory() {
        return userStory;
    }

    public void setUserStory(String userStory) {
        this.userStory = userStory;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public String getBoundaryConditions() {
        return boundaryConditions;
    }

    public void setBoundaryConditions(String boundaryConditions) {
        this.boundaryConditions = boundaryConditions;
    }

    public String getRelatedModules() {
        return relatedModules;
    }

    public void setRelatedModules(String relatedModules) {
        this.relatedModules = relatedModules;
    }

    public List<String> getTestDimensions() {
        return testDimensions;
    }

    public void setTestDimensions(List<String> testDimensions) {
        this.testDimensions = testDimensions;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public int getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(int caseCount) {
        this.caseCount = caseCount;
    }

    public boolean isReferenceMode() {
        return referenceMode;
    }

    public void setReferenceMode(boolean referenceMode) {
        this.referenceMode = referenceMode;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(float similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public String getGenerateMode() {
        return generateMode;
    }

    public void setGenerateMode(String generateMode) {
        this.generateMode = generateMode;
    }

    public String getCaseTemplate() {
        return caseTemplate;
    }

    public void setCaseTemplate(String caseTemplate) {
        this.caseTemplate = caseTemplate;
    }

    public List<String> getCoverageRequirements() {
        return coverageRequirements;
    }

    public void setCoverageRequirements(List<String> coverageRequirements) {
        this.coverageRequirements = coverageRequirements;
    }
}