package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * 测试用例表 实体类。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( "test_cases")
public class Cases implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 测试用例唯一ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

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
     * 状态（0-未执行，1-已完成，2-已失败）
     */
    private int status;

    /**
     * 当前版本号，初始为1
     */
    private Integer version = 1;

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
     * 所属文件夹ID
     */
    private Integer folderId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 逻辑删除（存储删除时间戳，null表示未删除）
     */
    @Column(isLogicDelete = true)
    private Long isDeleted;
}
