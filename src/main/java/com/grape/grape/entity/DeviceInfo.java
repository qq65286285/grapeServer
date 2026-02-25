package com.grape.grape.entity;

import com.grape.grape.service.MinioService;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息表 实体类。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("device_info")
public class DeviceInfo implements Serializable {


    /**
     * 设备ID，自增主键
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * Wi-Fi MAC地址
     */
    private String wifiAddress;

    /**
     * 设备序列号
     */
    private String devicesSerialNumber;

    /**
     * 设备名称
     */
    private String devicesName;

    /**
     * 设备制造商
     */
    private String devicesManufacturer;

    /**
     * 设备品牌
     */
    private String devicesBrand;

    /**
     * Android系统版本
     */
    private String androidVersion;

    /**
     * CPU详细信息
     */
    private String cpuInfo;

    /**
     * 内存信息
     */
    private String memoryInfo;

    /**
     * 存储分析详情
     */
    private String storageAnalysis;

    /**
     * 设备图标ID，前端据此生成URL
     */
    private String deviceIconId;

    /**
     * 品牌图标ID，用于资源服务映射为URL
     */
    private String brandIconId;

    /**
     * 记录创建者
     */
    private String createdBy;

    /**
     * 创建时间，毫秒级时间戳
     */
    private Long createdTime;

    /**
     * 最后更新者
     */
    private String updatedBy;

    /**
     * 最后更新时间，毫秒级时间戳
     */
    private Long updatedTime;

    /**
     *   is_online 是否在线:0-不在线，1-在线
     */
    private Integer isOnline;

    /**
     * resolution 分辨率
     */
    private String resolution;

}
