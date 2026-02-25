package com.grape.grape.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核心用户信息表，存储系统用户基础数据 实体类。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（建议UUID）
     */
    @Id
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private long createTime = new Date().getTime();


    @RelationManyToMany(joinTable = "user_role",
            joinSelfColumn = "user_id", joinTargetColumn = "role_id")
    private List<Role> roles;
}
