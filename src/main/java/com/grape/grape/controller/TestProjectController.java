package com.grape.grape.controller;

import com.grape.grape.entity.TestProject;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestProjectService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testProject")
public class TestProjectController {

    @Autowired
    private TestProjectService testProjectService;

    /**
     * 新增项目
     *
     * @param testProject 项目
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestProject testProject) {
        boolean saved = testProjectService.save(testProject);
        if (saved) {
            return Resp.ok(testProject);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除项目（软删除）
     *
     * @param id 项目ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestProject project = testProjectService.getById(id);
        if (project != null) {
            project.setIsDeleted(1);
            boolean deleted = testProjectService.updateById(project);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询项目
     *
     * @param id 项目ID
     * @return 项目
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestProject testProject = testProjectService.getById(id);
        if (testProject != null && testProject.getIsDeleted() == 0) {
            return Resp.ok(testProject);
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新项目
     *
     * @param testProject 项目
     * @return 更新结果
     */
    @PutMapping
    public Resp update(@RequestBody TestProject testProject) {
        boolean updated = testProjectService.updateById(testProject);
        if (updated) {
            return Resp.ok(testProject);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询项目
     *
     * @param page 页码
     * @param size 每页大小
     * @param status 状态（可选）
     * @param ownerId 负责人ID（可选）
     * @param projectName 项目名称（可选，模糊查询）
     * @param projectCode 项目编码（可选，模糊查询）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Integer status, @RequestParam(required = false) Long ownerId,
                    @RequestParam(required = false) String projectName, @RequestParam(required = false) String projectCode) {
        Page<TestProject> result = testProjectService.page(Page.of(page, size), status, ownerId, projectName, projectCode);
        return Resp.ok(result);
    }

    /**
     * 根据状态查询项目列表
     *
     * @param status 状态
     * @return 项目列表
     */
    @GetMapping("/listByStatus/{status}")
    public Resp listByStatus(@PathVariable Integer status) {
        List<TestProject> list = testProjectService.listByStatus(status);
        return Resp.ok(list);
    }

    /**
     * 根据负责人ID查询项目列表
     *
     * @param ownerId 负责人ID
     * @return 项目列表
     */
    @GetMapping("/listByOwnerId/{ownerId}")
    public Resp listByOwnerId(@PathVariable Long ownerId) {
        List<TestProject> list = testProjectService.listByOwnerId(ownerId);
        return Resp.ok(list);
    }

    /**
     * 根据项目编码查询项目
     *
     * @param projectCode 项目编码
     * @return 项目
     */
    @GetMapping("/getByProjectCode/{projectCode}")
    public Resp getByProjectCode(@PathVariable String projectCode) {
        TestProject project = testProjectService.getByProjectCode(projectCode);
        if (project != null) {
            return Resp.ok(project);
        } else {
            return Resp.error();
        }
    }

    /**
     * 批量删除项目（软删除）
     *
     * @param ids 项目ID列表
     * @return 删除成功的数量
     */
    @DeleteMapping("/batchDelete")
    public Resp batchDelete(@RequestParam List<Long> ids) {
        int successCount = testProjectService.batchDelete(ids);
        return Resp.ok(successCount);
    }

    /**
     * 恢复已删除的项目
     *
     * @param id 项目ID
     * @return 是否恢复成功
     */
    @PutMapping("/restore/{id}")
    public Resp restore(@PathVariable Long id) {
        boolean success = testProjectService.restore(id);
        if (success) {
            return Resp.ok("恢复成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新项目状态
     *
     * @param id 项目ID
     * @param status 状态
     * @return 是否更新成功
     */
    @PutMapping("/updateStatus/{id}")
    public Resp updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = testProjectService.updateStatus(id, status);
        if (success) {
            return Resp.ok("更新状态成功");
        } else {
            return Resp.error();
        }
    }
}