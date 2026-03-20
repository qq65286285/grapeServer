package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanComment;
import com.grape.grape.mapper.TestPlanCommentMapper;
import com.grape.grape.service.TestPlanCommentService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划评论表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanCommentServiceImpl extends ServiceImpl<TestPlanCommentMapper, TestPlanComment> implements TestPlanCommentService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanCommentServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanComment> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("is_pinned desc, created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanComment> listByTypeAndRelatedId(Integer commentType, Long relatedId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("comment_type = ? and related_id = ? and is_deleted = 0", commentType, relatedId)
                .orderBy("is_pinned desc, created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanComment> listByParentId(Long parentId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("parent_id = ? and is_deleted = 0", parentId)
                .orderBy("created_at asc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanComment> listByRootId(Long rootId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("root_id = ? and is_deleted = 0", rootId)
                .orderBy("parent_id asc, created_at asc");
        return list(queryWrapper);
    }

    @Override
    public void increaseReplyCount(Long commentId) {
        TestPlanComment comment = getById(commentId);
        if (comment != null) {
            comment.setReplyCount(comment.getReplyCount() + 1);
            comment.setUpdatedAt(System.currentTimeMillis());
            updateById(comment);
        }
    }

    @Override
    public void increaseLikeCount(Long commentId) {
        TestPlanComment comment = getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            comment.setUpdatedAt(System.currentTimeMillis());
            updateById(comment);
        }
    }

    @Override
    public void decreaseLikeCount(Long commentId) {
        TestPlanComment comment = getById(commentId);
        if (comment != null && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            comment.setUpdatedAt(System.currentTimeMillis());
            updateById(comment);
        }
    }

    @Override
    public boolean pinComment(Long commentId, Integer isPinned) {
        TestPlanComment comment = getById(commentId);
        if (comment != null) {
            comment.setIsPinned(isPinned);
            comment.setUpdatedAt(System.currentTimeMillis());
            return updateById(comment);
        }
        return false;
    }

    @Override
    public boolean save(TestPlanComment testPlanComment) {
        // 设置默认值
        if (testPlanComment.getParentId() == null) {
            testPlanComment.setParentId(0L);
        }
        if (testPlanComment.getRootId() == null) {
            testPlanComment.setRootId(testPlanComment.getParentId() == 0 ? testPlanComment.getId() : testPlanComment.getParentId());
        }
        if (testPlanComment.getAttachmentCount() == null) {
            testPlanComment.setAttachmentCount(0);
        }
        if (testPlanComment.getLikeCount() == null) {
            testPlanComment.setLikeCount(0);
        }
        if (testPlanComment.getReplyCount() == null) {
            testPlanComment.setReplyCount(0);
        }
        if (testPlanComment.getIsPinned() == null) {
            testPlanComment.setIsPinned(0); // 0-否
        }
        if (testPlanComment.getStatus() == null) {
            testPlanComment.setStatus(1); // 1-正常
        }
        if (testPlanComment.getIsDeleted() == null) {
            testPlanComment.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanComment.getCreatedAt() == null) {
            testPlanComment.setCreatedAt(now);
        }
        if (testPlanComment.getUpdatedAt() == null) {
            testPlanComment.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanComment.getCreatedBy() == null) {
                    testPlanComment.setCreatedBy(userId);
                }
                testPlanComment.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        // 保存评论
        boolean saved = super.save(testPlanComment);
        
        // 如果是回复评论，增加父评论的回复数
        if (saved && testPlanComment.getParentId() != 0) {
            increaseReplyCount(testPlanComment.getParentId());
        }

        return saved;
    }

    @Override
    public boolean updateById(TestPlanComment testPlanComment) {
        // 设置更新时间
        testPlanComment.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanComment.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanComment);
    }

    @Override
    public Page<TestPlanComment> page(Page<TestPlanComment> page, Long planId, Integer commentType, Long relatedId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (commentType != null) {
            queryWrapper.and("comment_type = ?", commentType);
        }

        if (relatedId != null) {
            queryWrapper.and("related_id = ?", relatedId);
        }

        queryWrapper.orderBy("is_pinned desc, created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }
}
