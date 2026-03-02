package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.CaseRequest;
import com.grape.grape.service.biz.CaseBizService;
import com.grape.grape.entity.Cases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试用例控制器
 * 提供测试用例管理的API接口，包括测试用例的增删改查、版本管理、统计分析等功能
 * 
 * 主要功能：
 * 1. 测试用例的CRUD操作
 * 2. 测试用例的步骤管理
 * 3. 测试用例的版本控制和回滚
 * 4. 测试用例的统计分析和查询
 * 5. 批量操作支持
 * 
 * @author grape-team
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/cases")
public class CasesController {

    private static final Logger log = LoggerFactory.getLogger(CasesController.class);

    /**
     * 测试用例业务服务
     * 负责测试用例的业务逻辑处理，包括用例管理、版本控制、向量同步等
     */
    @Autowired
    private CaseBizService caseBizService;

    /**
     * 添加测试用例
     * 
     * 功能说明：
     * 1. 接收测试用例的基本信息（标题、描述、优先级等）
     * 2. 接收测试用例的步骤列表
     * 3. 将测试用例信息保存到数据库
     * 4. 将测试用例步骤保存到数据库
     * 5. 异步将测试用例同步到向量数据库（用于AI检索）
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析测试用例数据
     * - 参数校验：验证必填字段的完整性
     * - 数据转换：将请求对象转换为实体对象
     * - 数据保存：调用服务层保存测试用例和步骤
     * - 向量同步：异步触发向量数据库同步
     * - 响应构建：返回保存结果
     * - 结束：完成测试用例创建流程
     * 
     * 调用服务：
     * - 调用 caseBizService.saveCase(caseRequest) 保存测试用例
     *   该方法内部会执行以下操作：
     *   1. 参数校验：验证caseRequest的有效性
     *   2. 实体转换：将CaseRequest转换为Cases实体
     *   3. 数据保存：执行INSERT语句保存用例信息
     *   4. 步骤保存：保存测试用例的步骤列表
     *   5. 向量同步：异步调用向量数据库同步服务
     * 
     * 调用外部服务：
     * - 异步调用 QdrantSyncBizService.processSingleTestCase(testCaseId)
     *   该服务会：
     *   1. 获取测试用例的完整信息和步骤
     *   2. 将用例内容转换为文本向量
     *   3. 将向量存储到Qdrant向量数据库
     *   4. 记录同步状态到数据库
     * 
     * 数据库调用：
     * - 插入测试用例：INSERT INTO cases (...) VALUES (...)
     * - 插入测试步骤：INSERT INTO test_case_steps (...) VALUES (...)
     * - 插入同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     * 
     * @param caseRequest 测试用例请求对象，包含以下字段：
     *                    - title: 用例标题（必填）
     *                    - description: 用例描述
     *                    - priority: 优先级（1-低，2-中，3-高）
     *                    - status: 状态（0-待执行，1-已完成，2-失败）
     *                    - folderId: 所属文件夹ID
     *                    - steps: 测试步骤列表
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=400, message=错误描述
     */
    @PostMapping("save")
    public Resp save(@RequestBody CaseRequest caseRequest) {
        // 方法开始：准备保存测试用例
        log.info("开始添加测试用例，标题: {}", caseRequest.getTitle());
        
        // 调用服务层保存测试用例
        // 该调用会：
        // 1. 保存测试用例基本信息
        // 2. 保存测试用例步骤
        // 3. 异步同步到向量数据库
        Resp result = caseBizService.saveCase(caseRequest);
        
        // 方法结束：返回保存结果
        log.info("添加测试用例完成，结果: {}", result.getCode() == 0 ? "成功" : "失败");
        return result;
    }

