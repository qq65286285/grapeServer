package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.mapper.TestCaseStepMapper;
import com.grape.grape.service.TestCaseStepService;
import com.grape.grape.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试用例步骤表 服务层实现。
 *
 * @author Administrator
 * @since 2026-02-05
 */
@Service
public class TestCaseStepServiceImpl extends ServiceImpl<TestCaseStepMapper, TestCaseStep> implements TestCaseStepService {

    @Resource
    private UserService userService;

    @Override
    public List<TestCaseStep> getByTestCaseId(Integer testCaseId) {
        return getMapper().getByTestCaseId(testCaseId);
    }

    @Override
    public boolean saveSteps(Integer testCaseId, List<TestCaseStep> steps) {
        try {
            System.out.println("=== 开始保存步骤 ===");
            System.out.println("caseId: " + testCaseId);
            System.out.println("steps: " + steps);
            System.out.println("steps.size(): " + steps.size());
            
            // 先删除测试用例的所有步骤
            System.out.println("删除旧步骤");
            boolean removed = removeByTestCaseId(testCaseId);
            System.out.println("删除旧步骤结果: " + removed);
            
            // 保存新的步骤列表
            long currentTime = System.currentTimeMillis();
            String currentUserId = UserUtils.getCurrentLoginUserId(userService);
            // 如果无法获取用户，使用"system"作为默认值
            if (currentUserId == null) {
                currentUserId = "system";
            }
            System.out.println("currentUserId: " + currentUserId);
            
            // 遍历步骤列表，按顺序设置步骤序号并保存
            for (int i = 0; i < steps.size(); i++) {
                TestCaseStep step = steps.get(i);
                System.out.println("保存步骤 " + (i + 1) + ": " + step);
                
                // 跳过null元素
                if (step == null) {
                    System.out.println("步骤 " + (i + 1) + " 为null，跳过");
                    continue;
                }
                
                step.setId(null); // 清除id，避免主键冲突
                step.setTestCaseId(testCaseId);
                step.setStepNumber(i + 1); // 按遍历顺序设置步骤序号，确保顺序正确
                step.setCreatedAt(currentTime);
                step.setUpdatedAt(currentTime);
                step.setCreatedBy(currentUserId);
                step.setUpdatedBy(currentUserId);
                // 确保 step 字段正确映射到 step_description 列
                if (step.getStep() != null) {
                    // 这里不需要额外处理，MyBatis Flex 会根据 @Column 注解自动映射
                }
                save(step);
                System.out.println("步骤 " + (i + 1) + " 保存成功");
            }
            
            System.out.println("=== 步骤保存完成 ===");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeByTestCaseId(Integer testCaseId) {
        try {
            getMapper().deleteByTestCaseId(testCaseId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
