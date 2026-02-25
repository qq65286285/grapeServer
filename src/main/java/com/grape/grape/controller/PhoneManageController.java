package com.grape.grape.controller;

import com.grape.grape.entity.CaseVersions;
import com.grape.grape.model.Resp;
import com.grape.grape.service.CaseVersionsService;
import com.grape.grape.service.biz.CaseVersionBizService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用例版本备份表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/phone")
public class PhoneManageController {

    /**
     * 添加测试用例版本备份表。
     *
     * @param caseVersions 测试用例版本备份表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseVersions caseVersions) {
        return true ;
    }

    /**
     * 根据主键删除测试用例版本备份表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return true ;

    }

    /**
     * 根据主键更新测试用例版本备份表。
     *
     * @param caseVersions 测试用例版本备份表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseVersions caseVersions) {
        return true ;
    }

    /**
     * 查询所有测试用例版本备份表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        List<Object> results = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        result.put("id", 1);
        result.put("name", "苹果手机1");
        result.put("ip", "127.0.0.1");
        result.put("port", "8888");
        result.put("color", "red");
        result.put("image","11");
        result.put("phoneNumber", "123456789");
        result.put("model", "爱疯14");
        result.put("sn", "PH1");
        results.add( result);

        result = new HashMap<>();
        result.put("id", 2);
        result.put("name", "安卓手机2");
        result.put("ip", "127.0.0.2");
        result.put("port", "8888");
        result.put("color", "red");
        result.put("image","11");
        result.put("phoneNumber", "123456789");
        result.put("model", "华为4折屏");
        result.put("sn", "PH1");
        results.add( result);
        return Resp.ok( results);
    }


    /**
     * 查询所有测试用例版本备份表。
     *
     * @return 所有数据
     */
    @GetMapping("listById")
    public Resp listById(@RequestParam("id") Integer caseId) {
        Map<String,Object> result = new HashMap<>();
        result.put("id", 1);
        result.put("name", "苹果手机1");
        result.put("ip", "127.0.0.1");
        result.put("port", "8888");
        result.put("color", "red");

        return Resp.ok(result);
    }

    /**
     * 根据测试用例版本备份表主键获取详细信息。
     *
     * @param id 测试用例版本备份表主键
     * @return 测试用例版本备份表详情
     */
    @GetMapping("getInfo/{id}")
    public Map<String,Object> getInfo(@PathVariable Integer id) {
        Map<String,Object> result = new HashMap<>();
        result.put("id", 1);
        result.put("name", "苹果手机1");
        result.put("ip", "127.0.0.1");
        result.put("port", "8888");
        result.put("color", "red");
        return result;
    }


}
