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
import com.grape.grape.entity.UserRole;
import com.grape.grape.service.UserRoleService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 用户角色映射表，实现RBAC多角色分配 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 保存用户角色映射表，实现RBAC多角色分配。
     *
     * @param userRole 用户角色映射表，实现RBAC多角色分配
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody UserRole userRole) {
        return userRoleService.save(userRole);
    }

    /**
     * 根据主键删除用户角色映射表，实现RBAC多角色分配。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable String id) {
        return userRoleService.removeById(id);
    }

    /**
     * 根据主键更新用户角色映射表，实现RBAC多角色分配。
     *
     * @param userRole 用户角色映射表，实现RBAC多角色分配
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody UserRole userRole) {
        return userRoleService.updateById(userRole);
    }

    /**
     * 查询所有用户角色映射表，实现RBAC多角色分配。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<UserRole> list() {
        return userRoleService.list();
    }

    /**
     * 根据主键获取用户角色映射表，实现RBAC多角色分配。
     *
     * @param id 用户角色映射表，实现RBAC多角色分配主键
     * @return 用户角色映射表，实现RBAC多角色分配详情
     */
    @GetMapping("getInfo/{id}")
    public UserRole getInfo(@PathVariable String id) {
        return userRoleService.getById(id);
    }

    /**
     * 分页查询用户角色映射表，实现RBAC多角色分配。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<UserRole> page(Page<UserRole> page) {
        return userRoleService.page(page);
    }

}
