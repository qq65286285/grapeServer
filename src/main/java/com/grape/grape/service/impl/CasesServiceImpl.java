package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.Cases;
import com.grape.grape.mapper.CasesMapper;
import com.grape.grape.service.CasesService;
import com.grape.grape.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 测试用例表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class CasesServiceImpl extends ServiceImpl<CasesMapper, Cases> implements CasesService{

    private static final Logger log = LoggerFactory.getLogger(CasesServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean save(Cases cases) {
        // 设置创建时间
        cases.setCreatedAt(System.currentTimeMillis());
        
        // 设置创建人ID和更新人ID
        String creatorId = UserUtils.getCurrentLoginUserId(userService);
        cases.setCreatedBy(creatorId);
        cases.setUpdatedBy(creatorId);
        log.info("设置测试用例创建人ID: {}", creatorId);
        
        return super.save(cases);
    }

    @Override
    public boolean updateById(Cases cases) {
        // 设置更新时间
        cases.setUpdatedAt(System.currentTimeMillis());
        
        // 设置更新人ID
        String updaterId = UserUtils.getCurrentLoginUserId(userService);
        // 如果无法获取用户ID，使用"system"作为默认值
        if (updaterId == null) {
            updaterId = "system";
            log.warn("无法获取当前用户ID，使用默认值 'system'");
        }
        cases.setUpdatedBy(updaterId);
        log.info("设置测试用例更新人ID: {}", updaterId);
        
        return super.updateById(cases);
    }

}
