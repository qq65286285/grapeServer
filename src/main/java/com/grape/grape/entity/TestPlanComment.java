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
 * 测试计划评论表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_comment")
public class TestPlanComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 评论类型: 1-计划评论, 2-用例评论, 3-执行评论, 4-报告评论
     */
    private Integer commentType;

    /**
     * 关联对象ID(根据comment_type关联不同表)
     */
    private Long relatedId;

    /**
     * 父评论ID(0表示顶级评论)
     */
    private Long parentId;

    /**
     * 根评论ID(用于快速查询评论树)
     */
    private Long rootId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 提及的用户: [{"user_id": 102, "user_name": "张三"}]
     */
    private String mentionUsers;

    /**
     * 附件数量
     */
    private Integer attachmentCount;

    /**
     * 附件列表
     */
    private String attachments;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 是否置顶: 0-否, 1-是
     */
    private Integer isPinned;

    /**
     * 状态: 1-正常, 2-已删除, 3-已屏蔽
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
