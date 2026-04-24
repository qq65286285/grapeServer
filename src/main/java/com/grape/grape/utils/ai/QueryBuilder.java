package com.grape.grape.utils.ai;

import com.grape.grape.model.prompt.ai.TestScenarioAnalysis;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryBuilder {

    /**
     * 策略1：完整语义查询
     * 拼接方式：平台 + 功能模块 + 实体类别:实体值 + 测试类型
     * 例如：Android PC平台 用户认证 登录功能 登录方式: 邮箱登录、谷歌登录、Apple登录、游客登录 功能测试 异常测试
     * @param analysis TestScenarioAnalysis对象
     * @return 查询字符串列表
     */
    public static List<String> buildFullSemanticQueries(TestScenarioAnalysis analysis) {
        List<String> queries = new ArrayList<>();
        if (analysis == null) {
            return queries;
        }

        // 构建基础查询
        StringBuilder baseQuery = new StringBuilder();

        // 添加平台信息
        if (analysis.getProduct() != null && analysis.getProduct().getPlatforms() != null && !analysis.getProduct().getPlatforms().isEmpty()) {
            baseQuery.append(String.join(" ", analysis.getProduct().getPlatforms())).append("平台 ");
        }

        // 添加功能模块信息
        if (analysis.getFunction() != null) {
            if (analysis.getFunction().getLevel1() != null && !analysis.getFunction().getLevel1().isEmpty()) {
                baseQuery.append(analysis.getFunction().getLevel1()).append(" ");
            }
            if (analysis.getFunction().getLevel2() != null && !analysis.getFunction().getLevel2().isEmpty()) {
                baseQuery.append(analysis.getFunction().getLevel2()).append("功能 ");
            }
        }

        // 添加登录方式信息
        if (analysis.getEntities() != null && !analysis.getEntities().isEmpty()) {
            for (TestScenarioAnalysis.Entity entity : analysis.getEntities()) {
                if ("登录方式".equals(entity.getCategory()) && entity.getValues() != null && !entity.getValues().isEmpty()) {
                    baseQuery.append("登录方式: " ).append(String.join("、", entity.getValues())).append(" ");
                    break;
                }
            }
        }

        // 添加测试类型信息
        if (analysis.getTest() != null && analysis.getTest().getTypes() != null && !analysis.getTest().getTypes().isEmpty()) {
            baseQuery.append(String.join(" ", analysis.getTest().getTypes()));
        }

        if (!baseQuery.isEmpty()) {
            queries.add(baseQuery.toString().trim());
        }

        return queries;
    }

    /**
     * 策略2：实体中心查询（精确匹配，推荐）
     * 拼接方式：功能名称 + 实体值
     * 实体类别 + 实体值 function.levelN + subfunctions[i]
     * 例如：登录功能 邮箱登录
     * @param analysis TestScenarioAnalysis对象
     * @return 查询字符串列表
     */
    public static List<String> buildEntityCentricQueries(TestScenarioAnalysis analysis) {
        List<String> queries = new ArrayList<>();
        if (analysis == null) {
            return queries;
        }

        // 功能名称
        String functionName = "";
        if (analysis.getFunction() != null && analysis.getFunction().getLevel2() != null && !analysis.getFunction().getLevel2().isEmpty()) {
            functionName = analysis.getFunction().getLevel2() + "功能";
        }

        // 一级模块作为实体类别
        String level1Module = "";
        if (analysis.getFunction() != null && analysis.getFunction().getLevel1() != null && !analysis.getFunction().getLevel1().isEmpty()) {
            level1Module = analysis.getFunction().getLevel1();
        }

        // 收集所有实体值（登录方式和子功能）
        Set<String> entityValues = new HashSet<>();
        
        // 登录方式作为实体值
        if (analysis.getEntities() != null && !analysis.getEntities().isEmpty()) {
            for (TestScenarioAnalysis.Entity entity : analysis.getEntities()) {
                if ("登录方式".equals(entity.getCategory()) && entity.getValues() != null && !entity.getValues().isEmpty()) {
                    entityValues.addAll(entity.getValues());
                    break;
                }
            }
        }

        // 子功能作为实体值
        if (analysis.getFunction() != null && analysis.getFunction().getSubFunctions() != null && !analysis.getFunction().getSubFunctions().isEmpty()) {
            entityValues.addAll(analysis.getFunction().getSubFunctions());
        }

        // 生成查询
        for (String entityValue : entityValues) {
            if (!functionName.isEmpty()) {
                queries.add(functionName + " " + entityValue);
            }
            if (!level1Module.isEmpty()) {
                queries.add(level1Module + " " + entityValue);
            }
        }

        return queries;
    }

    /**
     * 策略3：测试点直接查询（细粒度）
     * 直接使用 testPoints 数组中的每一项
     * 例如：邮箱登录成功
     * @param analysis TestScenarioAnalysis对象
     * @return 查询字符串列表
     */
    public static List<String> buildTestPointQueries(TestScenarioAnalysis analysis) {
        List<String> queries = new ArrayList<>();
        if (analysis == null) {
            return queries;
        }

        if (analysis.getTest() != null && analysis.getTest().getTestPoints() != null && !analysis.getTest().getTestPoints().isEmpty()) {
            queries.addAll(analysis.getTest().getTestPoints());
        }

        return queries;
    }

    /**
     * 策略4：场景描述查询（自然语言）
     * @param analysis TestScenarioAnalysis对象
     * @return 查询字符串列表
     */
    public static List<String> buildScenarioDescriptionQueries(TestScenarioAnalysis analysis) {
        List<String> queries = new ArrayList<>();
        if(analysis == null || CollectionUtil.isEmpty(analysis.getScenarios())){
            return new ArrayList<>();
        }
        for(TestScenarioAnalysis.Scenario scenario : analysis.getScenarios()){
            if(scenario.getDescription() != null && !scenario.getDescription().isEmpty()){
                queries.add(scenario.getDescription());
            }
        }
        return queries;
    }

    /**
     * 策略5：层级渐进查询（由粗到细）
     * 拼接方式：按功能层级逐层细化
     * level1 + 平台
     * level1 + level2
     * level2 + subFunction
     * testPoints
     * 例如：
     * 用户认证 Android
     * 用户认证 登录功能
     * 登录功能 邮箱登录
     * 邮箱登录成功
     * @param analysis TestScenarioAnalysis对象
     * @return 查询字符串列表
     */
    public static List<String> buildHierarchicalQueries(TestScenarioAnalysis analysis) {
        List<String> queries = new ArrayList<>();
        if (analysis == null) {
            return queries;
        }

        // level1 + 平台
        if (analysis.getFunction() != null && analysis.getFunction().getLevel1() != null && !analysis.getFunction().getLevel1().isEmpty()) {
            if (analysis.getProduct() != null && analysis.getProduct().getPlatforms() != null && !analysis.getProduct().getPlatforms().isEmpty()) {
                for (String platform : analysis.getProduct().getPlatforms()) {
                    queries.add(analysis.getFunction().getLevel1() + " " + platform);
                }
            }
        }

        // level1 + level2
        if (analysis.getFunction() != null) {
            if (analysis.getFunction().getLevel1() != null && !analysis.getFunction().getLevel1().isEmpty() &&
                    analysis.getFunction().getLevel2() != null && !analysis.getFunction().getLevel2().isEmpty()) {
                queries.add(analysis.getFunction().getLevel1() + " " + analysis.getFunction().getLevel2() + "功能");
            }
        }

        // level2 + subFunction
        if (analysis.getFunction() != null && analysis.getFunction().getLevel2() != null && !analysis.getFunction().getLevel2().isEmpty()) {
            if (analysis.getFunction().getSubFunctions() != null && !analysis.getFunction().getSubFunctions().isEmpty()) {
                for (String subFunction : analysis.getFunction().getSubFunctions()) {
                    queries.add(analysis.getFunction().getLevel2() + "功能 " + subFunction);
                }
            }
        }

        // testPoints
        if (analysis.getTest() != null && analysis.getTest().getTestPoints() != null && !analysis.getTest().getTestPoints().isEmpty()) {
            queries.addAll(analysis.getTest().getTestPoints());
        }

        return queries;
    }

    /**
     * 构建所有策略的查询
     * @param analysis TestScenarioAnalysis对象
     * @return 所有查询字符串列表
     */
    public static List<String> buildAllQueries(TestScenarioAnalysis analysis) {
        List<String> allQueries = new ArrayList<>();
        allQueries.addAll(buildFullSemanticQueries(analysis));
        allQueries.addAll(buildEntityCentricQueries(analysis));
        allQueries.addAll(buildTestPointQueries(analysis));
        allQueries.addAll(buildScenarioDescriptionQueries(analysis));
        allQueries.addAll(buildHierarchicalQueries(analysis));
        return allQueries;
    }
}