package com.grape.grape.service.biz;


import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.CaseVersions;
import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.entity.User;
import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.grape.grape.model.vo.CaseRequest;
import com.grape.grape.service.CaseVersionsService;
import com.grape.grape.service.CasesService;
import com.grape.grape.service.TestCaseFolderService;
import com.grape.grape.service.TestCaseStepService;
import com.grape.grape.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CaseBizServiceImpl implements CaseBizService {

    private static final Logger log = LoggerFactory.getLogger(CaseBizServiceImpl.class);

    @Resource
    CasesService casesService;
    @Resource
    CaseVersionsService caseVersionsService;
    @Resource
    TestCaseStepService testCaseStepService;
    @Resource
    TestCaseFolderService testCaseFolderService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private CaseNumberGeneratorService caseNumberGeneratorService;

    @Override
    public Resp saveCase(CaseRequest caseRequest) {
        if (caseRequest == null) {
            return Resp.info(400, "请求参数不能为空");
        }
        
        // 自动生成测试用例编号
        String caseNumber = caseNumberGeneratorService.generateCaseNumber();
        caseRequest.setCaseNumber(caseNumber);
        
        // 使用 CaseRequest 的转换方法构建测试用例对象
        Cases cases = caseRequest.toCases();
        
        // 保存测试用例
        boolean saveResult = casesService.save(cases);
        if (saveResult && caseRequest.getSteps() != null && !caseRequest.getSteps().isEmpty()) {
            // 保存测试用例步骤
            testCaseStepService.saveSteps(cases.getId(), caseRequest.getSteps());
        }
        

        
        return Resp.ok(cases);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Resp batchUpdateCases(List<CaseRequest> caseRequests) {
        if (caseRequests == null || caseRequests.isEmpty()) {
            return Resp.info(400, "请求参数不能为空");
        }
        
        try {
            for (CaseRequest caseRequest : caseRequests) {
                if (caseRequest == null || caseRequest.getId() == null) {
                    throw new IllegalArgumentException("测试用例ID不能为空");
                }
                
                // 使用 CaseRequest 的转换方法构建测试用例对象
                Cases cases = caseRequest.toCasesForUpdate();
                
                // 更新测试用例
                Resp updateResult = updateCase(cases);
                
                // 更新测试用例步骤
                if (updateResult.getCode() == 0 && caseRequest.getSteps() != null) {
                    saveCaseSteps(cases.getId(), caseRequest.getSteps());
                }
            }
            return Resp.ok(true);
        } catch (Exception e) {
            log.error("批量更新测试用例失败: {}", e.getMessage(), e);
            return Resp.info(500, "批量更新测试用例失败: " + e.getMessage());
        }
    }

    @Override
    public Resp removeCase(Integer id) {
        // 先删除对应的测试步骤
        testCaseStepService.removeByTestCaseId(id);
        // 执行物理删除测试用例
        boolean result = casesService.getMapper().deleteById(id) > 0;
        

        
        return Resp.ok(result);
    }

    @Override
    public Cases getCaseById(Integer id) {
        return casesService.getById(id);
    }

    @Override
    public List<Cases> listCases() {
        return casesService.list();
    }

    @Override
    public List<Cases> listByFolderId(Integer folderId) {
        // 获取所有子文件夹ID（包括自己）
        List<Integer> folderIds = new java.util.ArrayList<>();
        collectAllFolderIds(folderId, folderIds);
        
        // 查询所有文件夹中的用例
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in("folder_id", folderIds);
        return casesService.list(queryWrapper);
    }
    
    /**
     * 递归收集所有子文件夹ID
     * @param folderId 文件夹ID
     * @param folderIds 收集结果
     */
    private void collectAllFolderIds(Integer folderId, List<Integer> folderIds) {
        if (folderId == null) {
            return;
        }
        
        // 添加当前文件夹
        folderIds.add(folderId);
        
        // 递归获取子文件夹
        List<com.grape.grape.entity.TestCaseFolder> subFolders = testCaseFolderService.getByParentId(folderId);
        for (com.grape.grape.entity.TestCaseFolder folder : subFolders) {
            collectAllFolderIds(folder.getId(), folderIds);
        }
    }

    @Override
    public PageResp pageCases(Integer pageNum, Integer pageSize) {
        com.mybatisflex.core.paginate.Page<Cases> page = new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize);
        page = casesService.page(page);
        PageResp pageResp = new PageResp();
        return pageResp.pageInfoOk(page);
    }

    @Override
    public PageResp pageCases(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (params != null) {
            // 根据条件构建查询
            if (params.containsKey("folderId")) {
                queryWrapper.eq("folder_id", params.get("folderId"));
            }
            if (params.containsKey("module")) {
                queryWrapper.eq("module", params.get("module"));
            }
            if (params.containsKey("priority")) {
                queryWrapper.eq("priority", params.get("priority"));
            }
            if (params.containsKey("status")) {
                queryWrapper.eq("status", params.get("status"));
            }
            if (params.containsKey("keyword")) {
                    String keyword = params.get("keyword").toString();
                    // 简化查询条件，只使用一个 like 条件
                    queryWrapper.like("title", keyword);
                }
        }
        com.mybatisflex.core.paginate.Page<Cases> page = new com.mybatisflex.core.paginate.Page<>(pageNum, pageSize);
        page = casesService.page(page, queryWrapper);
        PageResp pageResp = new PageResp();
        return pageResp.pageInfoOk(page);
    }

    @Override
    public Map<String, Object> getCaseDetail(Integer id) {
        Cases cases = casesService.getById(id);
        if (cases == null) {
            return null;
        }
        
        // 获取测试用例步骤
        List<TestCaseStep> steps = testCaseStepService.list(new QueryWrapper().eq("test_case_id", id));
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("case", cases);
        result.put("steps", steps);
        
        return result;
    }

    @Override
    public Resp batchRemoveCases(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Resp.info(400, "请选择要删除的测试用例");
        }
        // 先删除每个测试用例对应的测试步骤
        for (Integer id : ids) {
            testCaseStepService.removeByTestCaseId(id);
        }
        // 执行物理删除测试用例
        int result = casesService.getMapper().deleteBatchByIds(ids);
        

        
        return Resp.ok(result > 0);
    }

    @Override
    public Map<String, Object> getCaseStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // 总用例数
        stats.put("total", casesService.count());
        
        // 按状态统计
        Map<String, Object> statusStats = new java.util.HashMap<>();
        statusStats.put("pending", casesService.count(new QueryWrapper().eq("status", 0)));
        statusStats.put("completed", casesService.count(new QueryWrapper().eq("status", 1)));
        statusStats.put("failed", casesService.count(new QueryWrapper().eq("status", 2)));
        stats.put("status", statusStats);
        
        // 按优先级统计
        Map<String, Object> priorityStats = new java.util.HashMap<>();
        priorityStats.put("low", casesService.count(new QueryWrapper().eq("priority", 1)));
        priorityStats.put("medium", casesService.count(new QueryWrapper().eq("priority", 2)));
        priorityStats.put("high", casesService.count(new QueryWrapper().eq("priority", 3)));
        stats.put("priority", priorityStats);
        
        return stats;
    }

    @Override
    public Resp saveCaseSteps(Integer caseId, List<TestCaseStep> steps) {
        if (caseId == null || steps == null || steps.isEmpty()) {
            return Resp.info(400, "请求参数不能为空");
        }
        
        boolean saveResult = testCaseStepService.saveSteps(caseId, steps);
        

        
        return Resp.ok(saveResult);
    }

    @Override
    public Resp updateCaseWithSteps(CaseRequest caseRequest) {
        if (caseRequest == null || caseRequest.getId() == null) {
            return Resp.info(400, "请求参数不能为空，且必须包含测试用例ID");
        }
        
        // 获取原测试用例信息，保留原编号
        Cases originalCase = casesService.getById(caseRequest.getId());
        if (originalCase != null) {
            caseRequest.setCaseNumber(originalCase.getCaseNumber());
        }
        
        // 使用 CaseRequest 的转换方法构建测试用例对象
        Cases cases = caseRequest.toCasesForUpdate();
        
        // 更新测试用例
        Resp updateResult = updateCase(cases);
        
        // 更新测试用例步骤
        log.info("=== 开始更新测试用例步骤 ===");
        log.info("caseId: {}", cases.getId());
        log.info("steps: {}", caseRequest.getSteps());
        log.info("updateResult.code: {}", updateResult.getCode());
        if (updateResult.getCode() == 0 && caseRequest.getSteps() != null) {
            log.info("执行步骤更新");
            saveCaseSteps(cases.getId(), caseRequest.getSteps());
        } else {
            log.info("跳过步骤更新，原因: updateResult.code = {}, caseRequest.getSteps() = {}", updateResult.getCode(), caseRequest.getSteps());
        }
        log.info("=== 测试用例步骤更新完成 ===");
        
        return updateResult;
    }

    @Override
    public Resp updateCase(Cases cases){
        //当前版本做记录
        Cases caseOri = casesService.getById(cases.getId());
        
        // 获取当前时间戳和当前登录用户ID
        long currentTime = System.currentTimeMillis();
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        String userId = (currentUserId != null) ? currentUserId : "system";
        
        if(null == caseOri){
            // 测试用例不存在，创建新的测试用例
            log.info("测试用例不存在，创建新的测试用例，ID: {}", cases.getId());
            
            // 设置创建时间和创建人
            cases.setCreatedAt(currentTime);
            cases.setCreatedBy(userId);
            cases.setUpdatedAt(currentTime);
            cases.setUpdatedBy(userId);
            
            // 保存测试用例
            casesService.save(cases);
            

            
            return Resp.info(ResultEnumI18n.SUCCESS);
        }

        // 获取测试用例的步骤信息
        String stepsJson = null;
        try {
            List<TestCaseStep> steps = testCaseStepService.getByTestCaseId(cases.getId());
            if (steps != null && !steps.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                stepsJson = objectMapper.writeValueAsString(steps);
            }
        } catch (Exception e) {
            log.error("转换步骤信息为JSON失败: {}", e.getMessage());
        }
        
        // 创建版本记录
        CaseVersions caseVersions = new CaseVersions().getByCaseDao(caseOri, stepsJson);
        
        // 设置版本记录的创建人和更新人
        caseVersions.setCreatedBy(userId);
        caseVersions.setUpdatedBy(userId);
        
        // 设置版本记录的创建时间和更新时间
        caseVersions.setCreatedAt(currentTime);
        caseVersions.setUpdatedAt(currentTime);
        
        //入库用例版本，历史记录
        caseVersionsService.save(caseVersions);
        
        //更新用例
        cases.setVersion(caseOri.getVersion() + 1);
        
        // 设置更新时间和更新人
        cases.setUpdatedAt(currentTime);
        cases.setUpdatedBy(userId);
        
        // 记录日志
        if (currentUserId != null) {
            log.info("设置测试用例更新人: {}，版本号: {}", currentUserId, cases.getVersion());
        } else {
            log.warn("无法获取当前登录用户，使用默认值 'system' 作为更新人");
        }
        
        casesService.updateById(cases);
        

        
        return Resp.info(ResultEnumI18n.SUCCESS);
    }

    @Override
    public Resp rollbackToVersion(Integer caseId, Integer versionId) {
        // 获取当前时间戳和当前登录用户ID
        long currentTime = System.currentTimeMillis();
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        String userId = (currentUserId != null) ? currentUserId : "system";
        
        // 获取版本信息
        CaseVersions caseVersions = caseVersionsService.getById(versionId);
        if (caseVersions == null) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        // 验证版本是否属于指定的测试用例
        if (!caseVersions.getTestCaseId().equals(caseId)) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        // 获取当前测试用例
        Cases currentCase = casesService.getById(caseId);
        if (currentCase == null) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        // 保存当前版本作为历史记录
        String stepsJson = null;
        try {
            List<TestCaseStep> steps = testCaseStepService.getByTestCaseId(caseId);
            if (steps != null && !steps.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                stepsJson = objectMapper.writeValueAsString(steps);
            }
        } catch (Exception e) {
            log.error("转换步骤信息为JSON失败: {}", e.getMessage());
        }
        
        // 创建当前版本的历史记录
        CaseVersions currentVersionRecord = new CaseVersions().getByCaseDao(currentCase, stepsJson);
        currentVersionRecord.setCreatedBy(userId);
        currentVersionRecord.setUpdatedBy(userId);
        currentVersionRecord.setCreatedAt(currentTime);
        currentVersionRecord.setUpdatedAt(currentTime);
        caseVersionsService.save(currentVersionRecord);
        
        // 回滚测试用例信息
        Cases rolledBackCase = new Cases();
        rolledBackCase.setId(caseId);
        rolledBackCase.setCaseNumber(caseVersions.getCaseNumber());
        rolledBackCase.setTitle(caseVersions.getTitle());
        rolledBackCase.setDescription(caseVersions.getDescription());
        rolledBackCase.setPriority(caseVersions.getPriority());
        rolledBackCase.setStatus(caseVersions.getCaseState());
        rolledBackCase.setVersion(currentCase.getVersion() + 1);
        rolledBackCase.setEnvironmentId(caseVersions.getEnvironmentId());
        rolledBackCase.setExpectedResult(caseVersions.getExpectedResult());
        rolledBackCase.setModule(caseVersions.getModule());
        rolledBackCase.setFolderId(caseVersions.getFolderId());
        rolledBackCase.setRemark(caseVersions.getRemark());
        rolledBackCase.setUpdatedAt(currentTime);
        rolledBackCase.setUpdatedBy(userId);
        
        // 更新测试用例
        casesService.updateById(rolledBackCase);
        
        // 回滚测试用例步骤
        log.info("开始回滚测试用例步骤，caseId: {}, stepsJson: {}", caseId, caseVersions.getStepsJson());
        if (caseVersions.getStepsJson() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                log.info("解析步骤JSON: {}", caseVersions.getStepsJson());
                List<TestCaseStep> rolledBackSteps = objectMapper.readValue(caseVersions.getStepsJson(), new TypeReference<List<TestCaseStep>>() {});
                log.info("解析得到步骤列表: {}", rolledBackSteps);
                boolean saveResult = testCaseStepService.saveSteps(caseId, rolledBackSteps);
                log.info("步骤回滚结果: {}", saveResult);
            } catch (Exception e) {
                log.error("回滚步骤信息失败: {}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            // 如果版本中没有步骤信息，删除当前步骤
            log.info("版本中没有步骤信息，删除当前步骤");
            testCaseStepService.removeByTestCaseId(caseId);
        }
        log.info("步骤回滚完成");
        

        
        return Resp.info(ResultEnumI18n.SUCCESS);
    }
    

}
