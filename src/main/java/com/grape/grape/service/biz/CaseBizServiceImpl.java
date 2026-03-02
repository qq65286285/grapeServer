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
import com.grape.grape.service.CaseVectorSyncService;
import com.grape.grape.service.biz.QdrantSyncBizService;
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
    
    /**
     * 日志记录器
     * 用于记录服务层的执行日志和异常信息
     */
    private static final Logger log = LoggerFactory.getLogger(CaseBizServiceImpl.class);
    
    /**
     * 异步同步线程池大小
     * 用于控制同时进行的向量数据库同步操作数量
     */
    private static final int SYNC_THREAD_POOL_SIZE = 5;
    
    /**
     * 同步状态常量 - 成功
     * 表示同步操作成功完成
     */
    private static final int SYNC_STATUS_SUCCESS = 1;
    
    /**
     * 同步状态常量 - 失败
     * 表示同步操作执行失败
     */
    private static final int SYNC_STATUS_FAILED = 2;
    
    /**
     * 默认用户ID
     * 当无法获取当前登录用户时使用的默认值
     */
    private static final String DEFAULT_USER_ID = "system";
    
    /**
     * 业务类型常量 - 新增
     * 表示测试用例的新增操作
     */
    private static final String BUSINESS_TYPE_ADD = "add";
    
    /**
     * 业务类型常量 - 更新
     * 表示测试用例的更新操作
     */
    private static final String BUSINESS_TYPE_UPDATE = "update";
    
    /**
     * 业务类型常量 - 删除
     * 表示测试用例的删除操作
     */
    private static final String BUSINESS_TYPE_DELETE = "delete";
    
    /**
     * 业务类型常量 - 回滚
     * 表示测试用例的版本回滚操作
     */
    private static final String BUSINESS_TYPE_ROLLBACK = "rollback";
    
    /**
     * 测试用例状态 - 待执行
     */
    private static final int CASE_STATUS_PENDING = 0;
    
    /**
     * 测试用例状态 - 已完成
     */
    private static final int CASE_STATUS_COMPLETED = 1;
    
    /**
     * 测试用例状态 - 失败
     */
    private static final int CASE_STATUS_FAILED = 2;
    
    /**
     * 优先级 - 低
     */
    private static final int PRIORITY_LOW = 1;
    
    /**
     * 优先级 - 中
     */
    private static final int PRIORITY_MEDIUM = 2;
    
    /**
     * 优先级 - 高
     */
    private static final int PRIORITY_HIGH = 3;

    /**
     * 测试用例数据访问服务
     * 负责测试用例的CRUD操作
     * 数据库调用：
     * - SELECT: 查询单个或多个测试用例
     * - INSERT: 插入新的测试用例
     * - UPDATE: 更新测试用例信息
     * - DELETE: 删除测试用例
     */
    @Resource
    private CasesService casesService;
    
    /**
     * 测试用例版本数据访问服务
     * 负责测试用例版本历史的CRUD操作
     * 数据库调用：
     * - SELECT: 查询版本历史记录
     * - INSERT: 插入版本快照
     * - UPDATE: 更新版本信息
     * - DELETE: 删除版本记录
     */
    @Resource
    private CaseVersionsService caseVersionsService;
    
    /**
     * 测试用例步骤数据访问服务
     * 负责测试步骤的CRUD操作
     * 数据库调用：
     * - SELECT: 查询测试用例的步骤列表
     * - INSERT: 插入新的测试步骤
     * - UPDATE: 更新测试步骤信息
     * - DELETE: 删除测试步骤
     */
    @Resource
    private TestCaseStepService testCaseStepService;
    
    /**
     * 测试用例文件夹数据访问服务
     * 负责文件夹结构的查询和管理
     * 数据库调用：
     * - SELECT: 查询文件夹信息
     * - INSERT: 创建新文件夹
     * - UPDATE: 更新文件夹信息
     * - DELETE: 删除文件夹
     */
    @Resource
    private TestCaseFolderService testCaseFolderService;
    
    /**
     * 用户数据访问服务
     * 负责用户信息的查询和管理
     * 数据库调用：
     * - SELECT: 查询用户信息
     * - INSERT: 创建新用户
     * - UPDATE: 更新用户信息
     * - DELETE: 删除用户
     */
    @Autowired
    private UserService userService;
    
    /**
     * Qdrant向量数据库同步业务服务
     * 负责将测试用例数据同步到向量数据库
     * 调用外部服务：
     * - 调用 Qdrant向量数据库进行向量化存储
     * - 用于AI检索和相似度匹配
     */
    @Autowired
    private QdrantSyncBizService qdrantSyncBizService;
    
    /**
     * 测试用例向量同步状态数据访问服务
     * 负责同步状态的记录和查询
     * 数据库调用：
     * - SELECT: 查询同步状态
     * - INSERT: 插入同步记录
     * - UPDATE: 更新同步状态
     */
    @Autowired
    private CaseVectorSyncService caseVectorSyncService;
    
    /**
     * Qdrant向量数据库服务
     * 负责向量数据库的直接操作
     * 调用外部服务：
     * - 调用 Qdrant向量数据库API
     * - 用于点数据的增删改查操作
     */
    @Autowired
    private com.grape.grape.service.QdrantService qdrantService;

    /**
     * 异步同步执行器
     * 用于在后台线程池中执行向量数据库同步操作
     * 避免阻塞主线程，提高系统响应速度
     */
    private final ExecutorService syncExecutor = Executors.newFixedThreadPool(SYNC_THREAD_POOL_SIZE);

    /**
     * 保存测试用例
     * 
     * 功能说明：
     * 1. 验证请求参数的有效性
     * 2. 将请求对象转换为实体对象
     * 3. 保存测试用例基本信息到数据库
     * 4. 保存测试用例步骤到数据库（如果存在）
     * 5. 异步同步到向量数据库
     * 
     * 业务流程：
     * - 开始：接收测试用例保存请求
     * - 参数校验：验证caseRequest不为空
     * - 数据转换：将CaseRequest转换为Cases实体
     * - 保存用例：调用casesService.save()保存到数据库
     * - 保存步骤：如果存在步骤，调用testCaseStepService.saveSteps()保存
     * - 向量同步：异步调用向量数据库同步服务
     * - 结束：返回保存结果
     * 
     * 调用服务：
     * - 调用 casesService.save(cases) 保存测试用例基本信息
     *   该方法会执行数据库插入操作：INSERT INTO cases (...) VALUES (...)
     * - 调用 testCaseStepService.saveSteps(caseId, steps) 保存测试步骤
     *   该方法会批量插入测试步骤：INSERT INTO test_case_steps (...) VALUES (...)
     * - 异步调用 asyncSyncToVectorDb(caseId, BUSINESS_TYPE_ADD) 同步到向量数据库
     *   该方法会调用向量数据库服务进行向量化存储
     * 
     * 调用外部服务：
     * - 异步调用 QdrantSyncBizService.processSingleTestCase(testCaseId)
     *   该服务会：
     *   1. 获取测试用例的完整信息和步骤
     *   2. 将用例内容转换为文本向量
     *   3. 调用Qdrant向量数据库API存储向量
     *   4. 记录同步状态到数据库
     * 
     * 数据库调用：
     * - 插入测试用例：INSERT INTO cases (...) VALUES (...)
     *   参数：title, description, priority, status, folder_id, created_time, created_by等
     * - 插入测试步骤：INSERT INTO test_case_steps (...) VALUES (...)
     *   参数：test_case_id, step_order, step_name, step_description, expected_result等
     * - 插入同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     *   参数：test_case_id, sync_status, sync_message, sync_time, business_type等
     * 
     * @param caseRequest 测试用例请求对象
     * @return Resp 响应对象，包含保存结果
     *         - 成功：code=0, data=true
     *         - 失败：code=400, message=错误描述
     */
    @Override
    public Resp saveCase(CaseRequest caseRequest) {
        // 方法开始：准备保存测试用例
        log.info("开始保存测试用例，标题: {}", caseRequest.getTitle());
        
        // 参数校验：验证请求参数不为空
        if (caseRequest == null) {
            log.warn("保存测试用例失败，请求参数为空");
            return Resp.info(400, "请求参数不能为空");
        }
        
        // 数据转换：将请求对象转换为实体对象
        Cases cases = caseRequest.toCases();
        
        // 调用服务层保存测试用例基本信息
        // 数据库调用：INSERT INTO cases (...) VALUES (...)
        boolean saveResult = casesService.save(cases);
        
        if (saveResult && caseRequest.getSteps() != null && !caseRequest.getSteps().isEmpty()) {
            // 保存测试用例步骤
            // 数据库调用：INSERT INTO test_case_steps (...) VALUES (...)
            testCaseStepService.saveSteps(cases.getId(), caseRequest.getSteps());
            log.info("测试用例步骤保存成功，步骤数量: {}", caseRequest.getSteps().size());
        }
        
        // 异步同步到向量数据库
        // 调用外部服务：Qdrant向量数据库
        if (saveResult) {
            asyncSyncToVectorDb(cases.getId(), BUSINESS_TYPE_ADD);
            log.info("已触发测试用例向量数据库同步，用例ID: {}", cases.getId());
        }
        
        // 方法结束：返回保存结果
        log.info("保存测试用例完成，结果: {}", saveResult ? "成功" : "失败");
        return Resp.ok(saveResult);
    }

    /**
     * 删除测试用例
     * 
     * 功能说明：
     * 1. 先删除测试用例的所有步骤
     * 2. 删除测试用例本身
     * 3. 从向量数据库中删除相关数据
     * 
     * 业务流程：
     * - 开始：接收测试用例删除请求
     * - 步骤删除：调用testCaseStepService.removeByTestCaseId()删除步骤
     * - 用例删除：调用casesService.getMapper().deleteById()删除用例
     * - 向量删除：异步调用向量数据库删除服务
     * - 结束：返回删除结果
     * 
     * 调用服务：
     * - 调用 testCaseStepService.removeByTestCaseId(id) 删除测试步骤
     *   该方法会执行：DELETE FROM test_case_steps WHERE test_case_id = ?
     * - 调用 casesService.getMapper().deleteById(id) 删除测试用例
     *   该方法会执行：DELETE FROM cases WHERE id = ?
     * - 异步调用 asyncDeleteFromVectorDb(id) 从向量数据库删除
     *   该方法会调用向量数据库删除服务
     * 
     * 调用外部服务：
     * - 异步调用 QdrantService.deletePoint(collectionName, pointId)
     *   该服务会调用Qdrant向量数据库API删除指定ID的向量数据
     * 
     * 数据库调用：
     * - 删除测试步骤：DELETE FROM test_case_steps WHERE test_case_id = ?
     *   参数：test_case_id（用例ID）
     * - 删除测试用例：DELETE FROM cases WHERE id = ?
     *   参数：id（用例ID）
     * - 插入同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     *   参数：test_case_id, sync_status, sync_message, sync_time, business_type等
     * 
     * @param id 测试用例ID
     * @return Resp 响应对象，包含删除结果
     *         - 成功：code=0, data=true
     *         - 失败：code=非0, data=false
     */
    @Override
    public Resp removeCase(Integer id) {
        // 方法开始：准备删除测试用例
        log.info("开始删除测试用例，用例ID: {}", id);
        
        // 先删除对应的测试步骤
        // 数据库调用：DELETE FROM test_case_steps WHERE test_case_id = ?
        testCaseStepService.removeByTestCaseId(id);
        log.info("测试用例步骤删除完成，用例ID: {}", id);
        
        // 执行物理删除测试用例
        // 数据库调用：DELETE FROM cases WHERE id = ?
        boolean result = casesService.getMapper().deleteById(id) > 0;
        
        // 异步从向量数据库中删除对应数据
        // 调用外部服务：Qdrant向量数据库
        if (result) {
            asyncDeleteFromVectorDb(id);
            log.info("已触发向量数据库删除，用例ID: {}", id);
        }
        
        // 方法结束：返回删除结果
        log.info("删除测试用例完成，用例ID: {}，结果: {}", id, result ? "成功" : "失败");
        return Resp.ok(result);
    }

    /**
     * 根据ID查询测试用例
     * 
     * 功能说明：
     * 从数据库查询指定ID的测试用例信息
     * 
     * 业务流程：
     * - 开始：接收查询请求
     * - 数据查询：调用casesService.getById()查询用例
     * - 结束：返回查询结果
     * 
     * 调用服务：
     * - 调用 casesService.getById(id) 查询测试用例
     *   该方法会执行：SELECT * FROM cases WHERE id = ?
     * 
     * 数据库调用：
     * - 查询测试用例：SELECT * FROM cases WHERE id = ?
     *   参数：id（用例ID）
     *   返回：Cases实体对象或null
     * 
     * @param id 测试用例ID
     * @return Cases 测试用例实体对象，如果不存在则返回null
     */
    @Override
    public Cases getCaseById(Integer id) {
        // 方法开始：准备查询测试用例
        log.info("开始查询测试用例，用例ID: {}", id);
        
        // 调用服务层查询测试用例
        // 数据库调用：SELECT * FROM cases WHERE id = ?
        Cases cases = casesService.getById(id);
        
        // 方法结束：返回查询结果
        if (cases != null) {
            log.info("查询测试用例成功，用例ID: {}，标题: {}", id, cases.getTitle());
        } else {
            log.info("测试用例不存在，用例ID: {}", id);
        }
        
        return cases;
    }

    /**
     * 查询所有测试用例
     * 
     * 功能说明：
     * 从数据库查询所有测试用例信息
     * 
     * 业务流程：
     * - 开始：接收查询请求
     * - 数据查询：调用casesService.list()查询所有用例
     * - 结束：返回查询结果
     * 
     * 调用服务：
     * - 调用 casesService.list() 查询所有测试用例
     *   该方法会执行：SELECT * FROM cases
     * 
     * 数据库调用：
     * - 查询所有测试用例：SELECT * FROM cases
     *   返回：List<Cases>测试用例列表
     * 
     * @return List<Cases> 测试用例列表
     */
    @Override
    public List<Cases> listCases() {
        // 方法开始：准备查询所有测试用例
        log.info("开始查询所有测试用例");
        
        // 调用服务层查询所有测试用例
        // 数据库调用：SELECT * FROM cases
        List<Cases> cases = casesService.list();
        
        // 方法结束：返回查询结果
        log.info("查询所有测试用例完成，数量: {}", cases.size());
        return cases;
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
        
        // 异步从向量数据库中删除对应数据
        if (result > 0) {
            for (Integer id : ids) {
                asyncDeleteFromVectorDb(id);
            }
        }
        
        return Resp.ok(result > 0);
    }

    @Override
    public Map<String, Object> getCaseStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("total", casesService.count());
        stats.put("status", getStatusStats());
        stats.put("priority", getPriorityStats());
        
        return stats;
    }

    private Map<String, Object> getStatusStats() {
        Map<String, Object> statusStats = new java.util.HashMap<>();
        statusStats.put("pending", casesService.count(new QueryWrapper().eq("status", CASE_STATUS_PENDING)));
        statusStats.put("completed", casesService.count(new QueryWrapper().eq("status", CASE_STATUS_COMPLETED)));
        statusStats.put("failed", casesService.count(new QueryWrapper().eq("status", CASE_STATUS_FAILED)));
        return statusStats;
    }

    private Map<String, Object> getPriorityStats() {
        Map<String, Object> priorityStats = new java.util.HashMap<>();
        priorityStats.put("low", casesService.count(new QueryWrapper().eq("priority", PRIORITY_LOW)));
        priorityStats.put("medium", casesService.count(new QueryWrapper().eq("priority", PRIORITY_MEDIUM)));
        priorityStats.put("high", casesService.count(new QueryWrapper().eq("priority", PRIORITY_HIGH)));
        return priorityStats;
    }

    @Override
    public Resp saveCaseSteps(Integer caseId, List<TestCaseStep> steps) {
        if (caseId == null || steps == null || steps.isEmpty()) {
            return Resp.info(400, "请求参数不能为空");
        }
        
        boolean saveResult = testCaseStepService.saveSteps(caseId, steps);
        
        // 异步同步到向量数据库
        if (saveResult) {
            asyncSyncToVectorDb(caseId, "update");
        }
        
        return Resp.ok(saveResult);
    }

    @Override
    public Resp updateCaseWithSteps(CaseRequest caseRequest) {
        if (caseRequest == null || caseRequest.getId() == null) {
            return Resp.info(400, "请求参数不能为空，且必须包含测试用例ID");
        }
        
        Cases cases = caseRequest.toCasesForUpdate();
        Resp updateResult = updateCase(cases);
        
        log.info("开始更新测试用例步骤，caseId: {}, updateResult.code: {}", cases.getId(), updateResult.getCode());
        
        if (updateResult.getCode() == 0 && caseRequest.getSteps() != null) {
            log.info("执行步骤更新");
            saveCaseSteps(cases.getId(), caseRequest.getSteps());
        } else {
            log.info("跳过步骤更新，原因: updateResult.code = {}, caseRequest.getSteps() = {}", 
                updateResult.getCode(), caseRequest.getSteps());
        }
        
        log.info("测试用例步骤更新完成");
        
        return updateResult;
    }

    @Override
    public Resp updateCase(Cases cases) {
        Cases originalCase = casesService.getById(cases.getId());
        
        long currentTime = System.currentTimeMillis();
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        String userId = (currentUserId != null) ? currentUserId : DEFAULT_USER_ID;
        
        if (originalCase == null) {
            return createNewCase(cases, currentTime, userId);
        }
        
        return updateExistingCase(cases, originalCase, currentTime, userId);
    }

    private Resp createNewCase(Cases cases, long currentTime, String userId) {
        log.info("测试用例不存在，创建新的测试用例，ID: {}", cases.getId());
        
        cases.setCreatedAt(currentTime);
        cases.setCreatedBy(userId);
        cases.setUpdatedAt(currentTime);
        cases.setUpdatedBy(userId);
        
        casesService.save(cases);
        asyncSyncToVectorDb(cases.getId(), BUSINESS_TYPE_ADD);
        
        return Resp.info(ResultEnumI18n.SUCCESS);
    }

    private Resp updateExistingCase(Cases cases, Cases originalCase, long currentTime, String userId) {
        String stepsJson = convertStepsToJson(cases.getId());
        
        saveCaseVersion(originalCase, stepsJson, currentTime, userId);
        
        cases.setVersion(originalCase.getVersion() + 1);
        cases.setUpdatedAt(currentTime);
        cases.setUpdatedBy(userId);
        
        if (currentUserId != null) {
            log.info("设置测试用例更新人: {}，版本号: {}", currentUserId, cases.getVersion());
        } else {
            log.warn("无法获取当前登录用户，使用默认值 'system' 作为更新人");
        }
        
        casesService.updateById(cases);
        asyncSyncToVectorDb(cases.getId(), BUSINESS_TYPE_UPDATE);
        
        return Resp.info(ResultEnumI18n.SUCCESS);
    }

    private String convertStepsToJson(Integer caseId) {
        try {
            List<TestCaseStep> steps = testCaseStepService.getByTestCaseId(caseId);
            if (steps != null && !steps.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(steps);
            }
        } catch (Exception e) {
            log.error("转换步骤信息为JSON失败: {}", e.getMessage(), e);
        }
        return null;
    }

    private void saveCaseVersion(Cases originalCase, String stepsJson, long currentTime, String userId) {
        CaseVersions caseVersions = new CaseVersions().getByCaseDao(originalCase, stepsJson);
        
        caseVersions.setCreatedBy(userId);
        caseVersions.setUpdatedBy(userId);
        caseVersions.setCreatedAt(currentTime);
        caseVersions.setUpdatedAt(currentTime);
        
        caseVersionsService.save(caseVersions);
    }

    @Override
    public Resp rollbackToVersion(Integer caseId, Integer versionId) {
        long currentTime = System.currentTimeMillis();
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        String userId = (currentUserId != null) ? currentUserId : DEFAULT_USER_ID;
        
        CaseVersions targetVersion = caseVersionsService.getById(versionId);
        if (targetVersion == null || !targetVersion.getTestCaseId().equals(caseId)) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        Cases currentCase = casesService.getById(caseId);
        if (currentCase == null) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        saveCurrentVersionAsHistory(caseId, currentTime, userId);
        performRollback(caseId, targetVersion, currentCase, currentTime, userId);
        
        return Resp.info(ResultEnumI18n.SUCCESS);
    }

    private void saveCurrentVersionAsHistory(Integer caseId, long currentTime, String userId) {
        String stepsJson = convertStepsToJson(caseId);
        Cases currentCase = casesService.getById(caseId);
        
        if (currentCase != null) {
            CaseVersions currentVersionRecord = new CaseVersions().getByCaseDao(currentCase, stepsJson);
            currentVersionRecord.setCreatedBy(userId);
            currentVersionRecord.setUpdatedBy(userId);
            currentVersionRecord.setCreatedAt(currentTime);
            currentVersionRecord.setUpdatedAt(currentTime);
            caseVersionsService.save(currentVersionRecord);
        }
    }

    private void performRollback(Integer caseId, CaseVersions targetVersion, Cases currentCase, long currentTime, String userId) {
        Cases rolledBackCase = new Cases();
        rolledBackCase.setId(caseId);
        rolledBackCase.setCaseNumber(targetVersion.getCaseNumber());
        rolledBackCase.setTitle(targetVersion.getTitle());
        rolledBackCase.setDescription(targetVersion.getDescription());
        rolledBackCase.setPriority(targetVersion.getPriority());
        rolledBackCase.setStatus(targetVersion.getCaseState());
        rolledBackCase.setVersion(currentCase.getVersion() + 1);
        rolledBackCase.setEnvironmentId(targetVersion.getEnvironmentId());
        rolledBackCase.setExpectedResult(targetVersion.getExpectedResult());
        rolledBackCase.setModule(targetVersion.getModule());
        rolledBackCase.setFolderId(targetVersion.getFolderId());
        rolledBackCase.setRemark(targetVersion.getRemark());
        rolledBackCase.setUpdatedAt(currentTime);
        rolledBackCase.setUpdatedBy(userId);
        
        casesService.updateById(rolledBackCase);
        rollbackCaseSteps(caseId, targetVersion);
        asyncSyncToVectorDb(caseId, BUSINESS_TYPE_ROLLBACK);
    }

    private void rollbackCaseSteps(Integer caseId, CaseVersions targetVersion) {
        if (targetVersion.getStepsJson() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<TestCaseStep> rolledBackSteps = objectMapper.readValue(
                    targetVersion.getStepsJson(), 
                    new TypeReference<List<TestCaseStep>>() {}
                );
                testCaseStepService.saveSteps(caseId, rolledBackSteps);
                log.info("步骤回滚成功，用例ID: {}", caseId);
            } catch (Exception e) {
                log.error("回滚步骤信息失败，用例ID: {}, 错误: {}", caseId, e.getMessage(), e);
            }
        } else {
            testCaseStepService.removeByTestCaseId(caseId);
            log.info("版本中无步骤信息，已删除当前步骤，用例ID: {}", caseId);
        }
    }
    
    private void asyncSyncToVectorDb(Integer testCaseId, String businessType) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始异步同步测试用例到向量数据库，testCaseId: {}, businessType: {}", testCaseId, businessType);
                
                Map<String, Object> syncResult = qdrantSyncBizService.processSingleTestCase(testCaseId);
                
                boolean success = (Boolean) syncResult.getOrDefault("success", false);
                String message = (String) syncResult.getOrDefault("message", "");
                
                saveSyncRecord(testCaseId, businessType, success, message);
                
                log.info("测试用例同步到向量数据库完成，testCaseId: {}, businessType: {}, success: {}, message: {}", 
                    testCaseId, businessType, success, message);
                
            } catch (Exception e) {
                log.error("同步测试用例到向量数据库时发生异常，testCaseId: {}, businessType: {}", testCaseId, businessType, e);
                saveSyncRecord(testCaseId, businessType, false, "同步异常: " + e.getMessage());
            }
        }, syncExecutor);
    }

    private void saveSyncRecord(Integer testCaseId, String businessType, boolean success, String message) {
        try {
            com.grape.grape.entity.CaseVectorSync syncRecord = new com.grape.grape.entity.CaseVectorSync();
            syncRecord.setTestCaseId(testCaseId);
            syncRecord.setSyncStatus(success ? SYNC_STATUS_SUCCESS : SYNC_STATUS_FAILED);
            syncRecord.setSyncMessage(message);
            syncRecord.setSyncTime(System.currentTimeMillis());
            syncRecord.setRetryCount(0);
            syncRecord.setBusinessType(businessType);
            syncRecord.setCreatedAt(System.currentTimeMillis());
            syncRecord.setUpdatedAt(System.currentTimeMillis());
            
            caseVectorSyncService.saveOrUpdate(syncRecord);
        } catch (Exception ex) {
            log.error("记录同步状态时发生异常", ex);
        }
    }
    
    private void asyncDeleteFromVectorDb(Integer testCaseId) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始异步从向量数据库中删除测试用例，testCaseId: {}", testCaseId);
                
                boolean deleteSuccess = qdrantService.deletePoint("test_case_memory", testCaseId);
                
                String message = deleteSuccess ? "删除成功" : "删除失败";
                saveSyncRecord(testCaseId, BUSINESS_TYPE_DELETE, deleteSuccess, message);
                
                log.info("从向量数据库中删除测试用例完成，testCaseId: {}, success: {}", testCaseId, deleteSuccess);
                
            } catch (Exception e) {
                log.error("从向量数据库中删除测试用例时发生异常，testCaseId: {}", testCaseId, e);
                saveSyncRecord(testCaseId, BUSINESS_TYPE_DELETE, false, "删除异常: " + e.getMessage());
            }
        }, syncExecutor);
    }
}
