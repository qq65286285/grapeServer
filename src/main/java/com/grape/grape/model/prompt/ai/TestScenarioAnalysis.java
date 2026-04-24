package com.grape.grape.model.prompt.ai;

import lombok.Data;

import java.util.List;

@Data
public class TestScenarioAnalysis {
    // 产品信息
    private ProductInfo product;
    // 功能信息
    private FunctionInfo function;
    // 测试信息
    private TestInfo test;
    // 业务实体列表
    private List<Entity> entities;
    // 技术信息
    private TechnicalInfo technical;
    // 关键词
    private Keywords keywords;
    // 场景列表
    private List<Scenario> scenarios;
    // 元数据
    private Metadata metadata;

    // 产品信息
    @Data
    public static class ProductInfo {
        private String type;              // SDK/API/APP/Web/H5/小程序
        private String name;              // 产品名称
        private List<String> platforms;   // Android/iOS/PC/Web/小程序
        private String version;           // 版本号（可选）
    }

    // 功能信息
    @Data
    public static class FunctionInfo {
        private String level1;            // 一级功能模块
        private String level2;            // 二级功能
        private String level3;            // 三级功能（可选）
        private List<String> subFunctions; // 子功能列表
    }

    // 测试信息
    @Data
    public static class TestInfo {
        private List<String> types;       // 测试类型：功能测试/异常测试/性能测试等
        private List<String> priorities;  // 优先级：P0/P1/P2/P3
        private List<String> focuses;     // 测试关注点：正常流程/异常处理/边界条件等
        private List<String> testPoints;  // 具体测试点
    }

    // 业务实体（核心字段）
    @Data
    public static class Entity {
        private String category;          // 实体类别：登录方式/支付方式/消息类型等
        private List<String> values;      // 实体值列表
    }

    // 技术信息
    @Data
    public static class TechnicalInfo {
        private List<String> terms;       // 技术术语：OAuth/token/JWT/session/API/SDK等
        private List<String> protocols;   // 协议：HTTP/HTTPS/WebSocket/gRPC/OAuth2.0等
        private List<String> dependencies; // 依赖项
    }

    // 关键词
    @Data
    public static class Keywords {
        private List<String> core;        // 核心关键词
        private List<String> business;    // 业务关键词
        private List<String> technical;   // 技术关键词
    }

    // 场景信息
    @Data
    public static class Scenario {
        private String type;              // 场景类型：正常场景/异常场景/边界场景/性能场景
        private String description;       // 场景描述
        private List<String> tags;        // 场景标签
    }

    // 元数据
    @Data
    public static class Metadata {
        private List<String> tags;        // 通用标签
    }
}