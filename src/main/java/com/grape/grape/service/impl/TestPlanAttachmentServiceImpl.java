package com.grape.grape.service.impl;

import com.grape.grape.component.FileVo;
import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanAttachment;
import com.grape.grape.mapper.TestPlanAttachmentMapper;
import com.grape.grape.service.MinioService;
import com.grape.grape.service.TestPlanAttachmentService;
import com.grape.grape.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 测试计划附件表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanAttachmentServiceImpl extends ServiceImpl<TestPlanAttachmentMapper, TestPlanAttachment> implements TestPlanAttachmentService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanAttachmentServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MinioService minioService;

    // 附件存储路径
    private static final String ATTACHMENT_DIR = "attachments/test-plan";

    @Override
    public boolean upload(MultipartFile file, Long planId, Integer attachmentType, Long relatedId) throws IOException {
        return uploadAndReturnId(file, planId, attachmentType, relatedId) != null;
    }

    @Override
    public Long uploadAndReturnId(MultipartFile file, Long planId, Integer attachmentType, Long relatedId) throws IOException {
        // 上传文件到 Minio
        FileVo fileVo = minioService.upload(file);
        if (fileVo == null) {
            log.error("文件上传到 Minio 失败");
            return null;
        }

        // 生成文件类型
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "";

        // 构建附件信息
        TestPlanAttachment attachment = TestPlanAttachment.builder()
                .planId(planId)
                .attachmentType(attachmentType)
                .relatedId(relatedId)
                .fileName(fileVo.getOldFileName())
                .fileSize(file.getSize())
                .fileType(fileType)
                .mimeType(file.getContentType())
                .storageType(3) // 3-MinIO 存储
                .storagePath(fileVo.getNewFileName())
                .fileUrl(fileVo.getFileUrl())
                .downloadCount(0)
                .status(1) // 1-正常
                .isDeleted(0) // 0-否
                .build();

        // 设置上传人ID和时间
        String uploaderIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (uploaderIdStr != null) {
            try {
                Long uploaderId = Long.parseLong(uploaderIdStr);
                attachment.setCreatedBy(uploaderId);
                attachment.setUpdatedBy(uploaderId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", uploaderIdStr);
            }
        }
        long now = System.currentTimeMillis();
        attachment.setCreatedAt(now);
        attachment.setUpdatedAt(now);

        // 保存到数据库
        if (save(attachment)) {
            return attachment.getId();
        } else {
            return null;
        }
    }

    @Override
    public String uploadAndReturnFileId(MultipartFile file, Long planId, Integer attachmentType, Long relatedId) throws IOException {
        // 上传文件到 Minio
        FileVo fileVo = minioService.upload(file);
        if (fileVo == null) {
            log.error("文件上传到 Minio 失败");
            return null;
        }

        // 生成文件类型
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "";

        // 构建附件信息
        TestPlanAttachment attachment = TestPlanAttachment.builder()
                .planId(planId)
                .attachmentType(attachmentType)
                .relatedId(relatedId)
                .fileName(fileVo.getOldFileName())
                .fileSize(file.getSize())
                .fileType(fileType)
                .mimeType(file.getContentType())
                .storageType(3) // 3-MinIO 存储
                .storagePath(fileVo.getNewFileName())
                .fileUrl(fileVo.getFileUrl())
                .downloadCount(0)
                .status(1) // 1-正常
                .isDeleted(0) // 0-否
                .build();

        // 设置上传人ID和时间
        String uploaderIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (uploaderIdStr != null) {
            try {
                Long uploaderId = Long.parseLong(uploaderIdStr);
                attachment.setCreatedBy(uploaderId);
                attachment.setUpdatedBy(uploaderId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", uploaderIdStr);
            }
        }
        long now = System.currentTimeMillis();
        attachment.setCreatedAt(now);
        attachment.setUpdatedAt(now);

        // 保存到数据库
        if (save(attachment)) {
            return fileVo.getNewFileName(); // 返回 Minio 的文件ID
        } else {
            return null;
        }
    }

    @Override
    public TestPlanAttachment getAttachmentById(Long id) {
        return getById(id);
    }

    @Override
    public void increaseDownloadCount(Long id) {
        TestPlanAttachment attachment = getById(id);
        if (attachment != null) {
            attachment.setDownloadCount(attachment.getDownloadCount() + 1);
            attachment.setUpdatedAt(System.currentTimeMillis());
            updateById(attachment);
        }
    }

    @Override
    public boolean save(TestPlanAttachment testPlanAttachment) {
        // 设置默认值
        if (testPlanAttachment.getStorageType() == null) {
            testPlanAttachment.setStorageType(1); // 1-本地存储
        }
        if (testPlanAttachment.getDownloadCount() == null) {
            testPlanAttachment.setDownloadCount(0);
        }
        if (testPlanAttachment.getStatus() == null) {
            testPlanAttachment.setStatus(1); // 1-正常
        }
        if (testPlanAttachment.getIsDeleted() == null) {
            testPlanAttachment.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanAttachment.getCreatedAt() == null) {
            testPlanAttachment.setCreatedAt(now);
        }
        if (testPlanAttachment.getUpdatedAt() == null) {
            testPlanAttachment.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanAttachment.getCreatedBy() == null) {
                    testPlanAttachment.setCreatedBy(userId);
                }
                testPlanAttachment.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanAttachment);
    }

    @Override
    public boolean updateById(TestPlanAttachment testPlanAttachment) {
        // 设置更新时间
        testPlanAttachment.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanAttachment.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanAttachment);
    }
}
