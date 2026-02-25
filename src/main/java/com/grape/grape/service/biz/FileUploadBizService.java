package com.grape.grape.service.biz;

import com.grape.grape.component.FileVo;
import com.grape.grape.model.Resp;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传业务服务接口
 * 用于处理文件上传相关的业务逻辑
 */
public interface FileUploadBizService {

    /**
     * 上传文件到 Minio
     * @param file 要上传的文件
     * @param bucketName 存储桶名称
     * @return 上传结果，包含文件信息
     */
    Resp uploadFile(MultipartFile file, String bucketName);

    /**
     * 验证存储桶名称是否符合 Amazon S3 标准
     * @param bucketName 存储桶名称
     * @return 是否有效
     */
    boolean isValidBucketName(String bucketName);

}
