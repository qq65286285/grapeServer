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
import com.grape.grape.entity.Role;
import com.grape.grape.service.RoleService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 系统角色定义表，RBAC模型核心组件 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 保存系统角色定义表，RBAC模型核心组件。
     *
     * @param role 系统角色定义表，RBAC模型核心组件
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Role role) {
        return roleService.save(role);
    }

    /**
     * 根据主键删除系统角色定义表，RBAC模型核心组件。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return roleService.removeById(id);
    }

    /**
     * 根据主键更新系统角色定义表，RBAC模型核心组件。
     *
     * @param role 系统角色定义表，RBAC模型核心组件
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Role role) {
        return roleService.updateById(role);
    }

    /**
     * 查询所有系统角色定义表，RBAC模型核心组件。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Role> list() {
        return roleService.list();
    }

    /**
     * 根据主键获取系统角色定义表，RBAC模型核心组件。
     *
     * @param id 系统角色定义表，RBAC模型核心组件主键
     * @return 系统角色定义表，RBAC模型核心组件详情
     */
    @GetMapping("getInfo/{id}")
    public Role getInfo(@PathVariable Integer id) {
        return roleService.getById(id);
    }

    /**
     * 分页查询系统角色定义表，RBAC模型核心组件。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Role> page(Page<Role> page) {
        return roleService.page(page);
    }

}
