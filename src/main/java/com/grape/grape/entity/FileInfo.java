package com.grape.grape.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-08-31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("file_info")
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @Id(keyType = KeyType.Auto)
    private BigInteger id;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件扩展名（如.pdf）
     */
    private String fileExtension;

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 上传者ID
     */
    private String uploaderId;

    /**
     * 分享令牌
     */
    private String shareToken;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 下载次数
     */
    private Long downloadCount;

    /**
     * 上传时间
     */
    private Timestamp createdAt;

    /**
     * 删除标记(0正常,1已删除)
     */
    private Boolean isDeleted;

}
