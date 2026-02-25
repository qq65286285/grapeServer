package com.grape.grape.service;

import com.grape.grape.entity.CaseVectorSync;

/**
 * 测试用例向量同步服务
 */
public interface CaseVectorSyncService extends MyBaseService<CaseVectorSync> {

    /**
     * 根据测试用例ID获取同步记录
     * @param testCaseId 测试用例ID
     * @return 同步记录
     */
    CaseVectorSync getByTestCaseId(Integer testCaseId);

    /**
     * 创建或更新同步记录
     * @param syncRecord 同步记录
     * @return 是否成功
     */
    boolean saveOrUpdate(CaseVectorSync syncRecord);
}