    /**
     * 删除测试用例
     * 
     * 功能说明：
     * 1. 接收测试用例ID
     * 2. 删除测试用例的所有步骤
     * 3. 删除测试用例本身
     * 4. 从向量数据库中删除对应的向量数据
     * 
     * 业务流程：
     * - 开始：接收HTTP DELETE请求，路径中包含用例ID
     * - 参数提取：从路径变量中提取用例ID
     * - 步骤删除：先删除测试用例的所有步骤
     * - 用例删除：删除测试用例记录
     * - 向量删除：从向量数据库删除相关数据
     * - 响应构建：返回删除结果
     * - 结束：完成测试用例删除流程
     * 
     * 调用服务：
     * - 调用 caseBizService.removeCase(id) 删除测试用例
     *   该方法内部会执行以下操作：
     *   1. 删除测试步骤：DELETE FROM test_case_steps WHERE test_case_id = ?
     *   2. 删除测试用例：DELETE FROM cases WHERE id = ?
     *   3. 向量删除：异步调用向量数据库删除服务
     * 
     * 调用外部服务：
     * - 异步调用 QdrantService.deletePoint(collectionName, pointId)
     *   该服务会从Qdrant向量数据库中删除指定ID的向量数据
     * 
     * 数据库调用：
     * - 删除测试步骤：DELETE FROM test_case_steps WHERE test_case_id = ?
     * - 删除测试用例：DELETE FROM cases WHERE id = ?
     * - 更新同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     * 
     * @param id 测试用例主键ID，从URL路径中获取
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=400, message=错误描述
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Integer id) {
        // 方法开始：准备删除测试用例
        log.info("开始删除测试用例，用例ID: {}", id);
        
        // 调用服务层删除测试用例
        // 该调用会：
        // 1. 删除测试用例的所有步骤
        // 2. 删除测试用例本身
        // 3. 从向量数据库删除相关数据
        Resp result = caseBizService.removeCase(id);
        
        // 方法结束：返回删除结果
        log.info("删除测试用例完成，结果: {}", result.getCode() == 0 ? "成功" : "失败");
        return result;
    }

    /**
     * 更新测试用例（包含步骤）
     * 
     * 功能说明：
     * 1. 接收测试用例的更新信息
     * 2. 保存当前版本为历史记录
     * 3. 更新测试用例的基本信息
     * 4. 更新测试用例的步骤
     * 5. 异步同步到向量数据库
     * 
     * 业务流程：
     * - 开始：接收HTTP PUT请求，解析更新数据
     * - 参数校验：验证用例ID和必填字段
     * - 版本保存：保存当前版本到历史记录表
     * - 用例更新：更新测试用例的基本信息
     * - 步骤更新：更新测试用例的步骤列表
     * - 向量同步：异步触发向量数据库同步
     * - 响应构建：返回更新结果
     * - 结束：完成测试用例更新流程
     * 
     * 调用服务：
     * - 调用 caseBizService.updateCaseWithSteps(caseRequest) 更新测试用例
     *   该方法内部会执行以下操作：
     *   1. 参数校验：验证caseRequest的有效性
     *   2. 版本保存：将当前用例保存到case_versions表
     *   3. 用例更新：UPDATE cases SET ... WHERE id = ?
     *   4. 步骤更新：先删除旧步骤，再插入新步骤
     *   5. 向量同步：异步调用向量数据库同步服务
     * 
     * 调用外部服务：
     * - 异步调用 QdrantSyncBizService.processSingleTestCase(testCaseId)
     *   该服务会更新向量数据库中的测试用例向量数据
     * 
     * 数据库调用：
     * - 保存版本：INSERT INTO case_versions (...) VALUES (...)
     * - 更新用例：UPDATE cases SET ... WHERE id = ?
     * - 删除旧步骤：DELETE FROM test_case_steps WHERE test_case_id = ?
     * - 插入新步骤：INSERT INTO test_case_steps (...) VALUES (...)
     * - 更新同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     * 
     * @param caseRequest 测试用例请求对象，包含以下字段：
     *                    - id: 用例ID（必填）
     *                    - title: 用例标题
     *                    - description: 用例描述
     *                    - priority: 优先级
     *                    - status: 状态
     *                    - steps: 测试步骤列表
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=400, message=错误描述
     */
    @PutMapping("update")
    public Resp update(@RequestBody CaseRequest caseRequest) {
        // 方法开始：准备更新测试用例
        log.info("开始更新测试用例，用例ID: {}", caseRequest.getId());
        
        // 调用服务层更新测试用例（包含步骤）
        // 该调用会：
        // 1. 保存当前版本为历史记录
        // 2. 更新测试用例基本信息
        // 3. 更新测试用例步骤
        // 4. 异步同步到向量数据库
        Resp result = caseBizService.updateCaseWithSteps(caseRequest);
        
        // 方法结束：返回更新结果
        log.info("更新测试用例完成，结果: {}", result.getCode() == 0 ? "成功" : "失败");
        return result;
    }

