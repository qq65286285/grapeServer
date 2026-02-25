package com.grape.grape.service.impl;

import com.grape.grape.entity.TestCaseFolder;
import com.grape.grape.mapper.TestCaseFolderMapper;
import com.grape.grape.service.TestCaseFolderService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用例文件夹服务实现类
 */
@Service
public class TestCaseFolderServiceImpl extends ServiceImpl<TestCaseFolderMapper, TestCaseFolder> implements TestCaseFolderService {

    @Override
    public List<TestCaseFolder> getByParentId(Integer parentId) {
        QueryWrapper qw = new QueryWrapper();
        qw.and("parent_id = ?", parentId);
        qw.orderBy("sort ASC, id ASC");
        return list(qw);
    }

    @Override
    public List<TestCaseFolder> buildFolderTree() {
        // 获取所有文件夹
        List<TestCaseFolder> allFolders = list();
        
        // 构建文件夹映射
        Map<Integer, TestCaseFolder> folderMap = new HashMap<>();
        for (TestCaseFolder folder : allFolders) {
            folderMap.put(folder.getId(), folder);
        }
        
        // 构建树状结构
        List<TestCaseFolder> rootFolders = new ArrayList<>();
        for (TestCaseFolder folder : allFolders) {
            if (folder.getParentId() == 0) {
                rootFolders.add(folder);
            }
        }
        
        return rootFolders;
    }

    @Override
    public String calculatePath(Integer parentId) {
        if (parentId == 0) {
            return "0";
        }
        
        TestCaseFolder parentFolder = getById(parentId);
        if (parentFolder == null) {
            return "0";
        }
        
        String parentPath = parentFolder.getPath();
        if (parentPath.equals("0")) {
            return String.valueOf(parentId);
        } else {
            return parentPath + "/" + parentId;
        }
    }
}
