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
 * 项目表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_project")
public class TestProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 项目编码
     */
    private String projectCode;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 状态: 1-进行中, 2-已完成, 3-已暂停
     */
    private Integer status;

    /**
     * 负责人ID
     */
    private String ownerId;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}