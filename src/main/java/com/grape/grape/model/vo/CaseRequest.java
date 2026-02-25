package com.grape.grape.model.vo;

import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.component.UserUtils;
import java.util.List;

/**
 * 测试用例请求对象
 * 用于接收和处理测试用例相关的 HTTP 请求参数
 */
public class CaseRequest {
    private Integer id;
    private String caseNumber;
    private String title;
    private String description;
    private Integer priority;
    private Integer status;
    private Integer version;
    private Integer environmentId;
    private String module;
    private Integer folderId;
    private String remark;
    private List<TestCaseStep> steps;
    private String createdBy;
    private Long createdAt;
    private Long updatedAt;
    private String updatedBy;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getEnvironmentId() { return environmentId; }
    public void setEnvironmentId(Integer environmentId) { this.environmentId = environmentId; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public Integer getFolderId() { return folderId; }
    public void setFolderId(Integer folderId) { this.folderId = folderId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<TestCaseStep> getSteps() { return steps; }
    public void setSteps(List<TestCaseStep> steps) { this.steps = steps; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    /**
     * 转换为 Cases 实体对象（用于保存操作）
     * @return Cases 实体对象
     */
    public Cases toCases() {
        Cases cases = new Cases();
        cases.setCaseNumber(this.caseNumber);
        cases.setTitle(this.title);
        cases.setDescription(this.description);
        cases.setPriority(this.priority);
        cases.setStatus(this.status != null ? this.status : 0);
        cases.setVersion(this.version != null ? this.version : 1);
        cases.setEnvironmentId(this.environmentId);
        cases.setExpectedResult("");
        cases.setModule(this.module);
        cases.setFolderId(this.folderId);
        cases.setRemark(this.remark);
        
        // 设置创建和更新时间
        long currentTime = System.currentTimeMillis();
        cases.setCreatedAt(currentTime);
        cases.setUpdatedAt(currentTime);
        
        // 设置创建人和更新人
        String currentUser = UserUtils.getCurrentUsername();
        cases.setCreatedBy(currentUser);
        cases.setUpdatedBy(currentUser);
        
        return cases;
    }

    /**
     * 转换为 Cases 实体对象（用于更新操作）
     * @return Cases 实体对象
     */
    public Cases toCasesForUpdate() {
        Cases cases = new Cases();
        cases.setId(this.id);
        cases.setCaseNumber(this.caseNumber);
        cases.setTitle(this.title);
        cases.setDescription(this.description);
        cases.setPriority(this.priority);
        cases.setStatus(this.status != null ? this.status : 0);
        cases.setVersion(this.version != null ? this.version : 1);
        cases.setEnvironmentId(this.environmentId);
        cases.setExpectedResult("");
        cases.setModule(this.module);
        cases.setFolderId(this.folderId);
        cases.setRemark(this.remark);
        
        // 设置更新时间
        cases.setUpdatedAt(System.currentTimeMillis());
        cases.setUpdatedBy(UserUtils.getCurrentUsername());
        
        return cases;
    }
}