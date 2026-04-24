package com.grape.grape.model.vo;

import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.component.UserUtils;
import java.util.List;

/**
 * 测试用例请求对象
 * 用于接收和处理测试用例相关的 HTTP 请求参数
 */

import lombok.Data;

@Data
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