package com.grape.grape.component;

import com.grape.grape.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageUtil {
    @Autowired
    MinioService minioService;


    /**
     * 根据图片 ID 生成可访问的 URL
     * @param imageId MinIO 中的图片对象 ID
     * @return 可访问的图片 URL（若 imageId 为空则返回 null）
     */
    public String generateImageUrl(String imageId) throws Exception {
        if (imageId == null || imageId.isEmpty())  return null;
        return minioService.getObjectUrl(imageId);  // 调用 MinioService 转换
    }
}
