package com.grape.grape.service.impl;

import com.grape.grape.entity.table.CaseVersionsTableDef;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.CaseVersions;
import com.grape.grape.mapper.CaseVersionsMapper;
import com.grape.grape.service.CaseVersionsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试用例版本备份表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class CaseVersionsServiceImpl extends ServiceImpl<CaseVersionsMapper, CaseVersions>  implements CaseVersionsService{

    @Override
    public List<CaseVersions> listByCaseId(int caseId) {
        QueryWrapper qw = new QueryWrapper();
        qw.and(CaseVersionsTableDef.CASE_VERSIONS.TEST_CASE_ID.eq(caseId));
        qw.orderBy(CaseVersionsTableDef.CASE_VERSIONS.CREATED_AT, false);
        return list(qw);
    }

    @Override
    public Page<CaseVersions> pageByCaseId(int caseId, Page<CaseVersions> page) {
        QueryWrapper qw = new QueryWrapper();
        qw.and(CaseVersionsTableDef.CASE_VERSIONS.TEST_CASE_ID.eq(caseId));
        qw.orderBy(CaseVersionsTableDef.CASE_VERSIONS.CREATED_AT, false);
        return page(page, qw);
    }
}
