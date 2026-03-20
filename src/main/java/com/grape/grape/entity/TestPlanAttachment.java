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
 * 测试计划附件表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_attachment")
public class TestPlanAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 附件类型: 1-计划附件, 2-用例附件, 3-执行附件, 4-报告附件, 5-评论附件
     */
    private Integer attachmentType;

    /**
     * 关联对象ID
     */
    private Long relatedId;

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
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 状态: 1-正常, 2-已删除
     */
    private Integer status;

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