    /**
     * 查询测试用例详情
     * 
     * 功能说明：
     * 1. 接收测试用例ID
     * 2. 查询测试用例的基本信息
     * 3. 查询测试用例的所有步骤
     * 4. 返回完整的用例详情
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，路径中包含用例ID
     * - 参数提取：从路径变量中提取用例ID
     * - 用例查询：查询测试用例的基本信息
     * - 步骤查询：查询测试用例的所有步骤
     * - 数据组装：将用例信息和步骤信息组装返回
     * - 响应构建：返回用例详情
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 caseBizService.getCaseDetail(id) 获取用例详情
     *   该方法内部会执行以下操作：
     *   1. 查询用例信息：SELECT * FROM cases WHERE id = ?
     *   2. 查询步骤信息：SELECT * FROM test_case_steps WHERE test_case_id = ?
     *   3. 组装返回数据
     * 
     * 数据库调用：
     * - 查询用例：SELECT * FROM cases WHERE id = ?
     * - 查询步骤：SELECT * FROM test_case_steps WHERE test_case_id = ? ORDER BY step_order
     * 
     * @param id 测试用例主键ID，从URL路径中获取
     * @return Resp 响应对象，包含用例详情
     *         - 成功时：code=0, data={case: 用例信息, steps: 步骤列表}
     *         - 失败时：code=404, message="测试用例不存在"
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Integer id) {
        // 方法开始：准备查询测试用例详情
        log.info("开始查询测试用例详情，用例ID: {}", id);
        
        // 调用服务层获取用例详情
        // 该调用会：
        // 1. 查询测试用例基本信息
        // 2. 查询测试用例步骤列表
        Map<String, Object> detail = caseBizService.getCaseDetail(id);
        
        // 方法中间：检查查询结果
        if (detail == null) {
            // 用例不存在
            log.info("测试用例不存在，用例ID: {}", id);
            return Resp.info(404, "测试用例不存在");
        }
        
        // 方法结束：返回查询结果
        log.info("查询测试用例详情成功，用例ID: {}", id);
        return Resp.ok(detail);
    }

    /**
     * 查询所有测试用例
     * 
     * 功能说明：
     * 1. 查询数据库中的所有测试用例
     * 2. 返回用例列表（不包含步骤）
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求
     * - 数据查询：调用服务层查询所有测试用例
     * - 响应构建：返回用例列表
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 caseBizService.listCases() 获取所有测试用例
     *   该方法会执行：SELECT * FROM cases
     * 
     * 数据库调用：
     * - 查询所有用例：SELECT * FROM cases
     * 
     * @return List<Cases> 测试用例列表
     */
    @GetMapping("list")
    public List<Cases> list() {
        // 方法开始：准备查询所有测试用例
        log.info("开始查询所有测试用例");
        
        // 调用服务层获取所有测试用例
        List<Cases> cases = caseBizService.listCases();
        
        // 方法结束：返回查询结果
        log.info("查询所有测试用例完成，数量: {}", cases.size());
        return cases;
    }

