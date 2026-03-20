package com.grape.grape.service;

import com.grape.grape.entity.TestPlanTemplate;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划模板表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanTemplateService extends MyBaseService<TestPlanTemplate> {

    /**
     * 根据计划类型查询模板列表
     *
     * @param planType 计划类型
     * @return 模板列表
     */
    List<TestPlanTemplate> listByPlanType(Integer planType);

    /**
     * 根据创建人ID查询模板列表
     *
     * @param createdBy 创建人ID
     * @return 模板列表
     */
    List<TestPlanTemplate> listByCreatedBy(Long createdBy);

    /**
     * 分页查询模板
     *
     * @param page 分页参数
     * @param planType 计划类型（可选）
     * @param createdBy 创建人ID（可选）
     * @param templateName 模板名称（可选，模糊查询）
     * @return 分页结果
     */
    Page<TestPlanTemplate> page(Page<TestPlanTemplate> page, Integer planType, Long createdBy, String templateName);

    /**
     * 增加使用次数
     *
     * @param id 模板ID
     * @return 是否增加成功
     */
    boolean increaseUseCount(Long id);

    /**
     * 获取使用次数最多的模板列表
     *
     * @param limit 限制数量
     * @return 模板列表
     */
    List<TestPlanTemplate> listMostUsed(int limit);

    /**
     * 批量删除模板（软删除）
     *
     * @param ids 模板ID列表
     * @return 删除成功的数量
     */
    int batchDelete(List<Long> ids);

    /**
     * 恢复已删除的模板
     *
     * @param id 模板ID
     * @return 是否恢复成功
     */
    boolean restore(Long id);

    /**
     * 根据模板ID复制模板
     *
     * @param id 模板ID
     * @param newTemplateName 新模板名称
     * @return 新模板
     */
    TestPlanTemplate copyTemplate(Long id, String newTemplateName);
}