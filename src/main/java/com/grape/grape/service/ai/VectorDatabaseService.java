package com.grape.grape.service.ai;

import com.grape.grape.model.prompt.ai.TestCase;
import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;

import cn.hutool.json.JSONUtil;

import com.grape.grape.model.prompt.ai.ReferenceType;
import io.milvus.client.MilvusClient;
import io.milvus.param.R;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.param.MetricType;
import io.milvus.grpc.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VectorDatabaseService {

    @Autowired
    private MilvusClient milvusClient;

    private static final String COLLECTION_NAME = "test_cases";

    public void storeData(List<TestCase> testCases) {
        try {
            if (testCases == null || testCases.isEmpty()) {
                System.out.println("No test cases to store");
                return;
            }

            // 准备插入数据
            List<InsertParam.Field> fields = new ArrayList<>();
            
            List<String> caseIds = new ArrayList<>();
            List<String> caseNames = new ArrayList<>();
            List<String> fullTexts = new ArrayList<>();
            List<String> preconditions = new ArrayList<>();
            List<String> testSteps = new ArrayList<>();
            List<String> expectedResults = new ArrayList<>();
            List<String> priorities = new ArrayList<>();
            List<String> iterations = new ArrayList<>();
            List<String> isSmokeTests = new ArrayList<>();
            List<String> automationFlags = new ArrayList<>();
            List<String> screenshots = new ArrayList<>();
            List<String> remarks = new ArrayList<>();
            List<List<Float>> embeddings = new ArrayList<>();
            List<Long> createdAts = new ArrayList<>();

            for (TestCase testCase : testCases) {
                // 生成用例编号
                testCase.generateCaseId();
                
                // 添加数据到各个字段列表
                caseIds.add(testCase.getCaseId());
                caseNames.add(testCase.getCaseName());
                fullTexts.add(testCase.getFullText());
                preconditions.add(testCase.getPrecondition());
                testSteps.add(testCase.getTestSteps());
                expectedResults.add(testCase.getExpectedResult());
                priorities.add(testCase.getPriority());
                iterations.add(testCase.getIteration());
                isSmokeTests.add(testCase.getIsSmokeTest());
                automationFlags.add(testCase.getAutomationFlag());
                screenshots.add(testCase.getScreenshot());
                remarks.add(testCase.getRemark());
                
                // 转换向量格式
                if (testCase.getEmbedding() != null) {
                    List<Float> embeddingList = new ArrayList<>(testCase.getEmbedding().length);
                    for (float value : testCase.getEmbedding()) {
                        embeddingList.add(value);
                    }
                    embeddings.add(embeddingList);
                } else {
                    // 如果没有向量，添加一个空向量
                    embeddings.add(new ArrayList<>());
                }
                
                createdAts.add(testCase.getCreatedAt());
            }

            // 添加字段到插入参数
            fields.add(new InsertParam.Field("case_id", caseIds));
            fields.add(new InsertParam.Field("case_name", caseNames));
            fields.add(new InsertParam.Field("full_text", fullTexts));
            fields.add(new InsertParam.Field("precondition", preconditions));
            fields.add(new InsertParam.Field("test_steps", testSteps));
            fields.add(new InsertParam.Field("expected_result", expectedResults));
            fields.add(new InsertParam.Field("priority", priorities));
            fields.add(new InsertParam.Field("iteration", iterations));
            fields.add(new InsertParam.Field("is_smoke_test", isSmokeTests));
            fields.add(new InsertParam.Field("automation_flag", automationFlags));
            fields.add(new InsertParam.Field("screenshot", screenshots));
            fields.add(new InsertParam.Field("remark", remarks));
            fields.add(new InsertParam.Field("embedding", embeddings));
            fields.add(new InsertParam.Field("created_at", createdAts));

            // 创建插入参数
            InsertParam insertParam = InsertParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withFields(fields)
                    .build();

            // 执行插入操作
            R<?> insertResponse = milvusClient.insert(insertParam);
            if (insertResponse.getStatus() == R.Status.Success.getCode()) {
                System.out.println("Successfully stored " + testCases.size() + " test cases");
            } else {
                System.out.println("Failed to store test cases: " + insertResponse.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error storing test cases: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 批量存储方法，支持分批处理
    public void storeDataInBatches(List<TestCase> testCases, int batchSize) {
        try {
            if (testCases == null || testCases.isEmpty()) {
                System.out.println("No test cases to store");
                return;
            }

            int totalSize = testCases.size();
            int processed = 0;

            while (processed < totalSize) {
                int end = Math.min(processed + batchSize, totalSize);
                List<TestCase> batch = testCases.subList(processed, end);
                
                // System.out.println("Storing batch " + (processed / batchSize + 1) + ", " + batch.size() + " test cases");
                storeData(batch);
                
                processed = end;
            }

            // System.out.println("Successfully stored all " + totalSize + " test cases");
        } catch (Exception e) {
            System.out.println("Error storing test cases in batches: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 向量查询方法
     * @param queryVector 查询向量
     * @param topK 返回结果数量
     * @param metricType 距离度量类型
     * @return 查询结果列表
     */
    public List<TestCase> searchByVector(float[] queryVector, int topK, MetricType metricType) {
        List<TestCase> results = new ArrayList<>();
        try {
            // 准备查询参数
            List<List<Float>> vectors = new ArrayList<>();
            List<Float> vectorList = new ArrayList<>(queryVector.length);
            for (float value : queryVector) {
                vectorList.add(value);
            }
            vectors.add(vectorList);

            // 构建搜索参数
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withMetricType(metricType)
                    .withTopK(topK)
                    .withVectors(vectors)
                    .withVectorFieldName("embedding")
                    .build();

            // 执行搜索
            R<SearchResults> searchResponse = milvusClient.search(searchParam);
            if (searchResponse.getStatus() == R.Status.Success.getCode()) {
                SearchResults resultsObj = searchResponse.getData();
                // 这里简化处理，实际需要根据Milvus SDK的API进行正确的结果解析
                // 由于结果解析比较复杂，这里暂时返回空列表
                System.out.println("Successfully searched test cases");
            } else {
                System.out.println("Failed to search test cases: " + searchResponse.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error searching test cases: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * 向量查询方法（默认使用COSINE距离度量）
     * @param queryVector 查询向量
     * @param topK 返回结果数量
     * @return 查询结果列表
     */
    public List<TestCase> searchByVector(float[] queryVector, int topK) {
        return searchByVector(queryVector, topK, MetricType.COSINE);
    }

    /**
     * 批量向量查询方法
     * @param queryVectors 查询向量列表
     * @param topK 返回结果数量
     * @param metricType 距离度量类型
     * @return 查询结果列表
     */
    public List<TestCase> searchByVectors(List<float[]> queryVectors, int topK, MetricType metricType) {
        List<TestCase> results = new ArrayList<>();
        try {
            // 准备查询参数
            List<List<Float>> vectors = new ArrayList<>();
            for (float[] queryVector : queryVectors) {
                List<Float> vectorList = new ArrayList<>(queryVector.length);
                for (float value : queryVector) {
                    vectorList.add(value);
                }
                vectors.add(vectorList);
            }

            // 构建搜索参数
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withMetricType(metricType)
                    .withTopK(topK)
                    .withVectors(vectors)
                    .withOutFields(TestCase.getFieldNames())
                    .withVectorFieldName("embedding")
                    .build();

            // 执行搜索
            R<SearchResults> searchResponse = milvusClient.search(searchParam);
            if (searchResponse.getStatus() == R.Status.Success.getCode()) {
                // 解析搜索结果
                // SearchResults 包含了搜索的结果数据，包括每个查询的结果
                // 实际应用中需要根据Milvus SDK的API进行正确的结果解析
                // 这里尝试解析SearchResults对象，提取TestCase
                try {
                    // 获取搜索结果数据
                    SearchResultsWrapper resultData = new SearchResultsWrapper(searchResponse.getData().getResults());
                    List<QueryResultsWrapper.RowRecord> queryResults = resultData.getRowRecords(0);
                    // 获取每个查询的结果
                    for (QueryResultsWrapper.RowRecord record : queryResults) {
                        // 提取TestCase对象
                    //    System.out.println(record);
                        TestCase testCase = new TestCase().getByRecord(record);
                        results.add(testCase);
                    }
                    //  System.out.println(JSONUtil.toJsonStr(results.get(0)));
                    }
                 catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("searchByVectors result error: " + searchResponse.getMessage());
            }
        } catch (Exception e) {
            // 异常处理，返回一些模拟的TestCase对象
        }
        return results;
    }

    /**
     * 批量向量查询方法（默认使用COSINE距离度量）
     * @param queryVectors 查询向量列表
     * @param topK 返回结果数量
     * @return 查询结果列表
     */
    public List<TestCase> searchByVectors(List<float[]> queryVectors, int topK) {
        return searchByVectors(queryVectors, topK, MetricType.COSINE);
    }
    
    /**
     * 将TestCase列表添加到Map中，以case_id为key，去重处理
     * @param testCases TestCase列表
     * @param testCaseMap 用例ID到用例对象的映射
     * @return 添加且去重了的Map
     */
    public Map<String, TestCase> addTestCasesToMap(List<TestCase> testCases, Map<String, TestCase> testCaseMap) {
        if (testCases == null || testCases.isEmpty()) {
            return testCaseMap;
        }
        
        if (testCaseMap == null) {
            testCaseMap = new HashMap<>();
        }
        
        for (TestCase testCase : testCases) {
            if (testCase != null ) {
                String caseId = testCase.getCaseId();
                if (!testCaseMap.containsKey(caseId)) {
                    testCaseMap.put(caseId, testCase);
                }
                // 如果已存在，跳过（保留第一次出现）
            }
        }
        
        return testCaseMap;
    }
    
    /**
     * 从TestScenarioAnalysis和TestCase中提取文本，用于向量化
     * @param candidates 候选用例列表
     * @param analysis 测试场景分析
     * @param referenceTypes 需要提取的参考文本类型列表
     * @return 文本映射，key标识文本来源，value是需要向量化的文本
     */
    public Map<String, String> extractTextForEmbedding(List<TestCase> candidates, 
        TestScenarioAnalysis analysis, List<ReferenceType> referenceTypes) {
        // 使用LinkedHashMap保持插入顺序
        Map<String, String> textMap = new LinkedHashMap<>();
        
        // 1. 从analysis提取参考文本
        if (analysis != null) {
            // 提取product
            if (shouldExtract(referenceTypes, ReferenceType.PRODUCT) && analysis.getProduct() != null) {
                TestScenarioAnalysis.ProductInfo product = analysis.getProduct();
                if (product.getType() != null && !product.getType().isEmpty()) {
                    textMap.put("product:type", product.getType());
                }
                if (product.getName() != null && !product.getName().isEmpty()) {
                    textMap.put("product:name", product.getName());
                }
                if (product.getPlatforms() != null) {
                    for (int i = 0; i < product.getPlatforms().size(); i++) {
                        String platform = product.getPlatforms().get(i);
                        if (platform != null && !platform.isEmpty()) {
                            textMap.put("product:platform:" + (i + 1), platform);
                        }
                    }
                }
                if (product.getVersion() != null && !product.getVersion().isEmpty()) {
                    textMap.put("product:version", product.getVersion());
                }
            }
            
            // 提取function
            if (shouldExtract(referenceTypes, ReferenceType.FUNCTION) && analysis.getFunction() != null) {
                TestScenarioAnalysis.FunctionInfo function = analysis.getFunction();
                if (function.getLevel1() != null && !function.getLevel1().isEmpty()) {
                    textMap.put("function:level1", function.getLevel1());
                }
                if (function.getLevel2() != null && !function.getLevel2().isEmpty()) {
                    textMap.put("function:level2", function.getLevel2());
                }
                if (function.getLevel3() != null && !function.getLevel3().isEmpty()) {
                    textMap.put("function:level3", function.getLevel3());
                }
                if (function.getSubFunctions() != null) {
                    for (int i = 0; i < function.getSubFunctions().size(); i++) {
                        String subFunction = function.getSubFunctions().get(i);
                        if (subFunction != null && !subFunction.isEmpty()) {
                            textMap.put("function:subFunction:" + (i + 1), subFunction);
                        }
                    }
                }
            }
            
            // 提取test
            if (shouldExtract(referenceTypes, ReferenceType.TEST) && analysis.getTest() != null) {
                TestScenarioAnalysis.TestInfo test = analysis.getTest();
                if (test.getTypes() != null) {
                    for (int i = 0; i < test.getTypes().size(); i++) {
                        String type = test.getTypes().get(i);
                        if (type != null && !type.isEmpty()) {
                            textMap.put("test:type:" + (i + 1), type);
                        }
                    }
                }
                if (test.getPriorities() != null) {
                    for (int i = 0; i < test.getPriorities().size(); i++) {
                        String priority = test.getPriorities().get(i);
                        if (priority != null && !priority.isEmpty()) {
                            textMap.put("test:priority:" + (i + 1), priority);
                        }
                    }
                }
                if (test.getFocuses() != null) {
                    for (int i = 0; i < test.getFocuses().size(); i++) {
                        String focus = test.getFocuses().get(i);
                        if (focus != null && !focus.isEmpty()) {
                            textMap.put("test:focus:" + (i + 1), focus);
                        }
                    }
                }
                if (test.getTestPoints() != null) {
                    for (int i = 0; i < test.getTestPoints().size(); i++) {
                        String testPoint = test.getTestPoints().get(i);
                        if (testPoint != null && !testPoint.isEmpty()) {
                            textMap.put("test:testPoint:" + (i + 1), testPoint);
                        }
                    }
                }
            }
            
            // 提取entities
            if (shouldExtract(referenceTypes, ReferenceType.ENTITIES) && analysis.getEntities() != null) {
                for (TestScenarioAnalysis.Entity entity : analysis.getEntities()) {
                    if (entity != null && entity.getCategory() != null && entity.getValues() != null) {
                        for (int i = 0; i < entity.getValues().size(); i++) {
                            String value = entity.getValues().get(i);
                            if (value != null && !value.isEmpty()) {
                                textMap.put("entity:" + entity.getCategory() + ":" + (i + 1), value);
                            }
                        }
                    }
                }
            }
            
            // 提取technical
            if (shouldExtract(referenceTypes, ReferenceType.TECHNICAL) && analysis.getTechnical() != null) {
                TestScenarioAnalysis.TechnicalInfo technical = analysis.getTechnical();
                if (technical.getTerms() != null) {
                    for (int i = 0; i < technical.getTerms().size(); i++) {
                        String term = technical.getTerms().get(i);
                        if (term != null && !term.isEmpty()) {
                            textMap.put("technical:term:" + (i + 1), term);
                        }
                    }
                }
                if (technical.getProtocols() != null) {
                    for (int i = 0; i < technical.getProtocols().size(); i++) {
                        String protocol = technical.getProtocols().get(i);
                        if (protocol != null && !protocol.isEmpty()) {
                            textMap.put("technical:protocol:" + (i + 1), protocol);
                        }
                    }
                }
                if (technical.getDependencies() != null) {
                    for (int i = 0; i < technical.getDependencies().size(); i++) {
                        String dependency = technical.getDependencies().get(i);
                        if (dependency != null && !dependency.isEmpty()) {
                            textMap.put("technical:dependency:" + (i + 1), dependency);
                        }
                    }
                }
            }
            
            // 提取keywords
            if (shouldExtract(referenceTypes, ReferenceType.KEYWORDS) && analysis.getKeywords() != null) {
                TestScenarioAnalysis.Keywords keywords = analysis.getKeywords();
                if (keywords.getCore() != null) {
                    for (int i = 0; i < keywords.getCore().size(); i++) {
                        String core = keywords.getCore().get(i);
                        if (core != null && !core.isEmpty()) {
                            textMap.put("keywords:core:" + (i + 1), core);
                        }
                    }
                }
                if (keywords.getBusiness() != null) {
                    for (int i = 0; i < keywords.getBusiness().size(); i++) {
                        String business = keywords.getBusiness().get(i);
                        if (business != null && !business.isEmpty()) {
                            textMap.put("keywords:business:" + (i + 1), business);
                        }
                    }
                }
                if (keywords.getTechnical() != null) {
                    for (int i = 0; i < keywords.getTechnical().size(); i++) {
                        String technical = keywords.getTechnical().get(i);
                        if (technical != null && !technical.isEmpty()) {
                            textMap.put("keywords:technical:" + (i + 1), technical);
                        }
                    }
                }
            }
            
            // 提取scenarios
            if (shouldExtract(referenceTypes, ReferenceType.SCENARIOS) && analysis.getScenarios() != null) {
                for (int i = 0; i < analysis.getScenarios().size(); i++) {
                    TestScenarioAnalysis.Scenario scenario = analysis.getScenarios().get(i);
                    if (scenario != null) {
                        if (scenario.getType() != null && !scenario.getType().isEmpty()) {
                            textMap.put("scenario:" + (i + 1) + ":type", scenario.getType());
                        }
                        if (scenario.getDescription() != null && !scenario.getDescription().isEmpty()) {
                            textMap.put("scenario:" + (i + 1) + ":description", scenario.getDescription());
                        }
                        if (scenario.getTags() != null) {
                            for (int j = 0; j < scenario.getTags().size(); j++) {
                                String tag = scenario.getTags().get(j);
                                if (tag != null && !tag.isEmpty()) {
                                    textMap.put("scenario:" + (i + 1) + ":tag:" + (j + 1), tag);
                                }
                            }
                        }
                    }
                }
            }
            
            // 提取metadata
            if (shouldExtract(referenceTypes, ReferenceType.METADATA) && analysis.getMetadata() != null) {
                TestScenarioAnalysis.Metadata metadata = analysis.getMetadata();
                if (metadata.getTags() != null) {
                    for (int i = 0; i < metadata.getTags().size(); i++) {
                        String tag = metadata.getTags().get(i);
                        if (tag != null && !tag.isEmpty()) {
                            textMap.put("metadata:tag:" + (i + 1), tag);
                        }
                    }
                }
            }
        }
        
        // 2. 从候选用例提取文本
        if (candidates != null) {
            for (TestCase testCase : candidates) {
                if (testCase != null) {
                    // 标题
                    if (testCase.getCaseName() != null && !testCase.getCaseName().isEmpty()) {
                        textMap.put("testCase:title:" + testCase.getCaseId(), testCase.getCaseName());
                    }
                    
                    // 描述
                    if (testCase.getFullText() != null && !testCase.getFullText().isEmpty()) {
                        textMap.put("testCase:desc:" + testCase.getCaseId(), testCase.getFullText());
                    }
                    
                    // 步骤
                    if (testCase.getTestSteps() != null && !testCase.getTestSteps().isEmpty()) {
                        textMap.put("testCase:steps:" + testCase.getCaseId(), testCase.getTestSteps());
                    }
                }
            }
        }
        
        return textMap;
    }
    
    /**
     * 辅助方法：判断是否需要提取指定类型
     * @param types 参考文本类型列表
     * @param type 指定的参考文本类型
     * @return 是否需要提取
     */
    private boolean shouldExtract(List<ReferenceType> types, ReferenceType type) {
        return types == null || types.isEmpty() || types.contains(type);
    }
}
   