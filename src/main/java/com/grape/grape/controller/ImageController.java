package com.grape.grape.controller;

import com.grape.grape.component.FileVo;
import com.grape.grape.component.MinioTemplate;
import com.grape.grape.entity.CaseVersions;
import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.grape.grape.service.MinioService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用例版本备份表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    MinioService minioService;

    // 上传
    @PostMapping("/upload")
    public Resp upload(@RequestParam("file") MultipartFile multipartFile) {
        FileVo fileVo = minioService.upload(multipartFile);
        if(null == fileVo){
            return Resp.info(ResultEnumI18n.BUSINESS_ERROR.getCode(),"上传失败");
        }
        return Resp.ok(fileVo);
    }

    // 下载文件
//    @GetMapping("/download")
//    public void download(@RequestParam("fileName")String fileName, HttpServletResponse response) {
//        this.minioConfig.download(fileName,response);
//    }

    // 列出所有存储桶名称
//    @PostMapping("/list")
//    public List<String> list() throws Exception {
//        return this.minioConfig.listBucketNames();
//    }

    // 创建存储桶
//    @PostMapping("/createBucket")
//    public boolean createBucket(String bucketName) throws Exception {
//        return this.minioConfig.makeBucket(bucketName);
//    }

    // 删除存储桶
//    @PostMapping("/deleteBucket")
//    public boolean deleteBucket(String bucketName) throws Exception {
//        return this.minioConfig.removeBucket(bucketName);
//    }

    // 列出存储桶中的所有对象名称
//    @PostMapping("/listObjectNames")
//    public List<String> listObjectNames(String bucketName) throws Exception {
//        return this.minioConfig.listObjectNames(bucketName);
//    }

    // 删除一个对象
//    @PostMapping("/removeObject")
//    public boolean removeObject(String bucketName, String objectName) throws Exception {
//        return this.minioConfig.removeObject(bucketName, objectName);
//    }

    // 文件访问路径
    @GetMapping("/getObjectUrl")
    public String getObjectUrl(@RequestParam("objName") String objectName) throws Exception {
        return minioService.getObjectUrl( objectName);
    }



}
