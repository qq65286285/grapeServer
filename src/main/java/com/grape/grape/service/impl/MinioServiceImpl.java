package com.grape.grape.service.impl;

import com.grape.grape.component.FileVo;
import com.grape.grape.component.MinioTemplate;
import com.grape.grape.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioServiceImpl implements MinioService {
    
    @Autowired
    MinioTemplate minioTemplate;
    @Value("${minio.bucketName:grape}")
    private String bucketName;
    
    @Override
    public FileVo upload(MultipartFile file) {
        return minioTemplate.upload(file, bucketName);
    }


    @Override
    public String getObjectUrl(String fileName) throws Exception {
        return minioTemplate.getObjUrl(bucketName, fileName);
    }

    @Override
    public boolean deleteFile(String fileName) throws Exception {
        minioTemplate.delete(bucketName, fileName);
        return true;
    }
}
