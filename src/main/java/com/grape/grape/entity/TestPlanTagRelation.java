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
 * 测试计划标签关联表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_tag_relation")
public class TestPlanTagRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 关联类型: 1-计划, 2-用例快照, 3-执行记录, 4-报告
     */
    private Integer relationType;

    /**
     * 关联对象ID
     */
    private Long relationId;

    /**
     * 计划ID(冗余字段，便于查询)
     */
    private Long planId;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;
}