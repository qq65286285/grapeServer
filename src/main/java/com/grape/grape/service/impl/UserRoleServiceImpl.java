package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.UserRole;
import com.grape.grape.mapper.UserRoleMapper;
import com.grape.grape.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色映射表，实现RBAC多角色分配 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>  implements UserRoleService{

}