    /**
     * 根据文件夹ID查询测试用例
     * 
     * 功能说明：
     * 1. 接收文件夹ID
     * 2. 递归查询该文件夹及其所有子文件夹中的测试用例
     * 3. 返回符合条件的测试用例列表
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析文件夹ID
     * - 参数提取：从请求体中提取文件夹ID
     * - 文件夹收集：递归收集该文件夹及其所有子文件夹的ID
     * - 用例查询：查询这些文件夹中的所有测试用例
     * - 响应构建：返回用例列表
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 caseBizService.listByFolderId(folderId) 按文件夹查询用例
     *   该方法内部会执行以下操作：
     *   1. 递归查询子文件夹：SELECT * FROM test_case_folder WHERE parent_id = ?
     *   2. 构建文件夹ID列表（包含所有子文件夹）
     *   3. 查询用例：SELECT * FROM cases WHERE folder_id IN (...)
     * 
     * 数据库调用：
     * - 查询子文件夹：SELECT * FROM test_case_folder WHERE parent_id = ?
     * - 查询用例：SELECT * FROM cases WHERE folder_id IN (folderId1, folderId2, ...)
     * 
     * @param params 请求参数，包含：
     *              - folderId: 文件夹ID（必填）
     * @return Resp 响应对象，包含测试用例列表
     *         - 成功时：code=0, data=List<Cases>
     *         - 失败时：code=400, message=错误描述
     */
    @PostMapping("listByFolderId")
    public Resp listByFolderId(@RequestBody Map<String, Object> params) {
        // 方法开始：准备按文件夹查询测试用例
        log.info("开始按文件夹查询测试用例，参数: {}", params);
        
        // 方法中间：提取并验证文件夹ID
        if (params != null && params.containsKey("folderId")) {
            Object folderIdObj = params.get("folderId");
            
            if (folderIdObj instanceof Integer) {
                // 整数类型，直接使用
                Integer folderId = (Integer) folderIdObj;
                log.info("文件夹ID（整数）: {}", folderId);
                
                // 调用服务层查询
                List<Cases> cases = caseBizService.listByFolderId(folderId);
                return Resp.ok(cases);
                
            } else if (folderIdObj instanceof String) {
                // 字符串类型，尝试转换为整数
                try {
                    Integer folderId = Integer.parseInt((String) folderIdObj);
                    log.info("文件夹ID（字符串转整数）: {}", folderId);
                    
                    // 调用服务层查询
                    List<Cases> cases = caseBizService.listByFolderId(folderId);
                    return Resp.ok(cases);
                } catch (NumberFormatException e) {
                    // 转换失败，记录日志
                    log.warn("文件夹ID格式错误: {}", folderIdObj);
                }
            }
        }
        
        // 方法结束：参数无效，返回空列表
        log.info("文件夹ID参数无效，返回空列表");
        return Resp.ok(new ArrayList<>());
    }

    /**
     * 分页查询测试用例
     * 
     * 功能说明：
     * 1. 接收分页参数（页码、每页大小）
     * 2. 执行分页查询
     * 3. 返回分页结果
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，解析分页参数
     * - 参数校验：验证页码和每页大小的有效性
     * - 分页查询：调用服务层执行分页查询
     * - 响应构建：返回分页结果
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 caseBizService.pageCases(pageNum, pageSize) 执行分页查询
     *   该方法会执行：
     *   1. 查询分页数据：SELECT * FROM cases LIMIT pageSize OFFSET (pageNum-1)*pageSize
     *   2. 查询总数：SELECT COUNT(*) FROM cases
     *   3. 组装分页结果
     * 
     * 数据库调用：
     * - 分页查询：SELECT * FROM cases LIMIT ? OFFSET ?
     * - 总数查询：SELECT COUNT(*) FROM cases
     * 
     * @param pageNum 页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @return PageResp 分页响应对象
     */
    @GetMapping("page")
    public PageResp page(@RequestParam(defaultValue = "1") Integer pageNum, 
                         @RequestParam(defaultValue = "10") Integer pageSize) {
        // 方法开始：准备分页查询
        log.info("开始分页查询测试用例，页码: {}，每页大小: {}", pageNum, pageSize);
        
        // 调用服务层执行分页查询
        PageResp result = caseBizService.pageCases(pageNum, pageSize);
        
        // 方法结束：返回分页结果
        log.info("分页查询完成，总记录数: {}", result.getTotalRow());
        return result;
    }

