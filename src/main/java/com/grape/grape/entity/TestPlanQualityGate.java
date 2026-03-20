package com.grape.grape.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划质量门禁表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_quality_gate")
public class TestPlanQualityGate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门禁ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 测试计划ID
     */
    private Long planId;

    /**
     * 门禁名称
     */
    private String gateName;

    /**
     * 门禁类型: 1-用例通过率, 2-缺陷数, 3-覆盖率, 4-自定义
     */
    private Integer gateType;

    /**
     * 条件运算符: >=, <=, =, >, <
     */
    private String conditionOperator;

    /**
     * 阈值
     */
    private BigDecimal thresholdValue;

    /**
     * 当前值
     */
    private BigDecimal currentValue;

    /**
     * 门禁状态: 1-未检查, 2-通过, 3-不通过
     */
    private Integer gateStatus;

    /**
     * 是否强制: 0-否, 1-是
     */
    private Integer isMandatory;

    /**
     * 检查时间
     */
    private Date checkTime;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}