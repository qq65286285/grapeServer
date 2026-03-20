package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestProject;
import com.grape.grape.mapper.TestProjectMapper;
import com.grape.grape.service.TestProjectService;
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
 * 项目表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestProjectServiceImpl extends ServiceImpl<TestProjectMapper, TestProject> implements TestProjectService {

    private static final Logger log = LoggerFactory.getLogger(TestProjectServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestProject> listByStatus(Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("status = ? and is_deleted = 0", status)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestProject> listByOwnerId(Long ownerId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("owner_id = ? and is_deleted = 0", ownerId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public TestProject getByProjectCode(String projectCode) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("project_code = ? and is_deleted = 0", projectCode);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestProject> page(Page<TestProject> page, Integer status, Long ownerId, String projectName, String projectCode) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (status != null) {
            queryWrapper.and("status = ?", status);
        }

        if (ownerId != null) {
            queryWrapper.and("owner_id = ?", ownerId);
        }

        if (projectName != null && !projectName.isEmpty()) {
            queryWrapper.and("project_name like ?", "%" + projectName + "%");
        }

        if (projectCode != null && !projectCode.isEmpty()) {
            queryWrapper.and("project_code like ?", "%" + projectCode + "%");
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public int batchDelete(List<Long> ids) {
        int successCount = 0;
        for (Long id : ids) {
            TestProject project = getById(id);
            if (project != null) {
                project.setIsDeleted(1);
                if (updateById(project)) {
                    successCount++;
                }
            }
        }
        return successCount;
    }

    @Override
    public boolean restore(Long id) {
        TestProject project = getById(id);
        if (project != null && project.getIsDeleted() == 1) {
            project.setIsDeleted(0);
            return updateById(project);
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        TestProject project = getById(id);
        if (project != null && project.getIsDeleted() == 0) {
            project.setStatus(status);
            return updateById(project);
        }
        return false;
    }

    @Override
    public boolean save(TestProject project) {
        // 设置创建人和创建时间
        if (project.getCreatedBy() == null) {
            project.setCreatedBy(getCurrentUserId());
        }
        if (project.getCreatedAt() == null) {
            project.setCreatedAt(new Date());
        }

        // 设置默认值
        if (project.getStatus() == null) {
            project.setStatus(1); // 默认状态为进行中
        }
        if (project.getIsDeleted() == null) {
            project.setIsDeleted(0);
        }

        return super.save(project);
    }

    @Override
    public boolean updateById(TestProject project) {
        // 设置更新时间
        project.setUpdatedAt(new Date());
        return super.updateById(project);
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID
     */
    private String getCurrentUserId() {
        return UserUtils.getCurrentLoginUserId(userService);
    }
}