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
 * 操作权限清单，控制接口/功能访问权限 实体类。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限键（如novel:create）
     */
    private String key;

    /**
     * 权限描述
     */
    private String description;

}
