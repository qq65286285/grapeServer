package com.grape.grape.service.biz;


import com.grape.grape.model.Resp;
import com.grape.grape.service.CaseVersionsService;
import com.mybatisflex.core.paginate.Page;
import com.grape.grape.entity.CaseVersions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CaseVersionBizServiceImpl implements CaseVersionBizService {


    @Resource
    CaseVersionsService caseVersionsService;

    @Override
    public Resp getListByCaseId(int caseId){
        return Resp.ok(caseVersionsService.listByCaseId(caseId));
    }

    @Override
    public Resp getListByCaseId(int caseId, Page<CaseVersions> page){
        return Resp.ok(caseVersionsService.pageByCaseId(caseId, page));
    }

    @Override
    public Resp pageByCaseId(int caseId, Page<CaseVersions> page){
        return Resp.ok(caseVersionsService.pageByCaseId(caseId, page));
    }
}
