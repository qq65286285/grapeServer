package com.grape.grape.controller;

import com.grape.grape.component.UserUtils;
import com.grape.grape.model.Resp;
import com.grape.grape.service.biz.CaseVersionBizService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.CaseVersions;
import com.grape.grape.service.CaseVersionsService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试用例版本备份表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/caseVersions")
public class CaseVersionsController {

    @Autowired
    private CaseVersionsService caseVersionsService;
    @Resource
    private CaseVersionBizService caseVersionBizService;
    /**
     * 添加测试用例版本备份表。
     *
     * @param caseVersions 测试用例版本备份表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseVersions caseVersions) {
        // 设置创建和更新时间
        long currentTime = System.currentTimeMillis();
        caseVersions.setCreatedAt(currentTime);
        caseVersions.setUpdatedAt(currentTime);
        
        // 设置创建人和更新人
        String currentUserId = UserUtils.getCurrentUserId();
        if (currentUserId != null) {
            caseVersions.setCreatedBy(currentUserId);
            caseVersions.setUpdatedBy(currentUserId);
        } else {
            caseVersions.setCreatedBy("system");
            caseVersions.setUpdatedBy("system");
        }
        
        return caseVersionsService.save(caseVersions);
    }

    /**
     * 根据主键删除测试用例版本备份表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return caseVersionsService.removeById(id);
    }

    /**
     * 根据主键更新测试用例版本备份表。
     *
     * @param caseVersions 测试用例版本备份表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseVersions caseVersions) {
        // 设置更新时间
        caseVersions.setUpdatedAt(System.currentTimeMillis());
        
        // 设置更新人
        String currentUserId = UserUtils.getCurrentUserId();
        if (currentUserId != null) {
            caseVersions.setUpdatedBy(currentUserId);
        } else {
            caseVersions.setUpdatedBy("system");
        }
        
        return caseVersionsService.updateById(caseVersions);
    }

    /**
     * 查询所有测试用例版本备份表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseVersions> list() {
        return caseVersionsService.list();
    }


    /**
     * 查询所有测试用例版本备份表。
     *
     * @return 所有数据
     */
    @GetMapping("listByCaseId")
    public Resp listByCaseId(@RequestParam("caseId") Integer caseId, Page<CaseVersions> page) {
        if (page == null) {
            return caseVersionBizService.getListByCaseId(caseId);
        } else {
            return caseVersionBizService.getListByCaseId(caseId, page);
        }
    }

    /**
     * 根据测试用例版本备份表主键获取详细信息。
     *
     * @param id 测试用例版本备份表主键
     * @return 测试用例版本备份表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseVersions getInfo(@PathVariable Integer id) {
        return caseVersionsService.getById(id);
    }

    /**
     * 分页查询测试用例版本备份表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseVersions> page(Page<CaseVersions> page) {
        return caseVersionsService.page(page);
    }

    /**
     * 分页查询测试用例版本备份表。
     *
     * @param caseId 测试用例ID
     * @param page 分页对象
     * @return 分页数据
     */
    @GetMapping("pageByCaseId")
    public Resp pageByCaseId(@RequestParam("caseId") Integer caseId, Page<CaseVersions> page) {
        return caseVersionBizService.pageByCaseId(caseId, page);
    }

}
