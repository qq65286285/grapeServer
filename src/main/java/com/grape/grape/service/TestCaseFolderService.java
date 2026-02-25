package com.grape.grape.service;

import com.grape.grape.entity.TestCaseFolder;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 测试用例文件夹服务接口
 */
public interface TestCaseFolderService extends IService<TestCaseFolder> {

    /**
     * 根据父文件夹ID获取子文件夹列表
     * @param parentId 父文件夹ID
     * @return 子文件夹列表
     */
    List<TestCaseFolder> getByParentId(Integer parentId);

    /**
     * 构建文件夹树状结构
     * @return 树状结构数据
     */
    List<TestCaseFolder> buildFolderTree();

    /**
     * 计算文件夹路径
     * @param parentId 父文件夹ID
     * @return 文件夹路径
     */
    String calculatePath(Integer parentId);
}
