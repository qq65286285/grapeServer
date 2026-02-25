package com.grape.grape.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.Permission;
import com.grape.grape.service.PermissionService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 操作权限清单，控制接口/功能访问权限 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 保存操作权限清单，控制接口/功能访问权限。
     *
     * @param permission 操作权限清单，控制接口/功能访问权限
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Permission permission) {
        return permissionService.save(permission);
    }

    /**
     * 根据主键删除操作权限清单，控制接口/功能访问权限。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return permissionService.removeById(id);
    }

    /**
     * 根据主键更新操作权限清单，控制接口/功能访问权限。
     *
     * @param permission 操作权限清单，控制接口/功能访问权限
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Permission permission) {
        return permissionService.updateById(permission);
    }

    /**
     * 查询所有操作权限清单，控制接口/功能访问权限。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Permission> list() {
        return permissionService.list();
    }

    /**
     * 根据主键获取操作权限清单，控制接口/功能访问权限。
     *
     * @param id 操作权限清单，控制接口/功能访问权限主键
     * @return 操作权限清单，控制接口/功能访问权限详情
     */
    @GetMapping("getInfo/{id}")
    public Permission getInfo(@PathVariable Integer id) {
        return permissionService.getById(id);
    }

    /**
     * 分页查询操作权限清单，控制接口/功能访问权限。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Permission> page(Page<Permission> page) {
        return permissionService.page(page);
    }

}
