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
 * 测试计划标签表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_tag")
public class TestPlanTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签编码
     */
    private String tagCode;

    /**
     * 标签颜色
     */
    private String tagColor;

    /**
     * 标签分类: 1-测试类型, 2-优先级, 3-状态, 4-版本, 5-环境, 6-自定义
     */
    private Integer tagCategory;

    /**
     * 描述
     */
    private String description;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 1-启用, 2-禁用
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间(毫秒级时间戳)
     */
    private Long updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}