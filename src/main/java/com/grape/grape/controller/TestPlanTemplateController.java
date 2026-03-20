package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanTemplate;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanTemplateService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划模板表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanTemplate")
public class TestPlanTemplateController {

    @Autowired
    private TestPlanTemplateService testPlanTemplateService;

    /**
     * 新增测试计划模板
     *
     * @param testPlanTemplate 测试计划模板
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanTemplate testPlanTemplate) {
        boolean saved = testPlanTemplateService.save(testPlanTemplate);
        if (saved) {
            return Resp.ok(testPlanTemplate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划模板（软删除）
     *
     * @param id 测试计划模板ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanTemplate template = testPlanTemplateService.getById(id);
        if (template != null) {
            template.setIsDeleted(1);
            boolean deleted = testPlanTemplateService.updateById(template);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划模板
     *
     * @param id 测试计划模板ID
     * @return 测试计划模板
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanTemplate testPlanTemplate = testPlanTemplateService.getById(id);
        if (testPlanTemplate != null && testPlanTemplate.getIsDeleted() == 0) {
            return Resp.ok(testPlanTemplate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 更新测试计划模板
     *
     * @param testPlanTemplate 测试计划模板
     * @return 更新结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanTemplate testPlanTemplate) {
        boolean updated = testPlanTemplateService.updateById(testPlanTemplate);
        if (updated) {
            return Resp.ok(testPlanTemplate);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划模板
     *
     * @param page 页码
     * @param size 每页大小
     * @param planType 计划类型（可选）
     * @param createdBy 创建人ID（可选）
     * @param templateName 模板名称（可选，模糊查询）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Integer planType, @RequestParam(required = false) Long createdBy,
                    @RequestParam(required = false) String templateName) {
        Page<TestPlanTemplate> result = testPlanTemplateService.page(Page.of(page, size), planType, createdBy, templateName);
        return Resp.ok(result);
    }

    /**
     * 根据计划类型查询模板列表
     *
     * @param planType 计划类型
     * @return 模板列表
     */
    @GetMapping("/listByPlanType/{planType}")
    public Resp listByPlanType(@PathVariable Integer planType) {
        List<TestPlanTemplate> list = testPlanTemplateService.listByPlanType(planType);
        return Resp.ok(list);
    }

    /**
     * 根据创建人ID查询模板列表
     *
     * @param createdBy 创建人ID
     * @return 模板列表
     */
    @GetMapping("/listByCreatedBy/{createdBy}")
    public Resp listByCreatedBy(@PathVariable Long createdBy) {
        List<TestPlanTemplate> list = testPlanTemplateService.listByCreatedBy(createdBy);
        return Resp.ok(list);
    }

    /**
     * 增加使用次数
     *
     * @param id 模板ID
     * @return 是否增加成功
     */
    @PutMapping("/increaseUseCount/{id}")
    public Resp increaseUseCount(@PathVariable Long id) {
        boolean success = testPlanTemplateService.increaseUseCount(id);
        if (success) {
            return Resp.ok("增加使用次数成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 获取使用次数最多的模板列表
     *
     * @param limit 限制数量
     * @return 模板列表
     */
    @GetMapping("/listMostUsed/{limit}")
    public Resp listMostUsed(@PathVariable int limit) {
        List<TestPlanTemplate> list = testPlanTemplateService.listMostUsed(limit);
        return Resp.ok(list);
    }

    /**
     * 批量删除模板（软删除）
     *
     * @param ids 模板ID列表
     * @return 删除成功的数量
     */
    @DeleteMapping("/batchDelete")
    public Resp batchDelete(@RequestParam List<Long> ids) {
        int successCount = testPlanTemplateService.batchDelete(ids);
        return Resp.ok(successCount);
    }

    /**
     * 恢复已删除的模板
     *
     * @param id 模板ID
     * @return 是否恢复成功
     */
    @PutMapping("/restore/{id}")
    public Resp restore(@PathVariable Long id) {
        boolean success = testPlanTemplateService.restore(id);
        if (success) {
            return Resp.ok("恢复成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据模板ID复制模板
     *
     * @param id 模板ID
     * @param newTemplateName 新模板名称
     * @return 新模板
     */
    @PostMapping("/copyTemplate/{id}")
    public Resp copyTemplate(@PathVariable Long id, @RequestParam String newTemplateName) {
        TestPlanTemplate newTemplate = testPlanTemplateService.copyTemplate(id, newTemplateName);
        if (newTemplate != null) {
            return Resp.ok(newTemplate);
        } else {
            return Resp.error();
        }
    }
}