    /**
     * 分页查询测试用例（带条件）
     * 
     * 功能说明：
     * 1. 接收分页参数和查询条件
     * 2. 根据条件执行分页查询
     * 3. 返回分页结果
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析分页参数和查询条件
     * - 参数校验：验证参数的有效性
     * - 条件构建：根据请求参数构建查询条件
     * - 分页查询：调用服务层执行条件分页查询
     * - 响应构建：返回分页结果
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 caseBizService.pageCases(pageNum, pageSize, params) 执行条件分页查询
     *   该方法会执行：
     *   1. 构建查询条件（WHERE子句）
     *   2. 查询分页数据
     *   3. 查询总数
     *   4. 组装分页结果
     * 
     * 数据库调用：
     * - 条件分页查询：SELECT * FROM cases WHERE ... LIMIT ? OFFSET ?
     * - 条件总数查询：SELECT COUNT(*) FROM cases WHERE ...
     * 
     * @param pageNum 页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @param params 查询条件，可包含：
     *              - folderId: 文件夹ID
     *              - module: 模块名称
     *              - priority: 优先级
     *              - status: 状态
     *              - keyword: 关键字（模糊查询标题）
     * @return PageResp 分页响应对象
     */
    @PostMapping("page")
    public PageResp page(@RequestParam(defaultValue = "1") Integer pageNum, 
                         @RequestParam(defaultValue = "10") Integer pageSize, 
                         @RequestBody(required = false) Map<String, Object> params) {
        // 方法开始：准备条件分页查询
        log.info("开始条件分页查询测试用例，页码: {}，每页大小: {}，条件: {}", pageNum, pageSize, params);
        
        // 调用服务层执行条件分页查询
        PageResp result = caseBizService.pageCases(pageNum, pageSize, params);
        
        // 方法结束：返回分页结果
        log.info("条件分页查询完成，总记录数: {}", result.getTotalRow());
        return result;
    }

    /**
     * 获取测试用例详情（包含步骤）
     * 
     * 功能说明：
     * 1. 接收测试用例ID
     * 2. 查询测试用例的基本信息
     * 3. 查询测试用例的所有步骤
     * 4. 返回完整的用例详情
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，路径中包含用例ID
     * - 参数提取：从路径变量中提取用例ID
     * - 用例查询：查询测试用例的基本信息
     * - 步骤查询：查询测试用例的所有步骤
     * - 数据组装：将用例信息和步骤信息组装返回
     * - 响应构建：返回用例详情
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 caseBizService.getCaseDetail(id) 获取用例详情
     *   该方法内部会执行以下操作：
     *   1. 查询用例信息：SELECT * FROM cases WHERE id = ?
     *   2. 查询步骤信息：SELECT * FROM test_case_steps WHERE test_case_id = ?
     *   3. 组装返回数据
     * 
     * 数据库调用：
     * - 查询用例：SELECT * FROM cases WHERE id = ?
     * - 查询步骤：SELECT * FROM test_case_steps WHERE test_case_id = ? ORDER BY step_order
     * 
     * @param id 测试用例主键ID，从URL路径中获取
     * @return Resp 响应对象，包含用例详情
     *         - 成功时：code=0, data={case: 用例信息, steps: 步骤列表}
     *         - 失败时：code=404, message="测试用例不存在"
     */
    @GetMapping("detail/{id}")
    public Resp detail(@PathVariable Integer id) {
        // 方法开始：准备查询测试用例详情
        log.info("开始查询测试用例详情（含步骤），用例ID: {}", id);
        
        // 调用服务层获取用例详情
        Map<String, Object> detail = caseBizService.getCaseDetail(id);
        
        // 方法中间：检查查询结果
        if (detail == null) {
            // 用例不存在
            log.info("测试用例不存在，用例ID: {}", id);
            return Resp.info(404, "测试用例不存在");
        }
        
        // 方法结束：返回查询结果
        log.info("查询测试用例详情成功，用例ID: {}", id);
        return Resp.ok(detail);
    }

