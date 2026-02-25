package com.grape.grape.controller;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestCaseFolder;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestCaseFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试用例文件夹控制器
 */
@RestController
@RequestMapping("/testCaseFolders")
public class TestCaseFolderController {

    @Autowired
    private TestCaseFolderService testCaseFolderService;

    /**
     * 添加文件夹
     */
    @PostMapping("save")
    public Resp save(@RequestBody TestCaseFolder folder) {
        try {
            // 计算文件夹路径
            String path = testCaseFolderService.calculatePath(folder.getParentId());
            folder.setPath(path);
            
            // 设置创建和更新时间
            long currentTime = System.currentTimeMillis();
            folder.setCreatedAt(currentTime);
            folder.setUpdatedAt(currentTime);
            
            // 设置创建人和更新人
            String currentUser = UserUtils.getCurrentUsername();
            folder.setCreatedBy(currentUser);
            folder.setUpdatedBy(currentUser);
            
            boolean result = testCaseFolderService.save(folder);
            return Resp.ok(result);
        } catch (Exception e) {
            return Resp.info(500, "添加文件夹失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件夹
     */
    @PutMapping("update")
    public Resp update(@RequestBody TestCaseFolder folder) {
        try {
            // 计算新的文件夹路径
            String path = testCaseFolderService.calculatePath(folder.getParentId());
            folder.setPath(path);
            
            // 设置更新时间和更新人
            folder.setUpdatedAt(System.currentTimeMillis());
            folder.setUpdatedBy(UserUtils.getCurrentUsername());
            
            boolean result = testCaseFolderService.updateById(folder);
            return Resp.ok(result);
        } catch (Exception e) {
            return Resp.info(500, "更新文件夹失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件夹
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Integer id) {
        try {
            boolean result = testCaseFolderService.removeById(id);
            return Resp.ok(result);
        } catch (Exception e) {
            return Resp.info(500, "删除文件夹失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件夹列表
     */
    @GetMapping("list")
    public Resp list() {
        try {
            List<TestCaseFolder> folders = testCaseFolderService.list();
            return Resp.ok(folders);
        } catch (Exception e) {
            return Resp.info(500, "获取文件夹列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据父文件夹ID获取子文件夹
     */
    @GetMapping("listByParentId/{parentId}")
    public Resp listByParentId(@PathVariable Integer parentId) {
        try {
            List<TestCaseFolder> folders = testCaseFolderService.getByParentId(parentId);
            return Resp.ok(folders);
        } catch (Exception e) {
            return Resp.info(500, "获取子文件夹失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件夹树状结构
     */
    @GetMapping("tree")
    public Resp tree() {
        try {
            List<TestCaseFolder> folderTree = testCaseFolderService.buildFolderTree();
            return Resp.ok(folderTree);
        } catch (Exception e) {
            return Resp.info(500, "获取文件夹树状结构失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件夹详情
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Integer id) {
        try {
            TestCaseFolder folder = testCaseFolderService.getById(id);
            return Resp.ok(folder);
        } catch (Exception e) {
            return Resp.info(500, "获取文件夹详情失败: " + e.getMessage());
        }
    }
}
