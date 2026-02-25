package com.grape.grape.service.impl;

import com.grape.grape.entity.CaseVectorSync;
import com.grape.grape.mapper.CaseVectorSyncMapper;
import com.grape.grape.service.CaseVectorSyncService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 测试用例向量同步服务实现
 */
@Service
public class CaseVectorSyncServiceImpl extends ServiceImpl<CaseVectorSyncMapper, CaseVectorSync> implements CaseVectorSyncService {

    @Override
    public CaseVectorSync getByTestCaseId(Integer testCaseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("test_case_id", testCaseId);
        return getMapper().selectOneByQuery(queryWrapper);
    }

    @Override
    public boolean saveOrUpdate(CaseVectorSync syncRecord) {
        CaseVectorSync existingRecord = getByTestCaseId(syncRecord.getTestCaseId());
        if (existingRecord != null) {
            syncRecord.setId(existingRecord.getId());
            return updateById(syncRecord);
        } else {
            return save(syncRecord);
        }
    }
}
