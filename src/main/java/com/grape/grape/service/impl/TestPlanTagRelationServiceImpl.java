package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanTagRelation;
import com.grape.grape.mapper.TestPlanTagRelationMapper;
import com.grape.grape.service.TestPlanTagRelationService;
import com.grape.grape.service.TestPlanTagService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划标签关联表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanTagRelationServiceImpl extends ServiceImpl<TestPlanTagRelationMapper, TestPlanTagRelation> implements TestPlanTagRelationService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanTagRelationServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TestPlanTagService testPlanTagService;

    @Override
    public List<TestPlanTagRelation> listByTagId(Long tagId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("tag_id = ?", tagId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTagRelation> listByRelation(Integer relationType, Long relationId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("relation_type = ? and relation_id = ?", relationType, relationId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTagRelation> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ?", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public boolean existsByTagAndRelation(Long tagId, Integer relationType, Long relationId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("tag_id = ? and relation_type = ? and relation_id = ?", tagId, relationType, relationId);
        return exists(queryWrapper);
    }

    @Override
    public Page<TestPlanTagRelation> page(Page<TestPlanTagRelation> page, Long tagId, Integer relationType, Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (tagId != null) {
            queryWrapper.where("tag_id = ?", tagId);
        }

        if (relationType != null) {
            queryWrapper.and("relation_type = ?", relationType);
        }

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public int batchAddRelations(List<Long> tagIds, Integer relationType, Long relationId, Long planId) {
        int successCount = 0;
        for (Long tagId : tagIds) {
            // 检查是否已存在关联
            if (!existsByTagAndRelation(tagId, relationType, relationId)) {
                TestPlanTagRelation relation = new TestPlanTagRelation();
                relation.setTagId(tagId);
                relation.setRelationType(relationType);
                relation.setRelationId(relationId);
                relation.setPlanId(planId);
                
                // 设置创建人和创建时间
                long now = System.currentTimeMillis();
                relation.setCreatedAt(now);
                
                String userIdStr = UserUtils.getCurrentLoginUserId(userService);
                if (userIdStr != null) {
                    try {
                        relation.setCreatedBy(Long.parseLong(userIdStr));
                    } catch (NumberFormatException e) {
                        log.warn("无法解析用户ID: {}", userIdStr);
                    }
                }
                
                if (save(relation)) {
                    successCount++;
                    // 增加标签使用次数
                    testPlanTagService.increaseUseCount(tagId);
                }
            }
        }
        return successCount;
    }

    @Override
    public int batchDeleteRelations(Integer relationType, Long relationId) {
        List<TestPlanTagRelation> relations = listByRelation(relationType, relationId);
        int successCount = 0;
        
        for (TestPlanTagRelation relation : relations) {
            if (removeById(relation.getId())) {
                successCount++;
                // 减少标签使用次数
                testPlanTagService.decreaseUseCount(relation.getTagId());
            }
        }
        
        return successCount;
    }

    @Override
    public boolean deleteRelation(Long tagId, Integer relationType, Long relationId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("tag_id = ? and relation_type = ? and relation_id = ?", tagId, relationType, relationId);
        List<TestPlanTagRelation> relations = list(queryWrapper);
        
        if (!relations.isEmpty()) {
            TestPlanTagRelation relation = relations.get(0);
            boolean deleted = removeById(relation.getId());
            if (deleted) {
                // 减少标签使用次数
                testPlanTagService.decreaseUseCount(tagId);
            }
            return deleted;
        }
        return false;
    }

    @Override
    public int deleteByTagId(Long tagId) {
        List<TestPlanTagRelation> relations = listByTagId(tagId);
        int successCount = 0;
        
        for (TestPlanTagRelation relation : relations) {
            if (removeById(relation.getId())) {
                successCount++;
                // 减少标签使用次数
                testPlanTagService.decreaseUseCount(tagId);
            }
        }
        
        return successCount;
    }

    @Override
    public int deleteByPlanId(Long planId) {
        List<TestPlanTagRelation> relations = listByPlanId(planId);
        int successCount = 0;
        
        for (TestPlanTagRelation relation : relations) {
            if (removeById(relation.getId())) {
                successCount++;
                // 减少标签使用次数
                testPlanTagService.decreaseUseCount(relation.getTagId());
            }
        }
        
        return successCount;
    }

    @Override
    public boolean save(TestPlanTagRelation testPlanTagRelation) {
        // 设置创建时间
        if (testPlanTagRelation.getCreatedAt() == null) {
            testPlanTagRelation.setCreatedAt(System.currentTimeMillis());
        }

        // 设置创建人
        if (testPlanTagRelation.getCreatedBy() == null) {
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    testPlanTagRelation.setCreatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }
        }

        boolean saved = super.save(testPlanTagRelation);
        if (saved) {
            // 增加标签使用次数
            testPlanTagService.increaseUseCount(testPlanTagRelation.getTagId());
        }
        return saved;
    }

    public boolean removeById(Long id) {
        TestPlanTagRelation relation = getById(id);
        if (relation != null) {
            boolean deleted = super.removeById(id);
            if (deleted) {
                // 减少标签使用次数
                testPlanTagService.decreaseUseCount(relation.getTagId());
            }
            return deleted;
        }
        return false;
    }
}