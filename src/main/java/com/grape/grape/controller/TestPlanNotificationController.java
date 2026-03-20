package com.grape.grape.controller;

import com.grape.grape.entity.TestPlanNotification;
import com.grape.grape.model.Resp;
import com.grape.grape.service.TestPlanNotificationService;
import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试计划通知表 控制器。
 *
 * @author Administrator
 * @since 2026-03-17
 */
@RestController
@RequestMapping("/api/testPlanNotification")
public class TestPlanNotificationController {

    @Autowired
    private TestPlanNotificationService testPlanNotificationService;

    /**
     * 新增测试计划通知
     *
     * @param testPlanNotification 测试计划通知
     * @return 新增结果
     */
    @PostMapping
    public Resp save(@RequestBody TestPlanNotification testPlanNotification) {
        boolean saved = testPlanNotificationService.save(testPlanNotification);
        if (saved) {
            return Resp.ok(testPlanNotification);
        } else {
            return Resp.error();
        }
    }

    /**
     * 修改测试计划通知
     *
     * @param testPlanNotification 测试计划通知
     * @return 修改结果
     */
    @PutMapping
    public Resp update(@RequestBody TestPlanNotification testPlanNotification) {
        boolean updated = testPlanNotificationService.updateById(testPlanNotification);
        if (updated) {
            return Resp.ok(testPlanNotification);
        } else {
            return Resp.error();
        }
    }

    /**
     * 根据ID删除测试计划通知
     *
     * @param id 测试计划通知ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Resp remove(@PathVariable Long id) {
        TestPlanNotification notification = testPlanNotificationService.getById(id);
        if (notification != null) {
            notification.setIsDeleted(1);
            boolean deleted = testPlanNotificationService.updateById(notification);
            if (deleted) {
                return Resp.ok("删除成功");
            }
        }
        return Resp.error();
    }

    /**
     * 根据ID查询测试计划通知
     *
     * @param id 测试计划通知ID
     * @return 测试计划通知
     */
    @GetMapping("/{id}")
    public Resp getById(@PathVariable Long id) {
        TestPlanNotification testPlanNotification = testPlanNotificationService.getById(id);
        if (testPlanNotification != null && testPlanNotification.getIsDeleted() == 0) {
            return Resp.ok(testPlanNotification);
        } else {
            return Resp.error();
        }
    }

    /**
     * 分页查询测试计划通知
     *
     * @param page 页码
     * @param size 每页大小
     * @param planId 计划ID（可选）
     * @param notifyType 通知类型（可选）
     * @param sendStatus 发送状态（可选）
     * @param channelType 通知渠道（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Resp page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                    @RequestParam(required = false) Long planId, @RequestParam(required = false) Integer notifyType,
                    @RequestParam(required = false) Integer sendStatus, @RequestParam(required = false) Integer channelType) {
        Page<TestPlanNotification> result = testPlanNotificationService.page(Page.of(page, size), planId, notifyType, sendStatus, channelType);
        return Resp.ok(result);
    }

    /**
     * 根据计划ID查询通知列表
     *
     * @param planId 计划ID
     * @return 通知列表
     */
    @GetMapping("/listByPlanId/{planId}")
    public Resp listByPlanId(@PathVariable Long planId) {
        List<TestPlanNotification> list = testPlanNotificationService.listByPlanId(planId);
        return Resp.ok(list);
    }

    /**
     * 根据通知类型查询通知列表
     *
     * @param notifyType 通知类型
     * @return 通知列表
     */
    @GetMapping("/listByNotifyType/{notifyType}")
    public Resp listByNotifyType(@PathVariable Integer notifyType) {
        List<TestPlanNotification> list = testPlanNotificationService.listByNotifyType(notifyType);
        return Resp.ok(list);
    }

    /**
     * 根据发送状态查询通知列表
     *
     * @param sendStatus 发送状态
     * @return 通知列表
     */
    @GetMapping("/listBySendStatus/{sendStatus}")
    public Resp listBySendStatus(@PathVariable Integer sendStatus) {
        List<TestPlanNotification> list = testPlanNotificationService.listBySendStatus(sendStatus);
        return Resp.ok(list);
    }

    /**
     * 更新通知发送状态
     *
     * @param id 通知ID
     * @param sendStatus 发送状态
     * @param failReason 失败原因（可选）
     * @return 操作结果
     */
    @PutMapping("/updateSendStatus/{id}")
    public Resp updateSendStatus(@PathVariable Long id, @RequestParam Integer sendStatus,
                               @RequestParam(required = false) String failReason) {
        boolean updated = testPlanNotificationService.updateSendStatus(id, sendStatus, failReason);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 增加重试次数
     *
     * @param id 通知ID
     * @return 操作结果
     */
    @PutMapping("/increaseRetryCount/{id}")
    public Resp increaseRetryCount(@PathVariable Long id) {
        boolean updated = testPlanNotificationService.increaseRetryCount(id);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 增加已读人数
     *
     * @param id 通知ID
     * @return 操作结果
     */
    @PutMapping("/increaseReadCount/{id}")
    public Resp increaseReadCount(@PathVariable Long id) {
        boolean updated = testPlanNotificationService.increaseReadCount(id);
        if (updated) {
            return Resp.ok("更新成功");
        } else {
            return Resp.error();
        }
    }

    /**
     * 发送通知
     *
     * @param id 通知ID
     * @return 操作结果
     */
    @PostMapping("/sendNotification/{id}")
    public Resp sendNotification(@PathVariable Long id) {
        TestPlanNotification notification = testPlanNotificationService.getById(id);
        if (notification != null) {
            boolean sent = testPlanNotificationService.sendNotification(notification);
            if (sent) {
                return Resp.ok("发送成功");
            } else {
                return Resp.error();
            }
        } else {
            return Resp.error();
        }
    }

    /**
     * 批量发送通知
     *
     * @param notifications 通知列表
     * @return 发送成功的数量
     */
    @PostMapping("/sendBatchNotifications")
    public Resp sendBatchNotifications(@RequestBody List<TestPlanNotification> notifications) {
        int successCount = testPlanNotificationService.sendBatchNotifications(notifications);
        return Resp.ok(successCount);
    }
}
