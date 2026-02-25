package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.Role;
import com.grape.grape.mapper.RoleMapper;
import com.grape.grape.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 系统角色定义表，RBAC模型核心组件 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>  implements RoleService{

}
