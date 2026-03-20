package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanAttachment;
import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanAttachmentService;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 测试计划附件表 控制层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/testPlanAttachment")
public class TestPlanAttachmentController {

    private static final Logger log = LoggerFactory.getLogger(TestPlanAttachmentController.class);

    @Autowired
    private TestPlanAttachmentService testPlanAttachmentService;

    /**
     * 上传附件
     *
     * @param file          文件
     * @param planId        计划ID
     * @param attachmentType 附件类型
     * @param relatedId     关联对象ID
     * @return 上传结果
     */
    @PostMapping("upload")
    public Resp upload(@RequestParam("file") MultipartFile file,
                      @RequestParam("planId") Long planId,
                      @RequestParam("attachmentType") Integer attachmentType,
                      @RequestParam(value = "relatedId", required = false) Long relatedId) {
        log.info("接收附件上传请求，计划ID: {}，附件类型: {}", planId, attachmentType);
        try {
            boolean result = testPlanAttachmentService.upload(file, planId, attachmentType, relatedId);
            if (result) {
                return Resp.ok("附件上传成功");
            } else {
                return Resp.error();
            }
        } catch (Exception e) {
            log.error("附件上传失败，错误信息: {}", e.getMessage(), e);
            return Resp.error();
        }
    }

    /**
     * 根据主键删除附件
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Long id) {
        boolean result = testPlanAttachmentService.removeById(id);
        if (result) {
            return Resp.ok("删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键更新附件
     *
     * @param testPlanAttachment 附件信息
     * @return 更新结果
     */
    @PutMapping("update")
    public Resp update(@RequestBody TestPlanAttachment testPlanAttachment) {
        boolean result = testPlanAttachmentService.updateById(testPlanAttachment);
        if (result) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据主键查询附件
     *
     * @param id 主键
     * @return 附件信息
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Long id) {
        TestPlanAttachment attachment = testPlanAttachmentService.getById(id);
        if (attachment != null) {
            return Resp.ok(attachment);
        } else {
            return Resp.info(404, "附件不存在");
        }
    }

    /**
     * 查询所有附件
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        List<TestPlanAttachment> attachments = testPlanAttachmentService.list();
        return Resp.ok(attachments);
    }

    /**
     * 分页查询
     */
    @GetMapping("page")
    public Resp page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");
        com.mybatisflex.core.paginate.Page<TestPlanAttachment> page = testPlanAttachmentService.page(com.mybatisflex.core.paginate.Page.of(pageNum, pageSize), queryWrapper);
        return Resp.ok(page);
    }

    /**
     * 分页查询（带条件）
     */
    @PostMapping("page")
    public Resp page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");

        // 构建查询条件
        if (params != null) {
            if (params.containsKey("planId")) {
                queryWrapper.and("plan_id = ?", params.get("planId"));
            }
            if (params.containsKey("attachmentType")) {
                queryWrapper.and("attachment_type = ?", params.get("attachmentType"));
            }
            if (params.containsKey("relatedId")) {
                queryWrapper.and("related_id = ?", params.get("relatedId"));
            }
            if (params.containsKey("fileName")) {
                queryWrapper.and("file_name like ?", "%" + params.get("fileName") + "%");
            }
            if (params.containsKey("fileType")) {
                queryWrapper.and("file_type = ?", params.get("fileType"));
            }
            if (params.containsKey("status")) {
                queryWrapper.and("status = ?", params.get("status"));
            }
        }

        com.mybatisflex.core.paginate.Page<TestPlanAttachment> page = testPlanAttachmentService.page(com.mybatisflex.core.paginate.Page.of(pageNum, pageSize), queryWrapper);
        return Resp.ok(page);
    }

    /**
     * 批量删除附件
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Long> ids) {
        boolean result = testPlanAttachmentService.removeByIds(ids);
        if (result) {
            return Resp.ok("批量删除成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据条件查询附件
     */
    @PostMapping("listByCondition")
    public Resp listByCondition(@RequestBody(required = false) Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("is_deleted = 0");

        // 构建查询条件
        if (params != null) {
            if (params.containsKey("planId")) {
                queryWrapper.and("plan_id = ?", params.get("planId"));
            }
            if (params.containsKey("attachmentType")) {
                queryWrapper.and("attachment_type = ?", params.get("attachmentType"));
            }
            if (params.containsKey("relatedId")) {
                queryWrapper.and("related_id = ?", params.get("relatedId"));
            }
            if (params.containsKey("fileName")) {
                queryWrapper.and("file_name like ?", "%" + params.get("fileName") + "%");
            }
            if (params.containsKey("fileType")) {
                queryWrapper.and("file_type = ?", params.get("fileType"));
            }
            if (params.containsKey("status")) {
                queryWrapper.and("status = ?", params.get("status"));
            }
        }

        List<TestPlanAttachment> attachments = testPlanAttachmentService.list(queryWrapper);
        return Resp.ok(attachments);
    }

    /**
     * 根据计划ID查询附件
     */
    @GetMapping("listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.where("plan_id = ? and is_deleted = 0", planId);
        List<TestPlanAttachment> attachments = testPlanAttachmentService.list(queryWrapper);
        return Resp.ok(attachments);
    }

    /**
     * 下载附件（增加下载次数）
     */
    @GetMapping("download/{id}")
    public Resp download(@PathVariable Long id) {
        TestPlanAttachment attachment = testPlanAttachmentService.getAttachmentById(id);
        if (attachment != null) {
            // 增加下载次数
            testPlanAttachmentService.increaseDownloadCount(id);
            return Resp.ok(attachment);
        } else {
            return Resp.info(404, "附件不存在");
        }
    }
}
