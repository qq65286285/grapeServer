package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanComment;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanCommentService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划评论表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanComment")
public class TestPlanCommentController {

    @Autowired
    private TestPlanCommentService testPlanCommentService;

    /**
     * 新增测试计划评论
     *
     * @param testPlanComment 测试计划评论
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanComment testPlanComment) {
        boolean saved = testPlanCommentService.save(testPlanComment);
        if (saved) {
            return Resp.ok(testPlanComment);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划评论
     *
     * @param testPlanComment 测试计划评论
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanComment testPlanComment) {
        boolean updated = testPlanCommentService.updateById(testPlanComment);
        if (updated) {
            return Resp.ok(testPlanComment);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划评论
     *
     * @param id 测试计划评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanComment testPlanComment = testPlanCommentService.getById(id);
        if (testPlanComment != null) {
            testPlanComment.setIsDeleted(1);
            boolean deleted = testPlanCommentService.updateById(testPlanComment);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划评论
     *
     * @param id 测试计划评论ID
     * @return 测试计划评论
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanComment testPlanComment = testPlanCommentService.getById(id);
        if (testPlanComment != null && testPlanComment.getIsDeleted() == 0) {
            return Resp.ok(testPlanComment);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划评论
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param commentType 评论类型（可选）
     * @param relatedId 关联ID（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer commentType,
                    @RequestParam(required = false) Long relatedId) {
        Page<TestPlanComment> result = testPlanCommentService.page(Page.of(page, size), planId, commentType, relatedId);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询评论列表
     *
     * @param planId 计划ID
     * @return 评论列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanComment> list = testPlanCommentService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据评论类型和关联ID查询评论列表
     *
     * @param commentType 评论类型
     * @param relatedId 关联ID
     * @return 评论列表
     */
    @GetMapping("/listByTypeAndRelatedId")
    public Resp listByTypeAndRelatedId(@RequestParam Integer commentType, @RequestParam Long relatedId) {
        List<TestPlanComment> list = testPlanCommentService.listByTypeAndRelatedId(commentType, relatedId);
        return Resp.ok(list);
    }

    /**
     * 根据父评论ID查询回复列表
     *
     * @param parentId 父评论ID
     * @return 回复列表
     */
    @GetMapping("/listByParentId/{parentId}")
    public Resp listByParentId(@PathVariable Long parentId) {
        List<TestPlanComment> list = testPlanCommentService.listByParentId(parentId);
        return Resp.ok(list);
    }

    /**
     * 根据根评论ID查询评论树
     *
     * @param rootId 根评论ID
     * @return 评论树列表
     */
    @GetMapping("/listByRootId/{rootId}")
    public Resp listByRootId(@PathVariable Long rootId) {
        List<TestPlanComment> list = testPlanCommentService.listByRootId(rootId);
        return Resp.ok(list);
    }

    /**
     * 增加点赞数
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @PostMapping("/increaseLikeCount/{commentId}")
    public Resp increaseLikeCount(@PathVariable Long commentId) {
        testPlanCommentService.increaseLikeCount(commentId);
        return Resp.ok("操作成功");
    }

    /**
     * 减少点赞数
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @PostMapping("/decreaseLikeCount/{commentId}")
    public Resp decreaseLikeCount(@PathVariable Long commentId) {
        testPlanCommentService.decreaseLikeCount(commentId);
        return Resp.ok("操作成功");
    }

    /**
     * 置顶评论
     *
     * @param commentId 评论ID
     * @param isPinned 是否置顶
     * @return 操作结果
     */
    @PostMapping("/pinComment/{commentId}")
    public Resp pinComment(@PathVariable Long commentId, @RequestParam Integer isPinned) {
        boolean pinned = testPlanCommentService.pinComment(commentId, isPinned);
        if (pinned) {
            return Resp.ok("操作成功");
        } else {
            return Resp.error();
        }
    }
}
