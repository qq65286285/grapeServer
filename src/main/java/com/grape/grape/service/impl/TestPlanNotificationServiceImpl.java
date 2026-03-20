package com.grape.grape.service.impl;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestPlanNotification;
import com.grape.grape.mapper.TestPlanNotificationMapper;
import com.grape.grape.service.TestPlanNotificationService;
import com.grape.grape.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划通知表 服务层实现。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@Service
public class TestPlanNotificationServiceImpl extends ServiceImpl<TestPlanNotificationMapper, TestPlanNotification> implements TestPlanNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TestPlanNotificationServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public List<TestPlanNotification> listByPlanId(Long planId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("plan_id = ? and is_deleted = 0", planId)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanNotification> listByNotifyType(Integer notifyType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("notify_type = ? and is_deleted = 0", notifyType)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public List<TestPlanNotification> listBySendStatus(Integer sendStatus) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("send_status = ? and is_deleted = 0", sendStatus)
                .orderBy("created_at desc");
        return list(queryWrapper);
    }

    @Override
    public Page<TestPlanNotification> page(Page<TestPlanNotification> page, Long planId, Integer notifyType, Integer sendStatus, Integer channelType) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where("is_deleted = 0");

        if (planId != null) {
            queryWrapper.and("plan_id = ?", planId);
        }

        if (notifyType != null) {
            queryWrapper.and("notify_type = ?", notifyType);
        }

        if (sendStatus != null) {
            queryWrapper.and("send_status = ?", sendStatus);
        }

        if (channelType != null) {
            queryWrapper.and("channel_type = ?", channelType);
        }

        queryWrapper.orderBy("created_at desc");

        return getMapper().paginate(page, queryWrapper);
    }

    @Override
    public boolean updateSendStatus(Long id, Integer sendStatus, String failReason) {
        TestPlanNotification notification = getById(id);
        if (notification != null) {
            notification.setSendStatus(sendStatus);
            if (failReason != null) {
                notification.setFailReason(failReason);
            }
            if (sendStatus == 1) { // 已发送
                notification.setSendTime(System.currentTimeMillis());
            }
            notification.setUpdatedAt(System.currentTimeMillis());
            return updateById(notification);
        }
        return false;
    }

    @Override
    public boolean increaseRetryCount(Long id) {
        TestPlanNotification notification = getById(id);
        if (notification != null) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setUpdatedAt(System.currentTimeMillis());
            return updateById(notification);
        }
        return false;
    }

    @Override
    public boolean increaseReadCount(Long id) {
        TestPlanNotification notification = getById(id);
        if (notification != null) {
            notification.setReadCount(notification.getReadCount() + 1);
            notification.setUpdatedAt(System.currentTimeMillis());
            return updateById(notification);
        }
        return false;
    }

    @Override
    public boolean sendNotification(TestPlanNotification notification) {
        try {
            // 这里实现具体的通知发送逻辑
            // 根据通知渠道类型选择不同的发送方式
            switch (notification.getChannelType()) {
                case 1: // 站内信
                    // 实现站内信发送逻辑
                    break;
                case 2: // 邮件
                    // 实现邮件发送逻辑
                    break;
                case 3: // 短信
                    // 实现短信发送逻辑
                    break;
                case 4: // 企业微信
                    // 实现企业微信发送逻辑
                    break;
                case 5: // 钉钉
                    // 实现钉钉发送逻辑
                    break;
                default:
                    log.warn("未知的通知渠道类型: {}", notification.getChannelType());
                    return false;
            }
            
            // 发送成功，更新状态
            return updateSendStatus(notification.getId(), 1, null);
        } catch (Exception e) {
            log.error("发送通知失败: {}", e.getMessage());
            // 发送失败，更新状态
            updateSendStatus(notification.getId(), 2, e.getMessage());
            return false;
        }
    }

    @Override
    public int sendBatchNotifications(List<TestPlanNotification> notifications) {
        int successCount = 0;
        for (TestPlanNotification notification : notifications) {
            if (sendNotification(notification)) {
                successCount++;
            }
        }
        return successCount;
    }

    @Override
    public boolean save(TestPlanNotification testPlanNotification) {
        // 设置默认值
        if (testPlanNotification.getSendStatus() == null) {
            testPlanNotification.setSendStatus(0); // 0-待发送
        }
        if (testPlanNotification.getRetryCount() == null) {
            testPlanNotification.setRetryCount(0);
        }
        if (testPlanNotification.getReadCount() == null) {
            testPlanNotification.setReadCount(0);
        }
        if (testPlanNotification.getTotalCount() == null) {
            testPlanNotification.setTotalCount(0);
        }
        if (testPlanNotification.getPriority() == null) {
            testPlanNotification.setPriority(2); // 2-中
        }
        if (testPlanNotification.getIsDeleted() == null) {
            testPlanNotification.setIsDeleted(0); // 0-否
        }

        // 设置时间戳
        long now = System.currentTimeMillis();
        if (testPlanNotification.getCreatedAt() == null) {
            testPlanNotification.setCreatedAt(now);
        }
        if (testPlanNotification.getUpdatedAt() == null) {
            testPlanNotification.setUpdatedAt(now);
        }

        // 设置创建人和更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                if (testPlanNotification.getCreatedBy() == null) {
                    testPlanNotification.setCreatedBy(userId);
                }
                testPlanNotification.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        // 保存通知
        boolean saved = super.save(testPlanNotification);
        
        // 如果保存成功且发送状态为待发送，则发送通知
        if (saved && testPlanNotification.getSendStatus() == 0) {
            sendNotification(testPlanNotification);
        }

        return saved;
    }

    @Override
    public boolean updateById(TestPlanNotification testPlanNotification) {
        // 设置更新时间
        testPlanNotification.setUpdatedAt(System.currentTimeMillis());

        // 设置更新人
        String userIdStr = UserUtils.getCurrentLoginUserId(userService);
        if (userIdStr != null) {
            try {
                Long userId = Long.parseLong(userIdStr);
                testPlanNotification.setUpdatedBy(userId);
            } catch (NumberFormatException e) {
                log.warn("无法解析用户ID: {}", userIdStr);
            }
        }

        return super.updateById(testPlanNotification);
    }
}
