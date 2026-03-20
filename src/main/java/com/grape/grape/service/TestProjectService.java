package com.grape.grape.service;

import com.grape.grape.entity.TestProject;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 项目表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestProjectService extends MyBaseService<TestProject> {

    /**
     * 根据状态查询项目列表
     *
     * @param status 状态
     * @return 项目列表
     */
    List<TestProject> listByStatus(Integer status);

    /**
     * 根据负责人ID查询项目列表
     *
     * @param ownerId 负责人ID
     * @return 项目列表
     */
    List<TestProject> listByOwnerId(Long ownerId);

    /**
     * 根据项目编码查询项目
     *
     * @param projectCode 项目编码
     * @return 项目
     */
    TestProject getByProjectCode(String projectCode);

    /**
     * 分页查询项目
     *
     * @param page 分页参数
     * @param status 状态（可选）
     * @param ownerId 负责人ID（可选）
     * @param projectName 项目名称（可选，模糊查询）
     * @param projectCode 项目编码（可选，模糊查询）
     * @return 分页结果
     */
    Page<TestProject> page(Page<TestProject> page, Integer status, Long ownerId, String projectName, String projectCode);

    /**
     * 批量删除项目（软删除）
     *
     * @param ids 项目ID列表
     * @return 删除成功的数量
     */
    int batchDelete(List<Long> ids);

    /**
     * 恢复已删除的项目
     *
     * @param id 项目ID
     * @return 是否恢复成功
     */
    boolean restore(Long id);

    /**
     * 更新项目状态
     *
     * @param id 项目ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
}