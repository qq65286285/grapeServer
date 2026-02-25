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
 * 测试环境配置表 实体类。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_environments")
public class Environments implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 测试环境唯一ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 测试环境名称
     */
    private String name;

    /**
     * 测试环境地址
     */
    private String url;

    /**
     * 测试环境描述
     */
    private String description;

    /**
     * 创建人ID
     */
    private Integer createdBy;

    /**
     * 更新人ID
     */
    private Integer updatedBy;

    /**
     * 乐观锁版本号（默认值为1）
     */
    private Integer revision;

    /**
     * 逻辑删除标记（0: 未删除, 1: 已删除）
     */
    private Integer isDeleted;

    /**
     * 创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;

}
