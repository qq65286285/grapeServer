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
 * 测试计划通知表 实体类。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_plan_notification")
public class TestPlanNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 通知类型: 1-计划创建, 2-计划变更, 3-任务分配, 4-执行提醒, 5-审批通知, 6-报告发布, 7-逾期提醒
     */
    private Integer notifyType;

    /**
     * 通知标题
     */
    private String notifyTitle;

    /**
     * 通知内容
     */
    private String notifyContent;

    /**
     * 目标类型: 1-指定用户, 2-角色, 3-全部成员
     */
    private Integer targetType;

    /**
     * 目标用户: [102, 103, 104]
     */
    private String targetUsers;

    /**
     * 通知渠道: 1-站内信, 2-邮件, 3-短信, 4-企业微信, 5-钉钉
     */
    private Integer channelType;

    /**
     * 发送时间(毫秒级时间戳)
     */
    private Long sendTime;

    /**
     * 发送状态: 0-待发送, 1-已发送, 2-发送失败
     */
    private Integer sendStatus;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 已读人数
     */
    private Integer readCount;

    /**
     * 目标总人数
     */
    private Integer totalCount;

    /**
     * 优先级: 1-高, 2-中, 3-低
     */
    private Integer priority;

    /**
     * 额外数据
     */
    private String extraData;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间(毫秒级时间戳)
     */
    private Long createdAt;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 更新时间(毫秒级时间戳)
     */
    private Long updatedAt;

    /**
     * 是否删除: 0-否, 1-是
     */
    private Integer isDeleted;
}
