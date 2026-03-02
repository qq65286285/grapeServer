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
 * 用户实体类
 * 对应数据库中的user表，存储系统用户的基础信息
 * 
 * 主要功能：
 * 1. 存储用户的账号信息（用户名、密码）
 * 2. 存储用户的联系方式（手机号、邮箱）
 * 3. 记录用户的创建时间
 * 4. 关联用户的角色信息（多对多关系）
 * 
 * 数据库表结构：
 * 表名：user
 * 主要字段：
 * - id: 用户ID（主键，建议使用UUID）
 * - username: 用户名（唯一索引）
 * - password: 加密后的密码（BCrypt加密）
 * - mobile: 手机号
 * - email: 邮箱地址
 * - create_time: 创建时间（时间戳）
 * 
 * 索引说明：
 * - PRIMARY KEY: id
 * - UNIQUE INDEX: username（确保用户名唯一）
 * 
 * @author grape-team
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User implements Serializable {

    /**
     * 序列化版本号
     * 用于Java对象的序列化和反序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 
     * 功能说明：
     * - 用户在系统中的唯一标识符
     * - 建议使用UUID格式（如：123e4567-e89b-12d3-a456-426614174000）
     * - 作为user表的主键
     * - 被user_role关联表引用（user_id字段）
     * 
     * 数据库映射：
     * - 列名：id
     * - 类型：VARCHAR(36) 或 VARCHAR(64)
     * - 约束：PRIMARY KEY, NOT NULL
     * - 默认值：无（必须手动指定）
     * 
     * 业务规则：
     * - 创建用户时必须指定
     * - 一旦创建不可修改
     * - 用于与其他表的关联查询
     * 
     * @Id 主键注解，标识该字段为主键
     */
    @Id
    private String id;

    /**
     * 用户名
     * 
     * 功能说明：
     * - 用户登录系统时使用的唯一标识
     * - 通常使用用户真实姓名或昵称
     * - 在系统中必须唯一
     * - 用于身份验证和权限控制
     * 
     * 数据库映射：
     * - 列名：username
     * - 类型：VARCHAR(50)
     * - 约束：UNIQUE, NOT NULL
     * - 默认值：无（必须手动指定）
     * 
     * 业务规则：
     * - 长度限制：1-50个字符
     * - 允许字符：字母、数字、下划线、中文
     * - 不能包含特殊字符和空格
     * - 必须唯一，重复的用户名会导致注册失败
     * - 创建后可以修改，但保持唯一性
     * 
     * 安全说明：
     * - 用户名是敏感信息，应在日志中脱敏处理
     * - 建议在展示时进行部分隐藏（如：user***）
     */
    private String username;

    /**
     * 加密密码
     * 
     * 功能说明：
     * - 用户登录时使用的密码（加密存储）
     * - 使用BCrypt算法加密存储
     * - 每次加密结果不同（包含随机盐值）
     * - 提高密码安全性，防止明文泄露
     * 
     * 数据库映射：
     * - 列名：password
     * - 类型：VARCHAR(60)
     * - 约束：NOT NULL
     * - 默认值：无（必须手动指定）
     * 
     * 业务规则：
     * - 长度限制：明文密码6-20个字符
     * - 加密后固定长度为60个字符（BCrypt特性）
     * - 必须加密后存储，严禁存储明文密码
     * - 修改密码时重新加密
     * 
     * 安全说明：
     * - 使用BCryptPasswordEncoder加密
     * - 加密强度：BCrypt默认10轮
     * - 验证时使用BCryptPasswordEncoder.matches()
     * - 严禁在日志、API响应中返回密码
     * - 密码字段在序列化时应被忽略
     * 
     * 加密示例：
     * - 明文：password123
     * - 加密后（示例）：$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     */
    private String password;

    /**
     * 手机号
     * 
     * 功能说明：
     * - 用户的联系电话
     * - 用于接收短信验证码和系统通知
     * - 可作为登录账号使用
     * 
     * 数据库映射：
     * - 列名：mobile
     * - 类型：VARCHAR(20)
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 格式要求：11位数字（中国大陆手机号）
     * - 可以为空（选填字段）
     * - 验证规则：^1[3-9]\\d{9}$
     * - 可以为空，但建议填写
     * 
     * 安全说明：
     * - 手机号是敏感信息，应在日志中脱敏处理
     * - 建议在展示时隐藏中间4位（如：138****5678）
     * - 避免在API响应中完整返回
     * 
     * 验证正则：
     * - 大陆手机号：^1[3-9]\\d{9}$
     * - 国际手机号：支持国际区号（如+86）
     */
    private String mobile;

    /**
     * 邮箱
     * 
     * 功能说明：
     * - 用户的电子邮件地址
     * - 用于接收系统通知和找回密码
     * - 可作为登录账号使用
     * 
     * 数据库映射：
     * - 列名：email
     * - 类型：VARCHAR(100)
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 格式要求：符合标准邮箱格式
     * - 可以为空（选填字段）
     * - 验证规则：^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$
     * - 可以为空，但建议填写
     * 
     * 安全说明：
     * - 邮箱是敏感信息，应在日志中脱敏处理
     * - 建议在展示时隐藏@符号前后部分（如：u***@example.com）
     * - 避免在API响应中完整返回
     * 
     * 验证正则：
     * - 标准格式：^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$
     * 
     * 使用场景：
     * - 发送系统通知邮件
     * - 密码重置邮件
     * - 活动推广邮件
     */
    private String email;

    /**
     * 创建时间
     * 
     * 功能说明：
     * - 记录用户账号的创建时间
     * - 使用时间戳格式存储（毫秒）
     * - 用于统计用户注册趋势
     * - 用于数据审计和追踪
     * 
     * 数据库映射：
     * - 列名：create_time
     * - 类型：BIGINT
     * - 约束：无
     * - 默认值：当前时间戳
     * 
     * 业务规则：
     * - 格式：Unix时间戳（毫秒）
     * - 示例：1640995200000（2022-01-01 00:00:00）
     * - 创建时自动设置为当前时间
     * - 创建后不可修改
     * - 用于排序和统计
     * 
     * 使用场景：
     * - 统计每日/每月用户注册量
     * - 用户活跃度分析
     * - 数据备份和恢复
     * - 审计日志查询
     */
    private long createTime = new Date().getTime();

    /**
     * 用户角色列表
     * 
     * 功能说明：
     * - 用户在系统中拥有的所有角色
     * - 用于权限控制和功能访问
     * - 通过多对多关联表user_role关联
     * 
     * 数据库映射：
     * - 关联表：user_role
     * - 本表字段：user_id（本表的id字段）
     * - 目标表字段：role_id（role表的id字段）
     * - 关系类型：多对多
     * 
     * 关联关系：
     * - 一个用户可以拥有多个角色
     * - 一个角色可以分配给多个用户
     * - 通过user_role关联表连接
     * 
     * 业务规则：
     * - 用户至少需要拥有一个角色
     * - 角色定义了用户的权限范围
     * - 权限继承：用户拥有所有角色的权限
     * - 角色冲突时，权限取并集
     * 
     * 使用场景：
     * - 权限控制：根据角色判断用户是否有权限访问某个功能
     * - 功能授权：为用户分配不同的角色以授予相应的功能权限
     * - 角色管理：动态添加或移除用户的角色
     * 
     * @RelationManyToMany 多对多关联注解
     * - joinTable: 关联表名
     * - joinSelfColumn: 本表在关联表中的字段名
     * - joinTargetColumn: 目标表在关联表中的字段名
     */
    @RelationManyToMany(joinTable = "user_role",
            joinSelfColumn = "user_id", joinTargetColumn = "role_id")
    private List<Role> roles;
}
