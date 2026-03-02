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
 * 测试用例实体类
 * 对应数据库中的test_cases表，存储测试用例的基本信息
 * 
 * 主要功能：
 * 1. 存储测试用例的基本信息（标题、描述、优先级等）
 * 2. 记录测试用例的执行状态
 * 3. 支持版本控制（每次更新自动递增版本号）
 * 4. 记录创建和更新信息（人、时间）
 * 5. 支持逻辑删除（软删除）
 * 
 * 数据库表结构：
 * 表名：test_cases
 * 主要字段：
 * - id: 测试用例ID（主键，自增）
 * - case_number: 用例编号（唯一）
 * - title: 测试用例标题
 * - description: 测试用例描述
 * - priority: 优先级（1-低，2-中，3-高）
 * - status: 状态（0-未执行，1-已完成，2-已失败）
 * - version: 版本号（初始为1，每次更新递增）
 * - environment_id: 测试环境ID
 * - expected_result: 预期结果
 * - module: 所属模块
 * - folder_id: 所属文件夹ID
 * - remark: 备注
 * - created_by: 创建人ID
 * - created_at: 创建时间（时间戳）
 * - updated_by: 更新人ID
 * - updated_at: 更新时间（时间戳）
 * - is_deleted: 逻辑删除标记（删除时间戳）
 * 
 * 索引说明：
 * - PRIMARY KEY: id
 * - UNIQUE INDEX: case_number（确保用例编号唯一）
 * - INDEX: folder_id（文件夹查询）
 * - INDEX: status（状态过滤）
 * - INDEX: priority（优先级排序）
 * 
 * @author grape-team
 * @since 2025-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_cases")
public class Cases implements Serializable {

    /**
     * 序列化版本号
     * 用于Java对象的序列化和反序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 测试用例唯一ID
     * 
     * 功能说明：
     * - 测试用例在系统中的唯一标识符
     * - 使用数据库自增ID生成
     * - 作为test_cases表的主键
     * - 被多个关联表引用
     * 
     * 数据库映射：
     * - 列名：id
     * - 类型：INT
     * - 约束：PRIMARY KEY, AUTO_INCREMENT, NOT NULL
     * - 默认值：自动递增
     * 
     * 业务规则：
     * - 创建时由数据库自动生成
     * - 一旦创建不可修改
     * - 用于与其他表的关联查询
     * - 被以下表引用：
     *   - test_case_steps.test_case_id（测试步骤）
     *   - case_versions.test_case_id（版本历史）
     *   - case_vector_sync.test_case_id（向量同步记录）
     * 
     * @Id 主键注解
     * @KeyType.Auto 自增键类型注解
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 用例编号
     * 
     * 功能说明：
     * - 测试用例的业务编号
     * - 用于业务层面的唯一标识
     * - 格式通常为：模块前缀-序号（如：TC-001）
     * 
     * 数据库映射：
     * - 列名：case_number
     * - 类型：VARCHAR(50)
     * - 约束：UNIQUE, NOT NULL
     * - 默认值：无（必须手动指定）
     * 
     * 业务规则：
     * - 长度限制：1-50个字符
     * - 允许字符：字母、数字、下划线、连字符
     * - 必须唯一，重复的用例编号会导致保存失败
     * - 建议格式：TC-模块-序号（如：TC-LOGIN-001）
     * - 创建后可以修改，但保持唯一性
     * 
     * 使用场景：
     * - 业务流程跟踪
     * - 测试报告生成
     * - 需求关联
     * - 跨系统引用
     */
    private String caseNumber;

    /**
     * 测试用例标题
     * 
     * 功能说明：
     * - 测试用例的简要描述
     * - 清晰表达测试的目的和范围
     * - 用于列表展示和搜索
     * 
     * 数据库映射：
     * - 列名：title
     * - 类型：VARCHAR(200)
     * - 约束：NOT NULL
     * - 默认值：无（必须手动指定）
     * 
     * 业务规则：
     * - 长度限制：1-200个字符
     * - 应简洁明了，包含测试要点
     * - 示例："用户登录-正常流程"
     * - 示例："用户注册-手机号格式验证"
     * - 必须填写，不能为空
     * 
     * 使用场景：
     * - 测试用例列表展示
     * - 模糊搜索
     * - 测试报告标题
     * - 需求跟踪
     */
    private String title;

    /**
     * 测试用例描述
     * 
     * 功能说明：
     * - 详细描述测试用例的测试内容
     * - 包含测试的目的、范围、前置条件等
     * - 可以包含测试步骤的简要说明
     * 
     * 数据库映射：
     * - 列名：description
     * - 类型：TEXT
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 可以使用Markdown格式
     * - 可以包含HTML标签
     * - 建议包含：
     *   - 测试目的
     *   - 测试范围
     *   - 前置条件
     *   - 测试数据
     *   - 注意事项
     * - 长度限制：TEXT类型，理论上无限制
     * 
     * 使用场景：
     * - 测试用例详情展示
     * - 测试用例理解
     * - 测试报告生成
     * - 新人培训
     */
    private String description;

