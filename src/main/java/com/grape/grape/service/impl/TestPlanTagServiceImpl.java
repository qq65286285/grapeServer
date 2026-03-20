package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanTag;
import com.grape.grape.mapper.TestPlanTagMapper;
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
 * 测试计划标签表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanTagServiceImpl extends ServiceImpl<TestPlanTagMapper, TestPlanTag> implements TestPlanTagService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanTagServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanTag> listByTagCategory(Integer tagCategory) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("tag_category = ? and is_deleted = 0", tagCategory)
                .orderBy("sort_order asc, use_count desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTag> listByStatus(Integer status) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("status = ? and is_deleted = 0", status)
                .orderBy("sort_order asc, use_count desc");
        return list(queryWrapper);
    }

    @Override
    public TestPlanTag getByTagName(String tagName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("tag_name = ? and is_deleted = 0", tagName);
        return getOne(queryWrapper);
    }

    @Override
    public Page<TestPlanTag> page(Page<TestPlanTag> page, Integer tagCategory, Integer status, String keyword) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (tagCategory != null) {
            queryWrapper.and("tag_category = ?", tagCategory);
        }

        if (status != null) {
            queryWrapper.and("status = ?", status);
        }

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and("(tag_name like ? or tag_code like ?)", "%" + keyword + "%", "%" + keyword + "%");
        }

        queryWrapper.orderBy("sort_order asc, use_count desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean increaseUseCount(Long id) {
        TestPlanTag tag = getById(id);
        if (tag != null) {
            tag.setUseCount(tag.getUseCount() + 1);
            tag.setUpdatedAt(System.currentTimeMillis());
            return updateById(tag);
        }
        return false;
    }

    @Override
    public boolean decreaseUseCount(Long id) {
        TestPlanTag tag = getById(id);
        if (tag != null && tag.getUseCount() > 0) {
            tag.setUseCount(tag.getUseCount() - 1);
            tag.setUpdatedAt(System.currentTimeMillis());
            return updateById(tag);
        }
        return false;
    }

    @Override
    public boolean enableTag(Long id) {
        TestPlanTag tag = getById(id);
        if (tag != null) {
            tag.setStatus(1); // 1-启用
            tag.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    tag.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(tag);
        }
        return false;
    }

    @Override
    public boolean disableTag(Long id) {
        TestPlanTag tag = getById(id);
        if (tag != null) {
            tag.setStatus(2); // 2-禁用
            tag.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    tag.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(tag);
        }
        return false;
    }

    @Override
    public List<TestPlanTag> getHotTags(int limit) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0 and status = 1")
                .orderBy("use_count desc")
                .limit(limit);
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanTag> listAllEnabled() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0 and status = 1")
                .orderBy("sort_order asc, use_count desc");
        return list(queryWrapper);
    }

    @Override
    public boolean save(TestPlanTag testPlanTag) {
        // 设置默认值
        if (testPlanTag.getTagColor() == null) {
            testPlanTag.setTagColor("#1890ff");
        }
        if (testPlanTag.getTagCategory() == null) {
            testPlanTag.setTagCategory(1);
        }
        if (testPlanTag.getUseCount() == null) {
            testPlanTag.setUseCount(0);
        }
        if (testPlanTag.getSortOrder() == null) {
            testPlanTag.setSortOrder(0);
        }
        if (testPlanTag.getStatus() == null) {
            testPlanTag.setStatus(1); // 1-启用
        }
        if (testPlanTag.getIsDeleted() == null) {
            testPlanTag.setIsDeleted(0);
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanTag.getCreatedAt() == null) {
            testPlanTag.setCreatedAt(now);
        }
        if (testPlanTag.getUpdatedAt() == null) {
            testPlanTag.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanTag.getCreatedBy() == null) {
                    testPlanTag.setCreatedBy(userId);
                }
                testPlanTag.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.save(testPlanTag);
    }

    @Override
    public boolean updateById(TestPlanTag testPlanTag) {
        // 设置更新时间
        testPlanTag.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                testPlanTag.setUpdatedBy(Long.parseLong(userIdStr));
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanTag);
    }

    public boolean removeById(Long id) {
        TestPlanTag tag = getById(id);
        if (tag != null) {
            tag.setIsDeleted(1);
            tag.setUpdatedAt(System.currentTimeMillis());
            
            // 设置更新人
            String userIdStr = UserUtils.getCurrentLoginUserId(userService);
            if (userIdStr != null) {
                try {
                    tag.setUpdatedBy(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdStr);
                }
            }

            return updateById(tag);
        }
        return false;
    }
}