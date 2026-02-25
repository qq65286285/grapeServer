package com.grape.grape.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试用例版本备份表 实体类。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_case_versions")
public class CaseVersions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本记录唯一ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 关联的测试用例ID
     */
    private Integer testCaseId;

    /**
     * 用例编号
     */
    private String caseNumber;

    /**
     * 测试用例标题
     */
    private String title;

    /**
     * 测试用例描述
     */
    private String description;

    /**
     * 优先级（1: Low, 2: Medium, 3: High）
     */
    private Integer priority;

    /**
     * 用例状态（0-未执行，1-已完成，2-已失败）
     */
    private Integer caseState;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 测试环境ID（关联测试环境配置表）
     */
    private Integer environmentId;

    /**
     * 预期结果
     */
    private String expectedResult;

    /**
     * 模块
     */
    private String module;

    /**
     * 备注
     */
    private String remark;

    /**
     * 所属文件夹ID
     */
    private Integer folderId;

    /**
     * 测试用例步骤（JSON格式）
     */
    private String stepsJson;

    /**
     * 乐观锁版本号（默认值为1）
     */
    private Integer revision;

    /**
     * 逻辑删除标记（存储删除时间戳，null表示未删除）
     */
    private Long isDeleted;

    /**
    * 创建人ID
    */
   private String createdBy;

   /**
    * 更新人ID
    */
   private String updatedBy;

    /**
     * 版本创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;

    public CaseVersions getByCaseDao(Cases cases, String stepsJson){
        CaseVersions caseVersions = new CaseVersions();
        caseVersions.setTestCaseId(cases.getId());
        caseVersions.setCaseNumber(cases.getCaseNumber());
        caseVersions.setTitle(cases.getTitle());
        caseVersions.setDescription(cases.getDescription());
        caseVersions.setPriority(cases.getPriority());
        caseVersions.setCaseState(cases.getStatus());
        caseVersions.setVersion(cases.getVersion());
        caseVersions.setEnvironmentId(cases.getEnvironmentId());
        caseVersions.setExpectedResult(cases.getExpectedResult());
        caseVersions.setModule(cases.getModule());
        caseVersions.setFolderId(cases.getFolderId());
        caseVersions.setRemark(cases.getRemark());
        caseVersions.setStepsJson(stepsJson);
        caseVersions.setRevision(cases.getVersion());
        caseVersions.setIsDeleted(cases.getIsDeleted());
        caseVersions.setCreatedBy(cases.getCreatedBy());
        caseVersions.setUpdatedBy(cases.getUpdatedBy());
        caseVersions.setCreatedAt(cases.getCreatedAt());
        caseVersions.setUpdatedAt(cases.getUpdatedAt());
        return caseVersions;

    }
}
