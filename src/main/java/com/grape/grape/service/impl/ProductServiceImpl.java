package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.Product;
import com.grape.grape.mapper.ProductMapper;
import com.grape.grape.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * 产品表 服务层实现。
 *
 * @author Administrator
 * @since 2025-02-08
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>  implements ProductService{

}