    /**
     * 批量删除测试用例
     * 
     * 功能说明：
     * 1. 接收测试用例ID列表
     * 2. 批量删除这些测试用例的步骤
     * 3. 批量删除这些测试用例
     * 4. 批量从向量数据库中删除相关数据
     * 
     * 业务流程：
     * - 开始：接收HTTP DELETE请求，解析用例ID列表
     * - 参数校验：验证ID列表的有效性
     * - 步骤删除：批量删除所有测试用例的步骤
     * - 用例删除：批量删除测试用例
     * - 向量删除：批量从向量数据库删除相关数据
     * - 响应构建：返回删除结果
     * - 结束：完成批量删除流程
     * 
     * 调用服务：
     * - 调用 caseBizService.batchRemoveCases(ids) 批量删除测试用例
     *   该方法内部会执行以下操作：
     *   1. 遍历ID列表，删除每个用例的步骤
     *   2. 批量删除测试用例：DELETE FROM cases WHERE id IN (...)
     *   3. 异步从向量数据库删除相关数据
     * 
     * 调用外部服务：
     * - 异步调用 QdrantService.deletePoint(collectionName, pointId)
     *   该服务会批量删除向量数据库中的向量数据
     * 
     * 数据库调用：
     * - 删除测试步骤：DELETE FROM test_case_steps WHERE test_case_id IN (...)
     * - 删除测试用例：DELETE FROM cases WHERE id IN (...)
     * - 更新同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     * 
     * @param ids 测试用例ID列表
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=400, message=错误描述
     */
    @DeleteMapping("batchRemove")
    public Resp batchRemove(@RequestBody List<Integer> ids) {
        // 方法开始：准备批量删除测试用例
        log.info("开始批量删除测试用例，数量: {}", ids.size());
        
        // 调用服务层批量删除测试用例
        // 该调用会：
        // 1. 批量删除测试用例的所有步骤
        // 2. 批量删除测试用例
        // 3. 从向量数据库批量删除相关数据
        Resp result = caseBizService.batchRemoveCases(ids);
        
        // 方法结束：返回删除结果
        log.info("批量删除测试用例完成，结果: {}", result.getCode() == 0 ? "成功" : "失败");
        return result;
    }

    /**
     * 获取测试用例统计信息
     * 
     * 功能说明：
     * 1. 统计测试用例的总数
     * 2. 按状态统计测试用例数量
     * 3. 按优先级统计测试用例数量
     * 4. 返回统计结果
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求
     * - 总数统计：查询测试用例总数
     * - 状态统计：按状态分组统计
     * - 优先级统计：按优先级分组统计
     * - 响应构建：组装统计结果
     * - 结束：返回统计信息
     * 
     * 调用服务：
     * - 调用 caseBizService.getCaseStats() 获取统计信息
     *   该方法会执行以下操作：
     *   1. 查询总数：SELECT COUNT(*) FROM cases
     *   2. 按状态统计：SELECT status, COUNT(*) FROM cases GROUP BY status
     *   3. 按优先级统计：SELECT priority, COUNT(*) FROM cases GROUP BY priority
     *   4. 组装统计结果
     * 
     * 数据库调用：
     * - 查询总数：SELECT COUNT(*) FROM cases
     * - 按状态统计：SELECT status, COUNT(*) FROM cases GROUP BY status
     * - 按优先级统计：SELECT priority, COUNT(*) FROM cases GROUP BY priority
     * 
     * @return Resp 响应对象，包含统计信息
     *         - 成功时：code=0, data={
     *           total: 总数,
     *           status: {pending: 待执行数, completed: 已完成数, failed: 失败数},
     *           priority: {low: 低优先级数, medium: 中优先级数, high: 高优先级数}
     *         }
     */
    @GetMapping("stats")
    public Resp stats() {
        // 方法开始：准备获取统计信息
        log.info("开始获取测试用例统计信息");
        
        // 调用服务层获取统计信息
        Map<String, Object> stats = caseBizService.getCaseStats();
        
        // 方法结束：返回统计结果
        log.info("获取测试用例统计信息完成");
        return Resp.ok(stats);
    }

