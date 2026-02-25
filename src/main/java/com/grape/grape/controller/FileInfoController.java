package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.FileInfo;
import com.grape.grape.service.FileInfoService;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.grape.grape.model.Resp;
import com.grape.grape.service.MinioService;
import org.springframework.web.bind.annotation.RequestParam;
import com.grape.grape.component.UserUtils;
import lombok.extern.slf4j.Slf4j;

/**
 *  控制层。
 *
 * @author Administrator
 * @since 2025-08-31
 */
@Slf4j
@RestController
@RequestMapping("/fileInfo")
public class FileInfoController {

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private MinioService minioService;

    /**
     * 添加。
     *
     * @param fileInfo 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody FileInfo fileInfo) {
        return fileInfoService.save(fileInfo);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable BigInteger id) {
        return fileInfoService.removeById(id);
    }

    /**
     * 删除文件（包括从MinIO删除文件和从数据库删除记录）。
     *
     * @param id 文件ID
     * @return 删除结果
     */
    @DeleteMapping("delete/{id}")
    public Resp delete(@PathVariable BigInteger id) {
        log.info("接收到删除文件请求，文件ID: {}", id);
        long startTime = System.currentTimeMillis();
        try {
            boolean result = fileInfoService.delete(id);
            long endTime = System.currentTimeMillis();
            log.info("删除文件请求处理完成，总耗时: {}ms，结果: {}", endTime - startTime, result ? "成功" : "失败");
            return Resp.ok(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("删除文件失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            return Resp.info(500, "删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据主键更新。
     *
     * @param fileInfo 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody FileInfo fileInfo) {
        return fileInfoService.updateById(fileInfo);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<FileInfo> list() {
        return fileInfoService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public FileInfo getInfo(@PathVariable BigInteger id) {
        return fileInfoService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public PageResp page(Page<FileInfo> page) {
        return new PageResp().pageInfoOk(fileInfoService.page(page));
    }

    /**
     * 上传文件。
     *
     * @param file 要上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Resp upload(@RequestParam("file") MultipartFile file) {
        log.info("开始上传文件，文件名: {}，文件大小: {}", file.getOriginalFilename(), file.getSize());
        long startTime = System.currentTimeMillis();
        try {
            boolean result = fileInfoService.upload(file);
            long endTime = System.currentTimeMillis();
            log.info("文件上传完成，总耗时: {}ms，结果: {}", endTime - startTime, result ? "成功" : "失败");
            return Resp.ok(result);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("文件上传失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            return Resp.info(500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件访问链接。
     *
     * @param objName 对象名称（存储路径）
     * @return 文件访问链接
     * @throws Exception 异常信息
     */
    @GetMapping("/getObjectUrl")
    public Resp getObjectUrl(@RequestParam("objName") String objName) throws Exception {
        log.info("接收到获取文件访问链接请求，对象名称: {}", objName);
        long startTime = System.currentTimeMillis();
        try {
            String url = minioService.getObjectUrl(objName);
            long endTime = System.currentTimeMillis();
            log.info("获取文件访问链接完成，总耗时: {}ms", endTime - startTime);
            return Resp.ok(url);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("获取文件访问链接失败，耗时: {}ms，错误信息: {}", endTime - startTime, e.getMessage(), e);
            throw e;
        }
    }

}
