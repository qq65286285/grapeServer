package com.grape.grape.service.biz;

import com.grape.grape.entity.Cases;
import com.grape.grape.service.CasesService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试用例编号生成服务
 * 生成规则：TC+年月日+4位编号
 * 确保线程安全，保证编号不重复
 */
@Service
public class CaseNumberGeneratorService {

    @Resource
    private CasesService casesService;

    // 锁对象，确保线程安全
    private final ReentrantLock lock = new ReentrantLock();

    // 日期格式，用于生成编号中的年月日部分
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 生成测试用例编号
     * @return 测试用例编号，格式：TC+年月日+4位编号
     */
    public String generateCaseNumber() {
        lock.lock();
        try {
            // 获取当前日期
            String dateStr = dateFormat.format(new Date());
            
            // 构建查询条件，查找当天已有的最大编号
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.like("case_number", "TC" + dateStr);
            queryWrapper.orderBy("case_number", false);
            
            // 获取当天的最大编号
            Cases maxCase = casesService.getMapper().selectOneByQuery(queryWrapper);
            String maxCaseNumber = maxCase != null ? maxCase.getCaseNumber() : null;
            
            // 计算下一个编号
            int sequence = 1;
            if (maxCaseNumber != null && maxCaseNumber.length() == 12) { // TC+8位日期+4位编号
                try {
                    String sequenceStr = maxCaseNumber.substring(10); // 截取最后4位
                    sequence = Integer.parseInt(sequenceStr) + 1;
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用默认值1
                    sequence = 1;
                }
            }
            
            // 格式化编号，确保4位序列
            return String.format("TC%s%04d", dateStr, sequence);
        } finally {
            lock.unlock();
        }
    }
}
