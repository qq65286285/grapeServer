package com.grape.grape.component;


import cn.hutool.core.util.RandomUtil;
import io.minio.*;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioTemplate {
    @Autowired(required = false)
    private MinioClient client;


    /**
     * @Description： 上传文件
     * @Param： [file, bucketName]
     * @return：void
     **/
    public FileVo upload(MultipartFile file, String bucketName) {
        if (client == null) {
            log.warn("Minio client not initialized, cannot upload file");
            return null;
        }
        try {
            // 验证存储桶名称是否符合 Amazon S3 标准
            if (!isValidBucketName(bucketName)) {
                log.error("Invalid bucket name: {}. Bucket names must follow Amazon S3 standards.", bucketName);
                return null;
            }
            createBucket(bucketName);

            String oldName = file.getOriginalFilename();
            String fileName = oldName;

            // 使用 try-with-resources 确保输入流被正确关闭
            try (InputStream inputStream = file.getInputStream()) {
                client.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), 0)
                                .contentType(file.getContentType()).build()
                );
            }

            String url = this.getObjUrl(bucketName, fileName);
            return FileVo.builder()
                    .oldFileName(oldName)
                    .newFileName(fileName)
                    .fileUrl(url)
                    .build();
        } catch (Exception e) {
            // 检查是否是临时文件删除失败的错误
            if (e instanceof UncheckedIOException && e.getMessage().contains("Cannot delete")) {
                log.warn("临时文件删除失败，忽略此错误: {}", e.getMessage());
                // 尝试获取已上传的文件URL并返回
                try {
                    String oldName = file.getOriginalFilename();
                String fileName = oldName;
                    String url = this.getObjUrl(bucketName, fileName);
                    return FileVo.builder()
                            .oldFileName(oldName)
                            .newFileName(fileName)
                            .fileUrl(url)
                            .build();
                } catch (Exception ex) {
                    log.error("获取文件URL时出错: {}", ex);
                    return null;
                }
            } else {
                log.error("上传文件出错: {}", e);
                return null;
            }
        }
    }

    /**
     * @Description： 上传多个文件
     * @Param： [file, bucketName]
     * @return：void
     **/
    public List<FileVo> uploads(List<MultipartFile> files, String bucketName) {
        if (client == null) {
            log.warn("Minio client not initialized, cannot upload files");
            return null;
        }
        try {
            List<FileVo> list = new ArrayList<>();
            createBucket(bucketName);

            for (MultipartFile file : files) {
                try {
                    String oldName = file.getOriginalFilename();
                    String fileName = oldName;

                    // 使用 try-with-resources 确保输入流被正确关闭
                    try (InputStream inputStream = file.getInputStream()) {
                        client.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(fileName)
                                        .stream(inputStream, file.getSize(), 0)
                                        .contentType(file.getContentType()).build()
                        );
                    }

                    String url = this.getObjUrl(bucketName, fileName);
                    list.add(
                            FileVo.builder()
                                    .oldFileName(oldName)
                                    .newFileName(fileName)
                                    .fileUrl(url)
                                    .build()
                    );
                } catch (Exception ex) {
                    // 检查是否是临时文件删除失败的错误
                    if (ex instanceof UncheckedIOException && ex.getMessage().contains("Cannot delete")) {
                        log.warn("临时文件删除失败，忽略此错误: {}", ex.getMessage());
                        // 尝试获取已上传的文件URL并添加到列表
                        try {
                            String oldName = file.getOriginalFilename();
                            String fileName = oldName;
                            String url = this.getObjUrl(bucketName, fileName);
                            list.add(
                                    FileVo.builder()
                                            .oldFileName(oldName)
                                            .newFileName(fileName)
                                            .fileUrl(url)
                                            .build()
                            );
                        } catch (Exception e) {
                            log.error("获取文件URL时出错: {}", e);
                        }
                    } else {
                        log.error("上传文件出错: {}", ex);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            log.error("上传文件出错: {}", e);
            return null;
        }
    }

    /**
     * @Description： 下载文件
     * @Param： [bucketName, fileName]
     * @return：void
     **/
    public void download(String bucketName, String fileName) throws Exception {
        if (client == null) {
            log.warn("Minio client not initialized, cannot download file");
            throw new Exception("Minio client not initialized");
        }
        client.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).filename(fileName).build());
    }

    /**
     * @Description： 获取文件链接
     * @Param： [bucketName, fileName]
     * @return：java.lang.String
     **/
    public String getObjUrl(String bucketName, String fileName) throws Exception {
        if (client == null) {
            log.warn("Minio client not initialized, cannot get object URL");
            throw new Exception("Minio client not initialized");
        }
        return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(30, TimeUnit.HOURS)
                        .build()
        );
    }

    /**
     * @Description： 删除文件
     * @Param： [bucketName, fileName]
     * @return：void
     **/
    public void delete(String bucketName, String fileName) throws Exception {
        if (client == null) {
            log.warn("Minio client not initialized, cannot delete file");
            throw new Exception("Minio client not initialized");
        }
        client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

    /**
     * 验证存储桶名称是否符合 Amazon S3 标准
     * @param bucketName 存储桶名称
     * @return 是否有效
     */
    private boolean isValidBucketName(String bucketName) {
        if (bucketName == null || bucketName.isEmpty()) {
            return false;
        }
        // 存储桶名称长度必须在 3-63 个字符之间
        if (bucketName.length() < 3 || bucketName.length() > 63) {
            return false;
        }
        // 存储桶名称只能包含小写字母、数字和连字符
        if (!bucketName.matches("^[a-z0-9][a-z0-9.-]*[a-z0-9]$") || bucketName.contains("..")) {
            return false;
        }
        // 存储桶名称不能以连字符开头或结尾
        if (bucketName.startsWith("-") || bucketName.endsWith("-")) {
            return false;
        }
        // 存储桶名称不能包含连续的连字符
        if (bucketName.contains("--")) {
            return false;
        }
        // 存储桶名称不能是 IP 地址格式
        if (bucketName.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$")) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    public void createBucket(String bucketName) {
        if (client == null) {
            log.warn("Minio client not initialized, cannot create bucket");
            return;
        }
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//            String sb = "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:GetBucketLocation\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "]}},{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:ListBucket\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "]},\"Condition\": {\"StringEquals\": {\"s3:prefix\": [\"*"]}}},{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:GetObject\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "/**"]}}]}";
//            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(sb).build());
        }
    }

}

