package com.grape.grape.controller;

import com.grape.grape.component.UserUtils;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.Environments;
import com.grape.grape.service.EnvironmentsService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 测试环境配置表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/environments")
public class EnvironmentsController {

    @Autowired
    private EnvironmentsService environmentsService;

    /**
     * 添加测试环境配置表。
     *
     * @param environments 测试环境配置表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Environments environments) {
        // 设置创建和更新时间
        long currentTime = System.currentTimeMillis();
        environments.setCreatedAt(currentTime);
        environments.setUpdatedAt(currentTime);
        
        // 设置创建人和更新人
        try {
            String userId = UserUtils.getCurrentUserId();
            if (userId != null) {
                Integer userIdInt = Integer.parseInt(userId);
                environments.setCreatedBy(userIdInt);
                environments.setUpdatedBy(userIdInt);
            }
        } catch (NumberFormatException e) {
            // 忽略类型转换异常
        }
        
        return environmentsService.save(environments);
    }

    /**
     * 根据主键删除测试环境配置表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return environmentsService.removeById(id);
    }

    /**
     * 根据主键更新测试环境配置表。
     *
     * @param environments 测试环境配置表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Environments environments) {
        // 设置更新时间
        environments.setUpdatedAt(System.currentTimeMillis());
        
        // 设置更新人
        try {
            String userId = UserUtils.getCurrentUserId();
            if (userId != null) {
                environments.setUpdatedBy(Integer.parseInt(userId));
            }
        } catch (NumberFormatException e) {
            // 忽略类型转换异常
        }
        
        return environmentsService.updateById(environments);
    }

    /**
     * 查询所有测试环境配置表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Environments> list() {
        return environmentsService.list();
    }

    /**
     * 根据测试环境配置表主键获取详细信息。
     *
     * @param id 测试环境配置表主键
     * @return 测试环境配置表详情
     */
    @GetMapping("getInfo/{id}")
    public Environments getInfo(@PathVariable Integer id) {
        return environmentsService.getById(id);
    }

    /**
     * 分页查询测试环境配置表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Environments> page(Page<Environments> page) {
        return environmentsService.page(page);
    }

}
