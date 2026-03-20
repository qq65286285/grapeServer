package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划模板表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_template")
public class TestPlanTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 适用计划类型: 1-项目测试, 2-迭代测试, 3-专项测试
     */
    private Integer planType;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 测试范围模板
     */
    private String testScope;

    /**
     * 测试目标模板
     */
    private String testTarget;

    /**
     * 测试策略模板
     */
    private String testStrategy;

    /**
     * 验收标准模板
     */
    private String acceptanceCriteria;

    /**
     * 默认配置: {"duration_days": 7, "remind_before_days": 1}
     */
    private String defaultConfig;

    /**
     * 用例筛选条件: {"module_ids": [], "priorities": [], "tags": [], "case_types": []}
     */
    private String caseFilter;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 最后使用时间
     */
    private Date lastUsedAt;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}