    /**
     * 优先级
     * 
     * 功能说明：
     * - 标识测试用例的执行优先级
     * - 用于测试排期和资源分配
     * - 影响测试执行的先后顺序
     * 
     * 数据库映射：
     * - 列名：priority
     * - 类型：TINYINT
     * - 约束：NOT NULL
     * - 默认值：2（中等）
     * 
     * 业务规则：
     * - 取值范围：1-3
     * - 1: Low（低优先级）
     * - 2: Medium（中等优先级）
     * - 3: High（高优先级）
     * - 默认值：2（中等）
     * 
     * 使用场景：
     * - 测试排期
     * - 资源分配
     * - 测试执行优先级排序
     * - 测试报告统计（按优先级统计）
     */
    private Integer priority;

    /**
     * 测试状态
     * 
     * 功能说明：
     * - 标识测试用例的执行状态
     * - 用于跟踪测试进度
     * - 用于测试报告统计
     * 
     * 数据库映射：
     * - 列名：status
     * - 类型：TINYINT
     * - 约束：NOT NULL
     * - 默认值：0（未执行）
     * 
     * 业务规则：
     * - 取值范围：0-2
     * - 0: Pending（未执行，初始状态）
     * - 1: Completed（已完成，测试通过）
     * - 2: Failed（已失败，测试不通过）
     * - 默认值：0（未执行）
     * - 状态流转：0 -> 1 或 0 -> 2
     * 
     * 使用场景：
     * - 测试进度跟踪
     * - 测试报告统计（按状态统计）
     * - 质量分析
     * - 测试执行筛选
     */
    private int status;

    /**
     * 当前版本号
     * 
     * 功能说明：
     * - 标识测试用例的当前版本
     * - 每次更新测试用例时自动递增
     * - 用于版本控制和回滚
     * - 与case_versions表关联，记录版本历史
     * 
     * 数据库映射：
     * - 列名：version
     * - 类型：INT
     * - 约束：NOT NULL
     * - 默认值：1（初始版本）
     * 
     * 业务规则：
     * - 初始值：1
     * - 每次更新测试用例时自动递增
     * - 修改标题、描述、步骤等都会触发版本递增
     * - 不允许手动修改
     * - 用于版本回滚（指定版本号回滚到历史版本）
     * 
     * 使用场景：
     * - 版本控制
     * - 版本回滚
     * - 变更历史追踪
     * - 版本对比
     * 
     * 关联表：
     * - case_versions: 记录每个版本的历史数据
     */
    private Integer version = 1;

    /**
     * 测试环境ID
     * 
     * 功能说明：
     * - 标识测试用例所属的测试环境
     * - 关联到测试环境配置表
     * - 不同环境可能有不同的配置和数据
     * 
     * 数据库映射：
     * - 列名：environment_id
     * - 类型：INT
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 可以为空（通用测试用例）
     * - 关联到environment表
     * - 一个测试用例可以属于多个环境
     * - 不同环境可以有不同的测试数据
     * 
     * 使用场景：
     * - 环境隔离
     * - 多环境测试
     * - 环境配置管理
     */
    private Integer environmentId;

    /**
     * 预期结果
     * 
     * 功能说明：
     * - 描述测试用例的预期执行结果
     * - 用于验证测试是否通过
     * - 可以包含多个预期结果
     * 
     * 数据库映射：
     * - 列名：expected_result
     * - 类型：TEXT
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 可以使用Markdown格式
     * - 可以包含HTML标签
     * - 建议包含：
     *   - 预期返回值
     *   - 预期状态码
     *   - 预期数据库状态
     *   - 预期界面变化
     *   - 预期日志输出
     * 
     * 使用场景：
     * - 测试结果验证
     * - 自动化测试断言
     * - 测试报告生成
     * - 测试用例理解
     */
    private String expectedResult;

    /**
     * 所属模块
     * 
     * 功能说明：
     * - 标识测试用例所属的功能模块
     * - 用于模块级别的测试统计
     * - 用于测试用例分类和筛选
     * 
     * 数据库映射：
     * - 列名：module
     * - 类型：VARCHAR(100)
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 长度限制：1-100个字符
     * - 示例："用户管理"
     * - 示例："订单模块"
     * - 示例："支付系统"
     * - 可以为空
     * 
     * 使用场景：
     * - 测试用例分类
     * - 模块级统计
     * - 测试用例筛选
     * - 测试报告分组
     */
    private String module;

