package com.grape.grape.service;

import com.grape.grape.model.vo.DeviceInfoVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.grape.grape.entity.DeviceInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 设备信息表 服务层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
public interface DeviceInfoService extends MyBaseService<DeviceInfo> {

    Page<DeviceInfoVo> pageInfo(Page<DeviceInfo> page);

    List<DeviceInfoVo> getList();

    boolean updateImage(MultipartFile multipartFile, int deviceId);

    boolean updateBrandImage(MultipartFile multipartFile, int deviceId);
}
