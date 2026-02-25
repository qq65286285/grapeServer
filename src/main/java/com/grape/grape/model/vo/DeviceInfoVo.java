package com.grape.grape.model.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.grape.grape.entity.DeviceInfo;
import com.grape.grape.service.MinioService;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Data
public class DeviceInfoVo extends DeviceInfo{


    private String deviceIconUrl;

    private String BrandIconUrl;


    /**
     * 将任意 DAO/Entity 对象转换为 DeviceInfoVo，并自动填充图标URL
     *
     * @return 填充好 URL 的 VO
     */
    public DeviceInfoVo toVo(DeviceInfo entity, MinioService minioService) {
        DeviceInfoVo vo = new DeviceInfoVo();
        vo.setId(entity.getId());
        vo.setWifiAddress(entity.getWifiAddress());
        vo.setDevicesSerialNumber(entity.getDevicesSerialNumber());
        vo.setDevicesName(entity.getDevicesName());
        vo.setDevicesManufacturer(entity.getDevicesManufacturer());
        vo.setDevicesBrand(entity.getDevicesBrand());
        vo.setAndroidVersion(entity.getAndroidVersion());
        vo.setCpuInfo(entity.getCpuInfo());
        vo.setMemoryInfo(entity.getMemoryInfo());
        vo.setStorageAnalysis(entity.getStorageAnalysis());
        vo.setDeviceIconId(entity.getDeviceIconId());
        vo.setBrandIconId(entity.getBrandIconId());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreatedTime(entity.getCreatedTime());
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setUpdatedTime(entity.getUpdatedTime());
        vo.setIsOnline(entity.getIsOnline());
        vo.setResolution(entity.getResolution());
        try {
            vo.setDeviceIconUrl(minioService.getObjectUrl(entity.getDeviceIconId()));
        } catch (Exception e) {
            vo.setDeviceIconUrl("");
        }

        try {
            vo.setBrandIconUrl(minioService.getObjectUrl(entity.getBrandIconId()));
        } catch (Exception e) {
            vo.setBrandIconUrl("");
        }
        return vo;
    }

    public List<DeviceInfoVo> toVoList(List<DeviceInfo> entities, MinioService minioService){
        if(CollectionUtil.isEmpty( entities)){
            return Collections.emptyList();
        }
        return entities.stream().map(entity -> toVo(entity, minioService)).collect(Collectors.toList());

    }
}
