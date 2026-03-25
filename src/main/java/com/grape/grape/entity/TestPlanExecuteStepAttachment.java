package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试计划执行步骤附件表 实体类。
 * 用于存储测试计划执行步骤的附件信息
 *
 * @author Administrator
 * @since 2026-03-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_execute_step_attachment")
public class TestPlanExecuteStepAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 执行步骤ID
     */
    private Long executeStepId;

    /**
     * 执行记录ID
     */
    private Long executeId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型(扩展名)
     */
    private String fileType;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 存储类型: 1-本地, 2-OSS, 3-MinIO, 4-其他
     */
    private Integer storageType;

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 访问URL
     */
    private String fileUrl;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 预览URL
     */
    private String previewUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 上传人ID
     */
    private Long createdBy;

    /**
     * 上传时间(毫秒级时间戳)
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