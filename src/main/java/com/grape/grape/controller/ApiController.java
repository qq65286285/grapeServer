package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import com.grape.grape.service.biz.FileUploadBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对外 API 控制器
 * 用于提供外部系统调用的接口
 */
@RestController
@RequestMapping("/public")
public class ApiController {

    @Autowired
    private FileUploadBizService fileUploadBizService;

    /**
     * 上传文件到 Minio
     * @param file 要上传的文件
     * @param bucketName 存储桶名称
     * @return 上传结果，包含文件信息
     */
    @PostMapping("/upload")
    public Resp uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucketName") String bucketName) {
        return fileUploadBizService.uploadFile(file, bucketName);
    }
}
