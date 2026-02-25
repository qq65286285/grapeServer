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
import com.grape.grape.entity.Product;
import com.grape.grape.service.ProductService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 产品表 控制层。
 *
 * @author Administrator
 * @since 2025-02-08
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 添加产品表。
     *
     * @param product 产品表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Product product) {
        // 设置创建和更新时间
        long currentTime = System.currentTimeMillis();
        product.setCreatedAt(currentTime);
        product.setUpdatedAt(currentTime);
        
        // 设置创建人和更新人
        try {
            String userId = UserUtils.getCurrentUserId();
            if (userId != null) {
                Integer userIdInt = Integer.parseInt(userId);
                product.setCreatedBy(userIdInt);
                product.setUpdatedBy(userIdInt);
            }
        } catch (NumberFormatException e) {
            // 忽略类型转换异常
        }
        
        return productService.save(product);
    }

    /**
     * 根据主键删除产品表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return productService.removeById(id);
    }

    /**
     * 根据主键更新产品表。
     *
     * @param product 产品表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Product product) {
        // 设置更新时间
        product.setUpdatedAt(System.currentTimeMillis());
        
        // 设置更新人
        try {
            String userId = UserUtils.getCurrentUserId();
            if (userId != null) {
                product.setUpdatedBy(Integer.parseInt(userId));
            }
        } catch (NumberFormatException e) {
            // 忽略类型转换异常
        }
        
        return productService.updateById(product);
    }

    /**
     * 查询所有产品表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Product> list() {
        return productService.list();
    }

    /**
     * 根据产品表主键获取详细信息。
     *
     * @param id 产品表主键
     * @return 产品表详情
     */
    @GetMapping("getInfo/{id}")
    public Product getInfo(@PathVariable Integer id) {
        return productService.getById(id);
    }

    /**
     * 分页查询产品表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Product> page(Page<Product> page) {
        return productService.page(page);
    }

}
