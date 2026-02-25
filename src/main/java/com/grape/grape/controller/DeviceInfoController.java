package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.utils.Adb_wifi_Connect;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.DeviceInfo;
import com.grape.grape.service.DeviceInfoService;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * 设备信息表 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Slf4j
@RestController
@RequestMapping("/deviceInfo")
public class DeviceInfoController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 保存设备信息表。
     *
     * @param deviceInfo 设备信息表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
//    @PostMapping("save")
//    public boolean save(@RequestBody DeviceInfo deviceInfo) {
//        return deviceInfoService.save(deviceInfo);
//    }

    /**
     * 根据主键删除设备信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
//    @DeleteMapping("remove/{id}")
//    public boolean remove(@PathVariable Integer id) {
//        return deviceInfoService.removeById(id);
//    }

    /**
     * 根据主键更新设备信息表。
     *
     * @param deviceInfo 设备信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
//    @PutMapping("update")
//    public boolean update(@RequestBody DeviceInfo deviceInfo) {
//        return deviceInfoService.updateById(deviceInfo);
//    }

    /**
     * 查询所有设备信息表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        log.info("开始查询所有设备信息");
        long startTime = System.currentTimeMillis();
        try {
            List<com.grape.grape.model.vo.DeviceInfoVo> result = deviceInfoService.getList();
            long endTime = System.currentTimeMillis();
            log.info("查询所有设备信息完成，耗时: {}ms，结果数量: {}", endTime - startTime, result.size());
            return Resp.ok(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("查询所有设备信息失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据主键获取设备信息表。
     *
     * @param id 设备信息表主键
     * @return 设备信息表详情
     */
    @GetMapping("getInfo/{id}")
    public DeviceInfo getInfo(@PathVariable Integer id) {
        log.info("开始根据ID查询设备信息，设备ID: {}", id);
        long startTime = System.currentTimeMillis();
        try {
            DeviceInfo result = deviceInfoService.getById(id);
            long endTime = System.currentTimeMillis();
            log.info("根据ID查询设备信息完成，设备ID: {}，耗时: {}ms，结果: {}", id, endTime - startTime, result != null ? "成功" : "未找到设备");
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("根据ID查询设备信息失败，设备ID: {}，耗时: {}ms，错误信息: {}", id, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询设备信息表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public PageResp page(@RequestBody  Page<DeviceInfo> page) {
        log.info("开始分页查询设备信息，页码: {}，每页大小: {}", page.getPageNumber(), page.getPageSize());
        long startTime = System.currentTimeMillis();
        try {
            com.mybatisflex.core.paginate.Page<com.grape.grape.model.vo.DeviceInfoVo> result = deviceInfoService.pageInfo(page);
            long endTime = System.currentTimeMillis();
            log.info("分页查询设备信息完成，页码: {}，每页大小: {}，耗时: {}ms，总记录数: {}，总页数: {}", 
                    page.getPageNumber(), page.getPageSize(), endTime - startTime, result.getTotalRow(), result.getTotalPage());
            return new PageResp().pageInfoOk(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("分页查询设备信息失败，页码: {}，每页大小: {}，耗时: {}ms，错误信息: {}", 
                    page.getPageNumber(), page.getPageSize(), endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/updateImg")
    public Resp upload(@RequestParam("file") MultipartFile multipartFile,@RequestParam("deviceId") int deviceId) {
        log.info("开始更新设备图片，设备ID: {}，文件名: {}，文件大小: {}", deviceId, multipartFile.getOriginalFilename(), multipartFile.getSize());
        long startTime = System.currentTimeMillis();
        try {
            boolean result = deviceInfoService.updateImage(multipartFile, deviceId);
            long endTime = System.currentTimeMillis();
            log.info("更新设备图片完成，设备ID: {}，耗时: {}ms，结果: {}", deviceId, endTime - startTime, result ? "成功" : "失败");
            return Resp.ok(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("更新设备图片失败，设备ID: {}，耗时: {}ms，错误信息: {}", deviceId, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/updateBrandImg")
    public Resp updateBrandImg(@RequestParam("file") MultipartFile multipartFile,@RequestParam("deviceId") int deviceId) {
        log.info("开始更新品牌图片，设备ID: {}，文件名: {}，文件大小: {}", deviceId, multipartFile.getOriginalFilename(), multipartFile.getSize());
        long startTime = System.currentTimeMillis();
        try {
            boolean result = deviceInfoService.updateBrandImage(multipartFile, deviceId);
            long endTime = System.currentTimeMillis();
            log.info("更新品牌图片完成，设备ID: {}，耗时: {}ms，结果: {}", deviceId, endTime - startTime, result ? "成功" : "失败");
            return Resp.ok(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("更新品牌图片失败，设备ID: {}，耗时: {}ms，错误信息: {}", deviceId, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }


    /* ----------------------------------------------------------------设备状态，执行API------------------------------------------- */
    @PostMapping("/syncDeviceStatus")
    public Resp syncDeviceStatus(){
        log.info("开始同步设备状态");
        long startTime = System.currentTimeMillis();
        try {
            Adb_wifi_Connect.adb_connect();
            log.info("执行ADB连接完成");
            Adb_wifi_Connect.set_online_devices();
            log.info("设置在线设备完成");
            long endTime = System.currentTimeMillis();
            log.info("同步设备状态完成，耗时: {}ms", endTime - startTime);
            return Resp.ok(null);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("同步设备状态失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/syncDeviceStatus")
    public Resp syncDeviceStatusGet(){
        return syncDeviceStatus();
    }
}
