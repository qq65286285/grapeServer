package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanTemplate;
import com.grape.grape.mapper.TestPlanTemplateMapper;
import com.grape.grape.service.TestPlanTemplateService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 测试计划模板表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanTemplateServiceImpl extends ServiceImpl<TestPlanTemplateMapper, TestPlanTemplate> implements TestPlanTemplateService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanTemplateServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanTemplate> listByPlanType(Integer planType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_type = ? and is_deleted = 0", planType)
                .orderBy("use_count desc, created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTemplate> listByCreatedBy(Long createdBy) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("created_by = ? and is_deleted = 0", createdBy)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public Page<TestPlanTemplate> page(Page<TestPlanTemplate> page, Integer planType, Long createdBy, String templateName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planType != null) {
            queryWrapper.and("plan_type = ?", planType);
        }

        if (createdBy != null) {
            queryWrapper.and("created_by = ?", createdBy);
        }

        if (templateName != null && !templateName.isEmpty()) {
            queryWrapper.and("template_name like ?", "%" + templateName + "%");
        }

        queryWrapper.orderBy("use_count desc, created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean increaseUseCount(Long id) {
        TestPlanTemplate template = getById(id);
        if (template != null && template.getIsDeleted() == 0) {
            template.setUseCount(template.getUseCount() + 1);
            template.setLastUsedAt(new Date());
            return updateById(template);
        }
        return false;
    }

    @Override
    public List<TestPlanTemplate> listMostUsed(int limit) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0")
                .orderBy("use_count desc, created_at desc")
                .limit(limit);
        return list(queryWrapper);
    }

    @Override
    public int batchDelete(List<Long> ids) {
        int successCount = 0;
        for (Long id : ids) {
            TestPlanTemplate template = getById(id);
            if (template != null) {
                template.setIsDeleted(1);
                template.setUpdatedBy(getCurrentUserId());
                template.setUpdatedAt(new Date());
                if (updateById(template)) {
                    successCount++;
                }
            }
        }
        return successCount;
    }

    @Override
    public boolean restore(Long id) {
        TestPlanTemplate template = getById(id);
        if (template != null && template.getIsDeleted() == 1) {
            template.setIsDeleted(0);
            template.setUpdatedBy(getCurrentUserId());
            template.setUpdatedAt(new Date());
            return updateById(template);
        }
        return false;
    }

    @Override
    public TestPlanTemplate copyTemplate(Long id, String newTemplateName) {
        TestPlanTemplate originalTemplate = getById(id);
        if (originalTemplate != null && originalTemplate.getIsDeleted() == 0) {
            TestPlanTemplate newTemplate = new TestPlanTemplate();
            newTemplate.setTemplateName(newTemplateName);
            newTemplate.setPlanType(originalTemplate.getPlanType());
            newTemplate.setDescription(originalTemplate.getDescription());
            newTemplate.setTestScope(originalTemplate.getTestScope());
            newTemplate.setTestTarget(originalTemplate.getTestTarget());
            newTemplate.setTestStrategy(originalTemplate.getTestStrategy());
            newTemplate.setAcceptanceCriteria(originalTemplate.getAcceptanceCriteria());
            newTemplate.setDefaultConfig(originalTemplate.getDefaultConfig());
            newTemplate.setCaseFilter(originalTemplate.getCaseFilter());
            newTemplate.setUseCount(0);
            newTemplate.setCreatedBy(getCurrentUserId());
            newTemplate.setCreatedAt(new Date());
            newTemplate.setUpdatedBy(getCurrentUserId());
            newTemplate.setUpdatedAt(new Date());
            newTemplate.setIsDeleted(0);

            if (save(newTemplate)) {
                return newTemplate;
            }
        }
        return null;
    }

    @Override
    public boolean save(TestPlanTemplate template) {
        // 设置创建人和创建时间
        if (template.getCreatedBy() == null) {
            template.setCreatedBy(getCurrentUserId());
        }
        if (template.getCreatedAt() == null) {
            template.setCreatedAt(new Date());
        }

        // 设置更新人和更新时间
        template.setUpdatedBy(getCurrentUserId());
        template.setUpdatedAt(new Date());

        // 设置默认值
        if (template.getUseCount() == null) {
            template.setUseCount(0);
        }
        if (template.getIsDeleted() == null) {
            template.setIsDeleted(0);
        }

        return super.save(template);
    }

    @Override
    public boolean updateById(TestPlanTemplate template) {
        // 设置更新人和更新时间
        template.setUpdatedBy(getCurrentUserId());
        template.setUpdatedAt(new Date());

        return super.updateById(template);
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     */
    private Long getCurrentUserId() {
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }
        return null;
    }
}