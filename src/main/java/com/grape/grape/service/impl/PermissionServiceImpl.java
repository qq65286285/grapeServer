package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.Permission;
import com.grape.grape.mapper.PermissionMapper;
import com.grape.grape.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * 操作权限清单，控制接口/功能访问权限 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>  implements PermissionService{

}
