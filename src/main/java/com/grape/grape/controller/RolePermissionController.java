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
import com.grape.grape.entity.RolePermission;
import com.grape.grape.service.RolePermissionService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 角色权限映射表，配置角色可执行操作 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/rolePermission")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    /**
     * 保存角色权限映射表，配置角色可执行操作。
     *
     * @param rolePermission 角色权限映射表，配置角色可执行操作
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody RolePermission rolePermission) {
        return rolePermissionService.save(rolePermission);
    }

    /**
     * 根据主键删除角色权限映射表，配置角色可执行操作。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return rolePermissionService.removeById(id);
    }

    /**
     * 根据主键更新角色权限映射表，配置角色可执行操作。
     *
     * @param rolePermission 角色权限映射表，配置角色可执行操作
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody RolePermission rolePermission) {
        return rolePermissionService.updateById(rolePermission);
    }

    /**
     * 查询所有角色权限映射表，配置角色可执行操作。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<RolePermission> list() {
        return rolePermissionService.list();
    }

    /**
     * 根据主键获取角色权限映射表，配置角色可执行操作。
     *
     * @param id 角色权限映射表，配置角色可执行操作主键
     * @return 角色权限映射表，配置角色可执行操作详情
     */
    @GetMapping("getInfo/{id}")
    public RolePermission getInfo(@PathVariable Integer id) {
        return rolePermissionService.getById(id);
    }

    /**
     * 分页查询角色权限映射表，配置角色可执行操作。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<RolePermission> page(Page<RolePermission> page) {
        return rolePermissionService.page(page);
    }

}
