package com.grape.grape.controller;

import com.grape.grape.service.QdrantSyncService;
import com.grape.grape.model.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Qdrant 同步控制器
 * 用于测试和管理 Qdrant 同步服务
 */
@RestController
@RequestMapping("/api/qdrant/sync")
public class QdrantSyncController {

    @Autowired
    private QdrantSyncService qdrantSyncService;

    /**
     * 初始化同步服务
     */
    @PostMapping("/init")
    public String initSync() {
        try {
            qdrantSyncService.init();
            return "✅ Qdrant 同步服务初始化成功";
        } catch (Exception e) {
            return "❌ Qdrant 同步服务初始化失败: " + e.getMessage();
        }
    }

    /**
     * 执行全量同步
     */
    @PostMapping("/full")
    public String fullSync() {
        try {
            qdrantSyncService.syncAllCases();
            return "✅ 全量同步完成";
        } catch (Exception e) {
            return "❌ 全量同步失败: " + e.getMessage();
        }
    }

    /**
     * 查询向量数据库中的所有向量
     */
    @GetMapping("/vectors")
    public Resp getAllVectors() {
        try {
            List<Map<String, Object>> vectors = qdrantSyncService.getAllVectors();
            return Resp.ok(vectors);
        } catch (Exception e) {
            return Resp.error();
        }
    }

    /**
     * 根据测试用例ID查询向量
     */
    @GetMapping("/vector/{caseId}")
    public Resp  getVectorByCaseId(@PathVariable Integer caseId) {
        try {
            Map<String, Object> vector = qdrantSyncService.getVectorByCaseId(caseId);
            return Resp.ok(vector);
        } catch (Exception e) {
            return Resp.error();
        }
    }

    /**
     * 相似向量搜索
     */
    @PostMapping("/search")
    public Resp searchSimilarVectors(@RequestBody Map<String, Object> request) {
        try {
            String queryText = (String) request.get("query");
            Integer limit = request.get("limit") != null ? (Integer) request.get("limit") : 5;
            List<Map<String, Object>> results = qdrantSyncService.searchSimilarVectors(queryText, limit);
            return Resp.ok(results);
        } catch (Exception e) {
            return Resp.error();
        }
    }
}

