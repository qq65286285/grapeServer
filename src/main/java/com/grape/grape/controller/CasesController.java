package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.CaseRequest;
import com.grape.grape.service.biz.CaseBizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.Cases;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试用例表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/cases")
public class CasesController {


    @Autowired
    private CaseBizService caseBizService;

    /**
     * 添加测试用例表。
     *
     * @param caseRequest 测试用例请求体
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public Resp save(@RequestBody CaseRequest caseRequest) {
        return caseBizService.saveCase(caseRequest);
    }

    /**
     * 根据主键删除测试用例表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Integer id) {
        return caseBizService.removeCase(id);
    }

    /**
     * 根据主键更新测试用例表。
     *
     * @param caseRequest 测试用例请求体
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public Resp update(@RequestBody CaseRequest caseRequest) {
        return caseBizService.updateCaseWithSteps(caseRequest);
    }

    /**
     * 根据主键查询测试用例表。
     *
     * @param id 主键
     * @return 测试用例表
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Integer id) {
        Map<String, Object> detail = caseBizService.getCaseDetail(id);
        if (detail == null) {
            return Resp.info(404, "测试用例不存在");
        }
        return Resp.ok(detail);
    }

    /**
     * 查询所有测试用例表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Cases> list() {
        return caseBizService.listCases();
    }

    /**
     * 根据文件夹ID查询测试用例
     */
    @PostMapping("listByFolderId")
    public Resp listByFolderId(@RequestBody Map<String, Object> params) {
        if (params != null && params.containsKey("folderId")) {
            Object folderIdObj = params.get("folderId");
            if (folderIdObj instanceof Integer) {
                return Resp.ok(caseBizService.listByFolderId((Integer) folderIdObj));
            } else if (folderIdObj instanceof String) {
                try {
                    return Resp.ok(caseBizService.listByFolderId(Integer.parseInt((String) folderIdObj)));
                } catch (NumberFormatException e) {
                    // 忽略类型转换异常
                }
            }
        }
        return Resp.ok(new ArrayList<>());
    }

    /**
     * 分页查询
     */
    @GetMapping("page")
    public PageResp page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        return caseBizService.pageCases(pageNum, pageSize);
    }

    /**
     * 分页查询（带条件）
     */
    @PostMapping("page")
    public PageResp page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestBody(required = false) Map<String, Object> params) {
        return caseBizService.pageCases(pageNum, pageSize, params);
    }

    /**
     * 获取测试用例详情（包含步骤）
     */
    @GetMapping("detail/{id}")
    public Resp detail(@PathVariable Integer id) {
        Map<String, Object> detail = caseBizService.getCaseDetail(id);
        if (detail == null) {
            return Resp.info(404, "测试用例不存在");
        }
        return Resp.ok(detail);
    }

    /**
     * 批量删除测试用例
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Integer> ids) {
        return caseBizService.batchRemoveCases(ids);
    }

    /**
     * 批量更新测试用例
     */
    @PutMapping("batchUpdate")
    public Resp batchUpdate(@RequestBody List<CaseRequest> caseRequests) {
        return caseBizService.batchUpdateCases(caseRequests);
    }

    /**
     * 获取测试用例统计信息
     */
    @GetMapping("stats")
    public Resp stats() {
        Map<String, Object> stats = caseBizService.getCaseStats();
        return Resp.ok(stats);
    }

    /**
     * 回滚测试用例到指定版本
     */
    @PostMapping("rollback")
    public Resp rollback(@RequestBody Map<String, Integer> params) {
        if (params != null && params.containsKey("caseId") && params.containsKey("versionId")) {
            return caseBizService.rollbackToVersion(params.get("caseId"), params.get("versionId"));
        }
        return Resp.info(400, "请求参数不能为空，且必须包含caseId和versionId");
    }
}
