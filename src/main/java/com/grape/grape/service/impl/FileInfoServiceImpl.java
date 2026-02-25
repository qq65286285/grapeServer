package com.grape.grape.service.impl;

import com.grape.grape.service.FileInfoService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.FileInfo;
import com.grape.grape.mapper.FileInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.grape.grape.service.MinioService;
import com.grape.grape.service.UserService;
import com.grape.grape.component.UserUtils;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-08-31
 */
@Slf4j
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>  implements FileInfoService {

    @Resource
    private MinioService minioService;

    @Resource
    private UserService userService;

    @Override
    public boolean upload(MultipartFile file) {
        log.info("开始上传文件，文件名: {}，文件大小: {}", file.getOriginalFilename(), file.getSize());
        long startTime = System.currentTimeMillis();
        try {
            // 上传文件到 MinIO
            com.grape.grape.component.FileVo fileVo = minioService.upload(file);
            log.info("文件上传到 MinIO 完成，新文件名: {}", fileVo != null ? fileVo.getNewFileName() : "上传失败");
            
            if (fileVo != null) {
                // 创建 FileInfo 对象
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(fileVo.getOldFileName());
                fileInfo.setFileSize(file.getSize());
                fileInfo.setStoragePath(fileVo.getNewFileName());
                
                // 设置文件扩展名
                String oldFileName = fileVo.getOldFileName();
                if (oldFileName != null && oldFileName.contains(".")) {
                    fileInfo.setFileExtension(oldFileName.substring(oldFileName.lastIndexOf(".")));
                }
                
                // 设置上传者ID
                String username = UserUtils.getCurrentUsername();
                if (username != null && !username.isEmpty()) {
                    try {
                        // 根据用户名查询用户信息
                        com.grape.grape.entity.User user = userService.findByUsername(username);
                        if (user != null && user.getId() != null) {
                            String uploaderId = user.getId();
                            fileInfo.setUploaderId(uploaderId);
                            log.info("设置上传者ID: {}，用户名: {}", uploaderId, username);
                        } else {
                            log.warn("根据用户名 {} 未找到用户信息，uploaderId未设置", username);
                        }
                    } catch (Exception e) {
                        log.error("查询用户信息失败，uploaderId未设置，错误信息: {}", e.getMessage(), e);
                    }
                } else {
                    log.warn("无法获取当前用户名，uploaderId未设置");
                }
                
                // 保存到数据库
                boolean saveResult = save(fileInfo);
                log.info("保存文件信息到数据库完成，结果: {}", saveResult);
                
                long endTime = System.currentTimeMillis();
                log.info("文件上传完成，总耗时: {}ms，结果: {}", endTime - startTime, saveResult ? "成功" : "失败");
                return saveResult;
            } else {
                log.warn("文件上传到 MinIO 失败");
                long endTime = System.currentTimeMillis();
                log.info("文件上传失败，总耗时: {}ms", endTime - startTime);
                return false;
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("文件上传失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean delete(java.math.BigInteger id) {
        log.info("开始删除文件，文件ID: {}", id);
        long startTime = System.currentTimeMillis();
        try {
            // 根据ID查询文件信息
            FileInfo fileInfo = getById(id);
            if (fileInfo == null) {
                log.warn("文件不存在，文件ID: {}", id);
                return false;
            }

            // 从MinIO删除文件
            String storagePath = fileInfo.getStoragePath();
            if (storagePath != null && !storagePath.isEmpty()) {
                try {
                    boolean minioDeleteResult = minioService.deleteFile(storagePath);
                    log.info("从MinIO删除文件完成，结果: {}，存储路径: {}", minioDeleteResult, storagePath);
                } catch (Exception e) {
                    log.error("从MinIO删除文件失败，错误信息: {}", e.getMessage(), e);
                    // 即使MinIO删除失败，也要继续删除数据库记录
                }
            } else {
                log.warn("文件存储路径为空，跳过MinIO删除操作");
            }

            // 从数据库删除记录
            boolean dbDeleteResult = removeById(id);
            log.info("从数据库删除文件记录完成，结果: {}", dbDeleteResult);

            long endTime = System.currentTimeMillis();
            log.info("文件删除完成，总耗时: {}ms，结果: {}", endTime - startTime, dbDeleteResult ? "成功" : "失败");
            return dbDeleteResult;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("文件删除失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            return false;
        }
    }
}
