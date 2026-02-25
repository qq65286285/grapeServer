package com.grape.grape.service.impl;

import com.grape.grape.component.FileVo;
import com.grape.grape.model.vo.DeviceInfoVo;
import com.grape.grape.service.MinioService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.DeviceInfo;
import com.grape.grape.mapper.DeviceInfoMapper;
import com.grape.grape.service.DeviceInfoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * 设备信息表 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Slf4j
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo>  implements DeviceInfoService{

    @Resource
    MinioService minioService;

    @Override
    public Page<DeviceInfoVo> pageInfo(Page<DeviceInfo> page){
        log.info("开始执行分页查询，页码: {}，每页大小: {}", page.getPageNumber(), page.getPageSize());
        long startTime = System.currentTimeMillis();
        try {
            Page<DeviceInfo> daoPage = this.page(page);
            log.info("数据库分页查询完成，总记录数: {}，总页数: {}", daoPage.getTotalRow(), daoPage.getTotalPage());
            
            long toVoStartTime = System.currentTimeMillis();
            Page<DeviceInfoVo> voPage = new Page<>();
            voPage.setRecords(new DeviceInfoVo().toVoList(daoPage.getRecords(), minioService));
            voPage.setPageNumber(daoPage.getPageNumber());
            voPage.setPageSize(daoPage.getPageSize());
            voPage.setTotalPage(daoPage.getTotalPage());
            voPage.setTotalRow(daoPage.getTotalRow());
            voPage.setOptimizeCountQuery(daoPage.needOptimizeCountQuery());
            log.info("转换为VO对象完成，耗时: {}ms", System.currentTimeMillis() - toVoStartTime);
            
            long endTime = System.currentTimeMillis();
            log.info("分页查询执行完成，总耗时: {}ms", endTime - startTime);
            return voPage;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("分页查询执行失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<DeviceInfoVo> getList(){
        log.info("开始执行获取所有设备信息");
        long startTime = System.currentTimeMillis();
        try {
            List<DeviceInfo> deviceInfoList = this.list();
            log.info("数据库查询完成，设备数量: {}", deviceInfoList.size());
            
            long toVoStartTime = System.currentTimeMillis();
            List<DeviceInfoVo> result = new DeviceInfoVo().toVoList(deviceInfoList, minioService);
            log.info("转换为VO对象完成，耗时: {}ms", System.currentTimeMillis() - toVoStartTime);
            
            long endTime = System.currentTimeMillis();
            log.info("获取所有设备信息执行完成，总耗时: {}ms，结果数量: {}", endTime - startTime, result.size());
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("获取所有设备信息执行失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean updateImage(MultipartFile multipartFile, int deviceId){
        log.info("开始执行更新设备图片，设备ID: {}，文件名: {}，文件大小: {}", deviceId, multipartFile.getOriginalFilename(), multipartFile.getSize());
        long startTime = System.currentTimeMillis();
        try {
            long uploadStartTime = System.currentTimeMillis();
            FileVo fileVo = minioService.upload(multipartFile);
            log.info("文件上传完成，耗时: {}ms，新文件名: {}", System.currentTimeMillis() - uploadStartTime, fileVo != null ? fileVo.getNewFileName() : "上传失败");
            
            if(fileVo != null){
                DeviceInfo deviceInfo = getById(deviceId);
                if(null != deviceInfo){
                    deviceInfo.setDeviceIconId(fileVo.getNewFileName());
                    boolean updateResult = updateById(deviceInfo);
                    log.info("数据库更新完成，结果: {}", updateResult);
                    
                    long endTime = System.currentTimeMillis();
                    log.info("更新设备图片执行完成，总耗时: {}ms，结果: {}", endTime - startTime, updateResult);
                    return updateResult;
                } else {
                    log.warn("未找到设备，设备ID: {}", deviceId);
                    long endTime = System.currentTimeMillis();
                    log.info("更新设备图片执行完成，总耗时: {}ms，结果: false", endTime - startTime);
                    return false;
                }
            } else {
                log.warn("文件上传失败");
                long endTime = System.currentTimeMillis();
                log.info("更新设备图片执行完成，总耗时: {}ms，结果: false", endTime - startTime);
                return false;
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("更新设备图片执行失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean updateBrandImage(MultipartFile multipartFile, int deviceId){
        log.info("开始执行更新品牌图片，设备ID: {}，文件名: {}，文件大小: {}", deviceId, multipartFile.getOriginalFilename(), multipartFile.getSize());
        long startTime = System.currentTimeMillis();
        try {
            long uploadStartTime = System.currentTimeMillis();
            FileVo fileVo = minioService.upload(multipartFile);
            log.info("文件上传完成，耗时: {}ms，新文件名: {}", System.currentTimeMillis() - uploadStartTime, fileVo != null ? fileVo.getNewFileName() : "上传失败");
            
            if(fileVo != null){
                DeviceInfo deviceInfo = getById(deviceId);
                if(null != deviceInfo){
                    deviceInfo.setBrandIconId(fileVo.getNewFileName());
                    boolean updateResult = updateById(deviceInfo);
                    log.info("数据库更新完成，结果: {}", updateResult);
                    
                    long endTime = System.currentTimeMillis();
                    log.info("更新品牌图片执行完成，总耗时: {}ms，结果: {}", endTime - startTime, updateResult);
                    return updateResult;
                } else {
                    log.warn("未找到设备，设备ID: {}", deviceId);
                    long endTime = System.currentTimeMillis();
                    log.info("更新品牌图片执行完成，总耗时: {}ms，结果: false", endTime - startTime);
                    return false;
                }
            } else {
                log.warn("文件上传失败");
                long endTime = System.currentTimeMillis();
                log.info("更新品牌图片执行完成，总耗时: {}ms，结果: false", endTime - startTime);
                return false;
            }
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("更新品牌图片执行失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }
}
