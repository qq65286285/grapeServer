package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.RolePermission;
import com.grape.grape.mapper.RolePermissionMapper;
import com.grape.grape.service.RolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色权限映射表，配置角色可执行操作 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission>  implements RolePermissionService{

}
