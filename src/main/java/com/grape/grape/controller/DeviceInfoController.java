package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.utils.AdbWifiConnect;
import com.grape.grape.model.vo.DeviceInfoVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.DeviceInfo;
import com.grape.grape.service.DeviceInfoService;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 设备信息控制器
 * 提供设备信息管理的API接口，包括设备CRUD操作、状态同步等功能
 * 
 * 主要功能：
 * 1. 设备信息的增删改查
 * 2. 设备列表分页查询
 * 3. 设备图片上传和更新
 * 4. 设备状态同步（通过ADB连接）
 * 
 * @author grape-team
 * @since 2025-10-13
 */
@Slf4j
@RestController
@RequestMapping("/deviceInfo")
public class DeviceInfoController {

    /**
     * 设备信息服务
     * 负责设备信息的数据访问和业务逻辑处理
     */
    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 查询所有设备信息
     * 
     * 功能说明：
     * 1. 从数据库查询所有设备信息
     * 2. 将设备实体转换为视图对象（VO）
     * 3. 返回设备列表响应
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，准备查询所有设备
     * - 数据查询：调用服务层获取设备列表数据
     * - 数据转换：将DeviceInfo实体转换为DeviceInfoVo视图对象
     * - 响应构建：组装返回给客户端的响应数据
     * - 结束：返回包含设备列表的成功响应
     * 
     * 调用服务：
     * - 调用 deviceInfoService.getList() 获取设备列表
     *   该方法内部会执行以下操作：
     *   1. 构建查询条件（无过滤条件）
     *   2. 执行数据库查询：SELECT * FROM device_info
     *   3. 将查询结果转换为VO对象列表
     *   4. 返回转换后的设备列表
     * 
     * 数据库调用：
     * - 执行SQL查询：SELECT * FROM device_info
     * - 查询结果包含所有在线和离线的设备信息
     * - 包括设备序列号、IP地址、品牌、型号等详细信息
     * 
     * @return Resp 响应对象，包含设备信息列表
     *         - 成功时：code=0, data=List<DeviceInfoVo>
     *         - 失败时：code=非0, message=错误描述
     */
    @GetMapping("list")
    public Resp list() {
        // 方法开始：记录开始时间，准备查询所有设备信息
        log.info("开始查询所有设备信息");
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用服务层获取设备列表
            // 该调用会查询数据库获取所有设备信息
            List<DeviceInfoVo> result = deviceInfoService.getList();
            
            // 方法中间：记录查询完成时间，计算耗时
            long endTime = System.currentTimeMillis();
            log.info("查询所有设备信息完成，耗时: {}ms，结果数量: {}", endTime - startTime, result.size());
            
            // 方法结束：返回查询结果
            return Resp.ok(result);
        } catch (Exception e) {
            // 异常处理：记录错误信息
            long endTime = System.currentTimeMillis();
            log.error("查询所有设备信息失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据主键查询设备信息
     * 
     * 功能说明：
     * 1. 接收设备ID作为路径参数
     * 2. 从数据库查询指定设备的详细信息
     * 3. 返回设备实体对象
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，路径中包含设备ID
     * - 参数提取：从路径变量中提取设备ID
     * - 数据查询：调用服务层根据ID查询设备信息
     * - 响应构建：将查询结果返回给客户端
     * - 结束：返回设备详细信息
     * 
     * 调用服务：
     * - 调用 deviceInfoService.getById(id) 根据ID查询设备
     *   该方法内部会执行以下操作：
     *   1. 构建查询条件：WHERE id = ?
     *   2. 执行数据库查询：SELECT * FROM device_info WHERE id = ?
     *   3. 返回查询结果或null（如果设备不存在）
     * 
     * 数据库调用：
     * - 执行SQL查询：SELECT * FROM device_info WHERE id = ?
     * - 参数：id（设备主键）
     * - 返回结果：单个DeviceInfo实体或null
     * 
     * @param id 设备主键ID，从URL路径中获取
     * @return DeviceInfo 设备实体对象，包含设备的完整信息
     *         - 如果设备存在：返回设备详细信息
     *         - 如果设备不存在：返回null
     */
    @GetMapping("getInfo/{id}")
    public DeviceInfo getInfo(@PathVariable Integer id) {
        // 方法开始：记录查询设备信息的开始时间
        log.info("开始根据ID查询设备信息，设备ID: {}", id);
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用服务层根据ID查询设备信息
            // 该调用会执行数据库查询获取指定ID的设备
            DeviceInfo result = deviceInfoService.getById(id);
            
            // 方法中间：记录查询完成时间，计算耗时
            long endTime = System.currentTimeMillis();
            log.info("根据ID查询设备信息完成，设备ID: {}，耗时: {}ms，结果: {}", 
                    id, endTime - startTime, result != null ? "成功" : "未找到设备");
            
            // 方法结束：返回查询结果
            return result;
        } catch (Exception e) {
            // 异常处理：记录错误信息
            long endTime = System.currentTimeMillis();
            log.error("根据ID查询设备信息失败，设备ID: {}，耗时: {}ms，错误信息: {}", 
                    id, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询设备信息
     * 
     * 功能说明：
     * 1. 接收分页参数（页码、每页大小）
     * 2. 从数据库查询分页数据
     * 3. 返回分页结果（包含总记录数、总页数等）
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析分页参数
     * - 参数校验：验证页码和每页大小的有效性
     * - 分页查询：调用服务层执行分页查询
     * - 响应构建：组装分页结果数据
     * - 结束：返回分页响应
     * 
     * 调用服务：
     * - 调用 deviceInfoService.pageInfo(page) 执行分页查询
     *   该方法内部会执行以下操作：
     *   1. 构建分页查询SQL（使用LIMIT和OFFSET）
     *   2. 执行数据库查询：SELECT * FROM device_info LIMIT ? OFFSET ?
     *   3. 执行总数查询：SELECT COUNT(*) FROM device_info
     *   4. 返回分页结果对象
     * 
     * 数据库调用：
     * - 执行分页查询：SELECT * FROM device_info LIMIT pageSize OFFSET (pageNum-1)*pageSize
     * - 执行总数查询：SELECT COUNT(*) FROM device_info
     * - 返回结果：包含数据列表和分页信息的Page对象
     * 
     * @param page 分页对象，包含以下字段：
     *             - pageNumber: 当前页码（从1开始）
     *             - pageSize: 每页记录数
     * @return PageResp 分页响应对象，包含以下信息：
     *         - totalRow: 总记录数
     *         - totalPage: 总页数
     *         - pageNumber: 当前页码
     *         - pageSize: 每页大小
     *         - records: 当前页数据列表
     */
    @PostMapping("page")
    public PageResp page(@RequestBody Page<DeviceInfo> page) {
        // 方法开始：记录开始时间，解析分页参数
        log.info("开始分页查询设备信息，页码: {}，每页大小: {}", page.getPageNumber(), page.getPageSize());
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用服务层执行分页查询
            // 该调用会执行数据库分页查询
            com.mybatisflex.core.paginate.Page<DeviceInfoVo> result = deviceInfoService.pageInfo(page);
            
            // 方法中间：记录查询完成时间，计算耗时
            long endTime = System.currentTimeMillis();
            log.info("分页查询设备信息完成，页码: {}，每页大小: {}，耗时: {}ms，总记录数: {}，总页数: {}", 
                    page.getPageNumber(), page.getPageSize(), endTime - startTime, 
                    result.getTotalRow(), result.getTotalPage());
            
            // 方法结束：返回分页结果
            return new PageResp().pageInfoOk(result);
        } catch (Exception e) {
            // 异常处理：记录错误信息
            long endTime = System.currentTimeMillis();
            log.error("分页查询设备信息失败，页码: {}，每页大小: {}，耗时: {}ms，错误信息: {}", 
                    page.getPageNumber(), page.getPageSize(), endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新设备图片
     * 
     * 功能说明：
     * 1. 接收上传的设备图片文件
     * 2. 将图片上传到MinIO对象存储
     * 3. 更新设备表中的图片URL字段
     * 4. 返回更新结果
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，包含图片文件和设备ID
     * - 参数校验：验证文件和设备ID的有效性
     * - 文件上传：将图片上传到MinIO存储服务
     * - 数据更新：更新数据库中设备的图片URL
     * - 响应构建：返回操作结果
     * - 结束：完成图片更新流程
     * 
     * 调用服务：
     * - 调用 deviceInfoService.updateImage(multipartFile, deviceId) 更新设备图片
     *   该方法内部会执行以下操作：
     *   1. 验证文件格式和大小
     *   2. 调用MinIOTemplate上传文件到对象存储
     *   3. 获取文件的访问URL
     *   4. 更新数据库：UPDATE device_info SET image_url = ? WHERE id = ?
     *   5. 返回更新结果
     * 
     * 调用外部服务：
     * - 调用 MinIOTemplate.uploadFile() 上传文件到MinIO
     *   MinIO是一个开源的对象存储服务，用于存储图片等文件
     *   上传成功后返回文件的访问URL
     * 
     * 数据库调用：
     * - 执行更新SQL：UPDATE device_info SET image_url = ?, updated_time = ? WHERE id = ?
     * - 参数：
     *   - image_url: MinIO中的图片访问URL
     *   - id: 设备主键ID
     * - 返回结果：受影响的行数（1表示成功，0表示失败）
     * 
     * @param multipartFile 上传的图片文件，支持常见图片格式（jpg、png等）
     * @param deviceId 设备ID，指定要更新图片的设备
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=非0, message=错误描述
     */
    @PostMapping("/updateImg")
    public Resp upload(@RequestParam("file") MultipartFile multipartFile, @RequestParam("deviceId") int deviceId) {
        // 方法开始：记录开始时间，准备更新设备图片
        log.info("开始更新设备图片，设备ID: {}，文件名: {}，文件大小: {}", 
                deviceId, multipartFile.getOriginalFilename(), multipartFile.getSize());
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用服务层更新设备图片
            // 该调用会：
            // 1. 上传图片到MinIO对象存储
            // 2. 更新数据库中的图片URL
            boolean result = deviceInfoService.updateImage(multipartFile, deviceId);
            
            // 方法中间：记录完成时间，计算耗时
            long endTime = System.currentTimeMillis();
            log.info("更新设备图片完成，设备ID: {}，耗时: {}ms，结果: {}", 
                    deviceId, endTime - startTime, result ? "成功" : "失败");
            
            // 方法结束：返回更新结果
            return Resp.ok(result);
        } catch (Exception e) {
            // 异常处理：记录错误信息
            long endTime = System.currentTimeMillis();
            log.error("更新设备图片失败，设备ID: {}，耗时: {}ms，错误信息: {}", 
                    deviceId, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新设备品牌图片
     * 
     * 功能说明：
     * 1. 接收上传的品牌图片文件
     * 2. 将图片上传到MinIO对象存储
     * 3. 更新设备表中的品牌图片URL字段
     * 4. 返回更新结果
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，包含品牌图片文件和设备ID
     * - 参数校验：验证文件和设备ID的有效性
     * - 文件上传：将图片上传到MinIO存储服务
     * - 数据更新：更新数据库中设备的品牌图片URL
     * - 响应构建：返回操作结果
     * - 结束：完成品牌图片更新流程
     * 
     * 调用服务：
     * - 调用 deviceInfoService.updateBrandImage(multipartFile, deviceId) 更新品牌图片
     *   该方法内部会执行以下操作：
     *   1. 验证文件格式和大小
     *   2. 调用MinIOTemplate上传文件到对象存储
     *   3. 获取文件的访问URL
     *   4. 更新数据库：UPDATE device_info SET brand_image_url = ?, updated_time = ? WHERE id = ?
     *   5. 返回更新结果
     * 
     * 调用外部服务：
     * - 调用 MinIOTemplate.uploadFile() 上传文件到MinIO
     *   MinIO是一个开源的对象存储服务，用于存储品牌图片等文件
     *   上传成功后返回文件的访问URL
     * 
     * 数据库调用：
     * - 执行更新SQL：UPDATE device_info SET brand_image_url = ?, updated_time = ? WHERE id = ?
     * - 参数：
     *   - brand_image_url: MinIO中的品牌图片访问URL
     *   - id: 设备主键ID
     * - 返回结果：受影响的行数（1表示成功，0表示失败）
     * 
     * @param multipartFile 上传的品牌图片文件，支持常见图片格式
     * @param deviceId 设备ID，指定要更新品牌图片的设备
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=非0, message=错误描述
     */
    @PostMapping("/updateBrandImg")
    public Resp updateBrandImg(@RequestParam("file") MultipartFile multipartFile, @RequestParam("deviceId") int deviceId) {
        // 方法开始：记录开始时间，准备更新品牌图片
        log.info("开始更新品牌图片，设备ID: {}，文件名: {}，文件大小: {}", 
                deviceId, multipartFile.getOriginalFilename(), multipartFile.getSize());
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用服务层更新品牌图片
            // 该调用会：
            // 1. 上传图片到MinIO对象存储
            // 2. 更新数据库中的品牌图片URL
            boolean result = deviceInfoService.updateBrandImage(multipartFile, deviceId);
            
            // 方法中间：记录完成时间，计算耗时
            long endTime = System.currentTimeMillis();
            log.info("更新品牌图片完成，设备ID: {}，耗时: {}ms，结果: {}", 
                    deviceId, endTime - startTime, result ? "成功" : "失败");
            
            // 方法结束：返回更新结果
            return Resp.ok(result);
        } catch (Exception e) {
            // 异常处理：记录错误信息
            long endTime = System.currentTimeMillis();
            log.error("更新品牌图片失败，设备ID: {}，耗时: {}ms，错误信息: {}", 
                    deviceId, endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 同步设备状态（POST方式）
     * 
     * 功能说明：
     * 1. 执行ADB连接命令，连接所有已连接的Android设备
     * 2. 获取设备的最新信息（序列号、IP地址、设备型号等）
     * 3. 将设备信息同步到数据库
     * 4. 更新设备的在线状态
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，准备同步设备状态
     * - ADB连接：执行ADB命令连接设备
     * - 信息收集：获取设备的详细信息（分辨率、品牌、型号等）
     * - 数据同步：将设备信息写入或更新到数据库
     * - 状态更新：标记设备为在线状态
     * - 响应构建：返回同步结果
     * - 结束：完成设备状态同步流程
     * 
     * 调用外部服务：
     * - 调用 AdbWifiConnect.adbConnect() 执行ADB连接和设备信息收集
     *   该方法内部会执行以下操作：
     *   1. 执行ADB命令：adb devices 获取已连接设备列表
     *   2. 对每个USB设备执行WiFi连接设置
     *   3. 获取设备信息：adb shell getprop ro.serialno 等命令
     *   4. 将设备信息写入数据库
     * - 调用 AdbWifiConnect.setOnlineDevices() 更新设备在线状态
     *   该方法内部会执行以下操作：
     *   1. 先将所有设备标记为离线
     *   2. 将当前在线的设备标记为在线
     *   3. 更新数据库中的is_online字段
     * 
     * 调用外部系统：
     * - 调用 ADB（Android Debug Bridge）命令行工具
     *   ADB是Android SDK提供的调试工具，用于与Android设备通信
     *   通过ADB可以执行shell命令、安装应用、传输文件等操作
     * - 常用ADB命令：
     *   - adb devices: 列出已连接的设备
     *   - adb -s <device> shell <command>: 执行shell命令
     *   - adb connect <ip>:<port>: 通过WiFi连接设备
     * 
     * 数据库调用：
     * - 插入新设备：INSERT INTO device_info (...) VALUES (...)
     * - 更新现有设备：UPDATE device_info SET ... WHERE devices_serial_number = ?
     * - 批量更新状态：UPDATE device_info SET is_online = 0 (所有设备)
     *   UPDATE device_info SET is_online = 1 WHERE wifi_address = ? OR devices_serial_number = ? (在线设备)
     * 
     * @return Resp 响应对象，包含同步结果
     *         - 成功时：code=0, data=null
     *         - 失败时：code=非0, message=错误描述
     */
    @PostMapping("/syncDeviceStatus")
    public Resp syncDeviceStatus() {
        // 方法开始：记录开始时间，准备同步设备状态
        log.info("开始同步设备状态");
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用ADB工具执行设备连接
            // 该调用会执行ADB命令连接设备并获取设备信息
            AdbWifiConnect.adbConnect();
            log.info("执行ADB连接完成");
            
            // 调用ADB工具设置在线设备
            // 该调用会更新数据库中设备的在线状态
            AdbWifiConnect.setOnlineDevices();
            log.info("设置在线设备完成");
            
            // 方法中间：记录完成时间，计算耗时
            long endTime = System.currentTimeMillis();
            log.info("同步设备状态完成，耗时: {}ms", endTime - startTime);
            
            // 方法结束：返回同步结果
            return Resp.ok(null);
        } catch (Exception e) {
            // 异常处理：记录错误信息
            long endTime = System.currentTimeMillis();
            log.error("同步设备状态失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 同步设备状态（GET方式）
     * 
     * 功能说明：
     * 提供GET方式的设备状态同步接口，方便某些场景下的调用
     * 内部实现与POST方式完全相同
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求
     * - 委托处理：调用syncDeviceStatus()方法执行同步逻辑
     * - 结束：返回同步结果
     * 
     * 调用服务：
     * - 调用 syncDeviceStatus() 方法执行实际的同步逻辑
     *   该方法会处理所有ADB连接和数据库更新操作
     * 
     * 数据库调用：
     * - 同POST方式的syncDeviceStatus()方法
     * 
     * @return Resp 响应对象，包含同步结果
     *         - 成功时：code=0, data=null
     *         - 失败时：code=非0, message=错误描述
     */
    @GetMapping("/syncDeviceStatus")
    public Resp syncDeviceStatusGet() {
        // 方法开始：接收GET请求，准备委托给POST方法处理
        return syncDeviceStatus();
    }
}