    /**
     * 回滚测试用例到指定版本
     * 
     * 功能说明：
     * 1. 接收测试用例ID和版本ID
     * 2. 保存当前版本为历史记录
     * 3. 从历史版本中恢复测试用例信息
     * 4. 从历史版本中恢复测试用例步骤
     * 5. 异步同步到向量数据库
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析用例ID和版本ID
     * - 参数校验：验证用例ID和版本ID的有效性
     * - 当前保存：保存当前版本为历史记录
     * - 版本恢复：从指定版本恢复用例信息
     * - 步骤恢复：从指定版本恢复用例步骤
     * - 向量同步：异步触发向量数据库同步
     * - 响应构建：返回回滚结果
     * - 结束：完成版本回滚流程
     * 
     * 调用服务：
     * - 调用 caseBizService.rollbackToVersion(caseId, versionId) 执行版本回滚
     *   该方法内部会执行以下操作：
     *   1. 查询版本信息：SELECT * FROM case_versions WHERE id = ?
     *   2. 保存当前版本：将当前用例信息保存到case_versions表
     *   3. 恢复用例信息：UPDATE cases SET ... WHERE id = ?
     *   4. 恢复用例步骤：DELETE并重新插入步骤
     *   5. 异步同步到向量数据库
     * 
     * 调用外部服务：
     * - 异步调用 QdrantSyncBizService.processSingleTestCase(testCaseId)
     *   该服务会更新向量数据库中的测试用例向量数据
     * 
     * 数据库调用：
     * - 查询版本：SELECT * FROM case_versions WHERE id = ?
     * - 保存当前版本：INSERT INTO case_versions (...) VALUES (...)
     * - 更新用例：UPDATE cases SET ... WHERE id = ?
     * - 删除旧步骤：DELETE FROM test_case_steps WHERE test_case_id = ?
     * - 插入新步骤：INSERT INTO test_case_steps (...) VALUES (...)
     * - 更新同步记录：INSERT INTO case_vector_sync (...) VALUES (...)
     * 
     * @param params 请求参数，包含：
     *              - caseId: 测试用例ID（必填）
     *              - versionId: 版本ID（必填）
     * @return Resp 响应对象，包含操作结果
     *         - 成功时：code=0, data=true
     *         - 失败时：code=400, message=错误描述
     */
    @PostMapping("rollback")
    public Resp rollback(@RequestBody Map<String, Integer> params) {
        // 方法开始：准备回滚测试用例
        Integer caseId = params.get("caseId");
        Integer versionId = params.get("versionId");
        log.info("开始回滚测试用例到指定版本，用例ID: {}，版本ID: {}", caseId, versionId);
        
        // 方法中间：验证参数
        if (params != null && params.containsKey("caseId") && params.containsKey("versionId")) {
            // 调用服务层执行版本回滚
            // 该调用会：
            // 1. 保存当前版本为历史记录
            // 2. 从指定版本恢复用例信息和步骤
            // 3. 异步同步到向量数据库
            Resp result = caseBizService.rollbackToVersion(params.get("caseId"), params.get("versionId"));
            
            // 方法结束：返回回滚结果
            log.info("回滚测试用例完成，结果: {}", result.getCode() == 0 ? "成功" : "失败");
            return result;
        }
        
        // 参数无效
        log.error("回滚参数无效，必须包含caseId和versionId");
        return Resp.info(400, "请求参数不能为空，且必须包含caseId和versionId");
    }
}
