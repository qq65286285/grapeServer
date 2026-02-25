package com.grape.grape.service;

import com.mybatisflex.core.service.IService;
import com.grape.grape.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-08-31
 */
public interface FileInfoService extends IService<FileInfo> {

    boolean upload(MultipartFile file);
    
    boolean delete(BigInteger id);

}
