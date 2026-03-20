package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划版本表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_version")
public class TestPlanVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 版本号
     */
    private String versionNo;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 版本类型: 1-自动保存, 2-手动保存, 3-里程碑版本
     */
    private Integer versionType;

    /**
     * 变更类型: 1-创建, 2-修改基本信息, 3-修改范围, 4-修改成员, 5-修改配置
     */
    private Integer changeType;

    /**
     * 变更摘要
     */
    private String changeSummary;

    /**
     * 变更明细: [{"field": "status", "before": 1, "after": 2, "label": "状态"}]
     */
    private String changeDetail;

    /**
     * 完整快照数据
     */
    private String snapshotData;

    /**
     * 用例数
     */
    private Integer caseCount;

    /**
     * 已执行数
     */
    private Integer executedCount;

    /**
     * 通过数
     */
    private Integer passCount;

    /**
     * 执行率
     */
    private Double executeRate;

    /**
     * 通过率
     */
    private Double passRate;

    /**
     * 快照大小(字节)
     */
    private Long fileSize;

    /**
     * 快照文件路径
     */
    private String filePath;

    /**
     * 是否基线版本: 0-否, 1-是
     */
    private Integer isBaseline;

    /**
     * 基线名称
     */
    private String baselineName;

    /**
     * 是否回滚版本: 0-否, 1-是
     */
    private Integer isRollback;

    /**
     * 回滚来源版本ID
     */
    private Long rollbackFromVersion;

    /**
     * 对比版本ID
     */
    private Long compareWithVersion;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;
}