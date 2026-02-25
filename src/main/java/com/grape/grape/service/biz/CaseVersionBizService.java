package com.grape.grape.service.biz;

import com.grape.grape.model.Resp;
import com.mybatisflex.core.paginate.Page;
import com.grape.grape.entity.CaseVersions;

public interface CaseVersionBizService {
    Resp getListByCaseId(int caseId);
    
    Resp getListByCaseId(int caseId, Page<CaseVersions> page);
    
    Resp pageByCaseId(int caseId, Page<CaseVersions> page);
}
