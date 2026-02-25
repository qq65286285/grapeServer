package com.grape.grape.service;

import com.grape.grape.component.FileVo;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    FileVo upload(MultipartFile file);

    String getObjectUrl(String fileName) throws Exception;
    
    boolean deleteFile(String fileName) throws Exception;
}
