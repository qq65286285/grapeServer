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

/**
 * 产品表 实体类。
 *
 * @author Administrator
 * @since 2025-02-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 执行记录唯一ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 创建人ID
     */
    private Integer createdBy;

    /**
     * 更新人ID
     */
    private Integer updatedBy;

    /**
     * 创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;

    /**
     * 逻辑删除标记（0: 未删除, 1: 已删除）
     */
    @Column(isLogicDelete = true)
    private Integer isDeleted = 0;

    /**
     * 乐观锁版本号（默认值为1）
     */
    private Integer revision = 1;

    /**
     * 发版时间（实际）
     */
    private Long actualLaunchTime;

    /**
     * 发版时间（计划）
     */
    private Long planLaunchTime;

    /**
     * 版本名称
     */
    private String productName;

    /**
     * 版本号
     */
    private String productVersion;

    /**
     * 文件夹ID
     */
    private int folderId;
}
