package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.Environments;
import com.grape.grape.mapper.EnvironmentsMapper;
import com.grape.grape.service.EnvironmentsService;
import org.springframework.stereotype.Service;

/**
 * 测试环境配置表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class EnvironmentsServiceImpl extends ServiceImpl<EnvironmentsMapper, Environments>  implements EnvironmentsService{

}
