package com.grape.grape.service;

import com.grape.grape.entity.TestPlanNotification;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 测试计划通知表 服务层。
 *
 * @author Administrator
 * @since 2026-03-17
 */
public interface TestPlanNotificationService extends MyBaseService<TestPlanNotification> {

    /**
     * 根据计划ID查询通知列表
     *
     * @param planId 计划ID
     * @return 通知列表
     */
    List<TestPlanNotification> listByPlanId(Long planId);

    /**
     * 根据通知类型查询通知列表
     *
     * @param notifyType 通知类型
     * @return 通知列表
     */
    List<TestPlanNotification> listByNotifyType(Integer notifyType);

    /**
     * 根据发送状态查询通知列表
     *
     * @param sendStatus 发送状态
     * @return 通知列表
     */
    List<TestPlanNotification> listBySendStatus(Integer sendStatus);

    /**
     * 分页查询通知
     *
     * @param page 分页参数
     * @param planId 计划ID（可选）
     * @param notifyType 通知类型（可选）
     * @param sendStatus 发送状态（可选）
     * @param channelType 通知渠道（可选）
     * @return 分页结果
     */
    Page<TestPlanNotification> page(Page<TestPlanNotification> page, Long planId, Integer notifyType, Integer sendStatus, Integer channelType);

    /**
     * 更新通知发送状态
     *
     * @param id 通知ID
     * @param sendStatus 发送状态
     * @param failReason 失败原因（可选）
     * @return 是否操作成功
     */
    boolean updateSendStatus(Long id, Integer sendStatus, String failReason);

    /**
     * 增加重试次数
     *
     * @param id 通知ID
     * @return 是否操作成功
     */
    boolean increaseRetryCount(Long id);

    /**
     * 增加已读人数
     *
     * @param id 通知ID
     * @return 是否操作成功
     */
    boolean increaseReadCount(Long id);

    /**
     * 发送通知
     *
     * @param notification 通知对象
     * @return 是否发送成功
     */
    boolean sendNotification(TestPlanNotification notification);

    /**
     * 批量发送通知
     *
     * @param notifications 通知列表
     * @return 发送成功的数量
     */
    int sendBatchNotifications(List<TestPlanNotification> notifications);
}
