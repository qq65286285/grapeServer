package com.grape.grape.controller;

import com.grape.grape.entity.TestCaseGenerateTask;
import com.grape.grape.mapper.TestCaseGenerateTaskMapper;
import com.grape.grape.model.Resp;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 测试用例任务控制器
 */
@RestController
@RequestMapping("/ai/testcase/task")
public class TestCaseTaskController {

    private static final Logger log = LoggerFactory.getLogger(TestCaseTaskController.class);

    @Resource
    private TestCaseGenerateTaskMapper testCaseGenerateTaskMapper;

    /**
     * 查询任务列表（支持分页和筛选）
     * @param page 页码
     * @param pageSize 每页条数
     * @param status 任务状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 任务列表
     */
    @GetMapping("/list")
    public Resp getTaskList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        log.info("查询任务列表: page={}, pageSize={}, status={}, startDate={}, endDate={}",
                page, pageSize, status, startDate, endDate);

        try {
            // 构建查询条件
            QueryChain<TestCaseGenerateTask> query = QueryChain.of(TestCaseGenerateTask.class);

            // 状态筛选
            if (status != null) {
                query.where(TestCaseGenerateTask::getStatus).eq(status);
            }

            // 时间范围筛选
            if (startDate != null) {
                query.where(TestCaseGenerateTask::getCreatedAt).ge(LocalDateTime.parse(startDate));
            }
            if (endDate != null) {
                query.where(TestCaseGenerateTask::getCreatedAt).le(LocalDateTime.parse(endDate));
            }

            // 按创建时间倒序排序
            query.orderBy(TestCaseGenerateTask::getCreatedAt, false);

            // 执行分页查询
            Page<TestCaseGenerateTask> result = testCaseGenerateTaskMapper.paginate(page, pageSize, query);

            // 构建响应数据
            Map<String, Object> data = Map.of(
                    "total", result.getTotalRow(),
                    "pages", result.getTotalPage(),
                    "current", page,
                    "size", pageSize,
                    "records", result.getRecords()
            );

            return Resp.ok(data);
        } catch (Exception e) {
            log.error("查询任务列表失败", e);
            return Resp.error();
        }
    }

    /**
     * 查询任务详情
     * @param id 任务ID
     * @return 任务详情
     */
    @GetMapping("/detail")
    public Resp getTaskDetail(@RequestParam Long id) {
        log.info("查询任务详情: {}", id);

        try {
            TestCaseGenerateTask task = testCaseGenerateTaskMapper.selectOneById(id);
            if (task == null) {
                return Resp.info(404, "任务不存在");
            }
            return Resp.ok(task);
        } catch (Exception e) {
            log.error("查询任务详情失败", e);
            return Resp.error();
        }
    }

    /**
     * 删除单个任务
     * @param id 任务ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Resp deleteTask(@RequestParam Long id) {
        log.info("删除任务: {}", id);

        try {
            // 检查任务是否存在
            TestCaseGenerateTask task = testCaseGenerateTaskMapper.selectOneById(id);
            if (task == null) {
                return Resp.info(404, "任务不存在");
            }

            // 执行删除操作
            int result = testCaseGenerateTaskMapper.deleteById(id);
            if (result > 0) {
                return Resp.ok("删除成功");
            } else {
                return Resp.info(500, "删除失败");
            }
        } catch (Exception e) {
            log.error("删除任务失败", e);
            return Resp.error();
        }
    }
}