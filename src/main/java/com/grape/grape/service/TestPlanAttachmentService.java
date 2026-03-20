package com.grape.grape.service;

import com.grape.grape.entity.TestPlanAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 测试计划附件表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanAttachmentService extends MyBaseService<TestPlanAttachment> {

    /**
     * 上传附件
     *
     * @param file          文件
     * @param planId        计划ID
     * @param attachmentType 附件类型
     * @param relatedId     关联对象ID
     * @return 是否上传成功
     */
    boolean upload(MultipartFile file, Long planId, Integer attachmentType, Long relatedId) throws IOException;

    /**
     * 下载附件
     *
     * @param id 附件ID
     * @return 附件信息
     */
    TestPlanAttachment getAttachmentById(Long id);

    /**
     * 增加下载次数
     *
     * @param id 附件ID
     */
    void increaseDownloadCount(Long id);
}