    /**
     * 所属文件夹ID
     * 
     * 功能说明：
     * - 标识测试用例所属的文件夹
     * - 支持文件夹的树形结构
     * - 用于测试用例的层级管理和分类
     * - 关联到test_case_folder表
     * 
     * 数据库映射：
     * - 列名：folder_id
     * - 类型：INT
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 关联到test_case_folder表
     * - 支持文件夹的树形结构（parent_id）
     * - 可以为空（未分类）
     * - 一个测试用例只能属于一个文件夹
     * - 查询时会递归查询子文件夹中的用例
     * 
     * 使用场景：
     * - 测试用例分类
     * - 层级管理
     * - 权限控制（文件夹级别）
     * - 测试用例导航
     * 
     * 关联表：
     * - test_case_folder: 测试用例文件夹表
     */
    private Integer folderId;

    /**
     * 备注
     * 
     * 功能说明：
     * - 用于记录测试用例的补充信息
     * - 可以包含注意事项、已知问题等
     * - 支持多行文本
     * 
     * 数据库映射：
     * - 列名：remark
     * - 类型：TEXT
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 可以使用Markdown格式
     * - 可以包含HTML标签
     * - 可以包含：
     *   - 注意事项
     *   - 已知问题
     *   - 特殊说明
     *   - 相关链接
     *   - 参考文档
     * - 可以为空
     * 
     * 使用场景：
     * - 测试用例说明
     * - 测试注意事项
     * - 已知问题记录
     * - 知识库
     */
    private String remark;

    /**
     * 创建人ID
     * 
     * 功能说明：
     * - 记录创建该测试用例的用户ID
     * - 用于数据审计和追踪
     * - 关联到user表
     * 
     * 数据库映射：
     * - 列名：created_by
     * - 类型：VARCHAR(36)
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 关联到user表的id字段
     * - 创建时自动设置为当前登录用户ID
     * - 创建后不可修改
     * - 可以为空（系统自动创建）
     * 
     * 使用场景：
     * - 数据审计
     * - 创建人查询
     * - 权限控制
     * - 数据统计
     */
    private String createdBy;

    /**
     * 创建时间
     * 
     * 功能说明：
     * - 记录测试用例的创建时间
     * - 使用时间戳格式存储（毫秒）
     * - 用于数据审计和统计
     * 
     * 数据库映射：
     * - 列名：created_at
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
     * - 数据审计
     * - 创建时间查询
     * - 数据统计（按创建时间统计）
     * - 版本追踪
     */
    private Long createdAt;

    /**
     * 更新时间
     * 
     * 功能说明：
     * - 记录测试用例的最后更新时间
     * - 使用时间戳格式存储（毫秒）
     * - 用于数据审计和统计
     * 
     * 数据库映射：
     * - 列名：updated_at
     * - 类型：BIGINT
     * - 约束：无
     * - 默认值：当前时间戳
     * 
     * 业务规则：
     * - 格式：Unix时间戳（毫秒）
     * - 示例：1640995200000（2022-01-01 00:00:00）
     * - 每次更新时自动设置为当前时间
     * - 版本回滚时也会更新
     * - 用于排序和统计
     * 
     * 使用场景：
     * - 数据审计
     * - 更新时间查询
     * - 数据统计（按更新时间统计）
     * - 最近更新列表
     */
    private Long updatedAt;

    /**
     * 更新人ID
     * 
     * 功能说明：
     * - 记录最后更新该测试用例的用户ID
     * - 用于数据审计和追踪
     * - 关联到user表
     * 
     * 数据库映射：
     * - 列名：updated_by
     * - 类型：VARCHAR(36)
     * - 约束：无
     * - 默认值：NULL
     * 
     * 业务规则：
     * - 关联到user表的id字段
     * - 每次更新时自动设置为当前登录用户ID
     * - 版本回滚时也会更新
     * - 可以为空（系统自动更新）
     * 
     * 使用场景：
     * - 数据审计
     * - 更新人查询
     * - 变更历史追踪
     * - 权限控制
     */
    private String updatedBy;

    /**
     * 逻辑删除标记
     * 
     * 功能说明：
     * - 标识测试用例是否已被逻辑删除
     * - 使用软删除而非物理删除
     * - 存储删除时间戳，NULL表示未删除
     * 
     * 数据库映射：
     * - 列名：is_deleted
     * - 类型：BIGINT
     * - 约束：无
     * - 默认值：NULL（未删除）
     * 
     * 业务规则：
     * - NULL: 未删除（正常状态）
     * - 非NULL: 已删除（存储删除时间戳）
     * - 软删除后数据仍保留在数据库中
     * - 查询时自动过滤已删除的记录
     * - 支持恢复已删除的测试用例
     * 
     * 使用场景：
     * - 软删除
     * - 数据恢复
     * - 数据审计
     * - 垃圾回收
     * 
     * @Column(isLogicDelete = true) 逻辑删除标记注解
     */
    @Column(isLogicDelete = true)
    private Long isDeleted;
}
