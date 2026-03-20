package com.grape.grape.service;

import com.grape.grape.entity.TestPlanComment;
import com.mybatisflex.core.paginate.Page;

/**
 * 测试计划评论表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanCommentService extends MyBaseService<TestPlanComment> {

    /**
     * 根据计划ID查询评论列表
     *
     * @param planId 计划ID
     * @return 评论列表
     */
    java.util.List<TestPlanComment> listByPlanId(Long planId);

    /**
     * 根据评论类型和关联ID查询评论列表
     *
     * @param commentType 评论类型
     * @param relatedId   关联ID
     * @return 评论列表
     */
    java.util.List<TestPlanComment> listByTypeAndRelatedId(Integer commentType, Long relatedId);

    /**
     * 根据父评论ID查询回复列表
     *
     * @param parentId 父评论ID
     * @return 回复列表
     */
    java.util.List<TestPlanComment> listByParentId(Long parentId);

    /**
     * 根据根评论ID查询评论树
     *
     * @param rootId 根评论ID
     * @return 评论树列表
     */
    java.util.List<TestPlanComment> listByRootId(Long rootId);

    /**
     * 增加回复数
     *
     * @param commentId 评论ID
     */
    void increaseReplyCount(Long commentId);

    /**
     * 增加点赞数
     *
     * @param commentId 评论ID
     */
    void increaseLikeCount(Long commentId);

    /**
     * 减少点赞数
     *
     * @param commentId 评论ID
     */
    void decreaseLikeCount(Long commentId);

    /**
     * 置顶评论
     *
     * @param commentId 评论ID
     * @param isPinned  是否置顶
     * @return 是否操作成功
     */
    boolean pinComment(Long commentId, Integer isPinned);

    /**
     * 分页查询评论
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param commentType 评论类型（可选）
     * @param relatedId 关联ID（可选）
     * @return 分页结果
     */
    Page<TestPlanComment> page(Page<TestPlanComment> page, Long planId, Integer commentType, Long relatedId);
}
