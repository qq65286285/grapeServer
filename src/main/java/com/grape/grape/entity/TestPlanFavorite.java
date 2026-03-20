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
 * 测试计划收藏表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_favorite")
public class TestPlanFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 收藏类型: 1-个人收藏, 2-团队收藏
     */
    private Integer favoriteType;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;
}
