package com.grape.grape.service.biz;

import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.model.vo.CaseRequest;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;
import java.util.Map;

public interface CaseBizService {
    Resp updateCase(Cases cases);
    Resp updateCaseWithSteps(CaseRequest caseRequest);
    Resp rollbackToVersion(Integer caseId, Integer versionId);
    Resp saveCase(CaseRequest caseRequest);
    Resp removeCase(Integer id);
    Cases getCaseById(Integer id);
    List<Cases> listCases();
    List<Cases> listByFolderId(Integer folderId);
    PageResp pageCases(Integer pageNum, Integer pageSize);
    PageResp pageCases(Integer pageNum, Integer pageSize, Map<String, Object> params);
    Map<String, Object> getCaseDetail(Integer id);
    Resp batchRemoveCases(List<Integer> ids);
    Map<String, Object> getCaseStats();
    Resp saveCaseSteps(Integer caseId, List<TestCaseStep> steps);
    Resp batchUpdateCases(List<CaseRequest> caseRequests);
}
