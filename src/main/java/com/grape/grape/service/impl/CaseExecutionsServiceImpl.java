package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.CaseExecutions;
import com.grape.grape.mapper.CaseExecutionsMapper;
import com.grape.grape.service.CaseExecutionsService;
import org.springframework.stereotype.Service;

/**
 * 用例执行表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class CaseExecutionsServiceImpl extends ServiceImpl<CaseExecutionsMapper, CaseExecutions>  implements CaseExecutionsService{

    /**
     * 重写save方法，在保存之前设置默认值和操作人信息
     */
    @Override
    public boolean save(CaseExecutions caseExecutions) {
        // 设置默认值
        caseExecutions.setDefaultValues();
        
        // 这里可以添加设置创建人和执行人的逻辑
        // 例如：从上下文中获取当前用户ID
        // String currentUserId = UserUtils.getCurrentUserId();
        // if (currentUserId != null) {
        //     caseExecutions.setCreatedBy(currentUserId);
        //     caseExecutions.setExecutedBy(currentUserId);
        // }
        
        return super.save(caseExecutions);
    }

    /**
     * 重写updateById方法，在更新之前设置更新时间和操作人信息
     */
    @Override
    public boolean updateById(CaseExecutions caseExecutions) {
        // 设置更新时间
        caseExecutions.setUpdatedAt(System.currentTimeMillis());
        
        // 这里可以添加设置更新人的逻辑
        // 例如：从上下文中获取当前用户ID
        // String currentUserId = UserUtils.getCurrentUserId();
        // if (currentUserId != null) {
        //     caseExecutions.setUpdatedBy(currentUserId);
        // }
        
        return super.updateById(caseExecutions);
    }

}
