package com.grape.grape.service.biz;

import com.grape.grape.entity.Cases;
import com.grape.grape.model.vo.dashboard.DistributionVO;
import com.grape.grape.model.vo.dashboard.TestCaseStatsVO;
import com.grape.grape.model.vo.dashboard.TopCreatorVO;
import com.grape.grape.model.vo.dashboard.WeekTrendVO;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 驾驶舱用例TAB业务服务实现类
 */
@Service
public class TestCaseStatsBizServiceImpl implements TestCaseStatsBizService {

    @Resource
    private DataSource dataSource;

    @Override
    public TestCaseStatsVO getTestCaseStats() {
        TestCaseStatsVO stats = new TestCaseStatsVO();

        // 1. 用例总数
        Long totalCaseCount = QueryChain.of(Cases.class)
                .where(Cases::getIsDeleted).eq(0)
                .count();
        stats.setTotalCaseCount(totalCaseCount);

        // 2. 本周新增用例数
        Long weeklyNewCaseCount = getWeeklyNewCaseCount();
        stats.setWeeklyNewCaseCount(weeklyNewCaseCount);

        // 3. 用例新增趋势（最近6周）
        List<WeekTrendVO> weeklyTrend = getWeeklyTrend();
        stats.setWeeklyTrend(weeklyTrend);

        // 4. 用例模块分布
        List<DistributionVO> moduleDistribution = getModuleDistribution();
        stats.setModuleDistribution(moduleDistribution);

        // 5. 用例优先级分布
        List<DistributionVO> priorityDistribution = getPriorityDistribution();
        stats.setPriorityDistribution(priorityDistribution);

        // 6. 用例文件夹分布
        List<DistributionVO> folderDistribution = getFolderDistribution();
        stats.setFolderDistribution(folderDistribution);

        // 7. 用例创建TOP-10人员
        List<TopCreatorVO> topCreators = getTopCreators();
        stats.setTopCreators(topCreators);

        return stats;
    }

    /**
     * 获取本周新增用例数
     */
    private Long getWeeklyNewCaseCount() {
        long[] weekRange = getCurrentWeekRange();
        return QueryChain.of(Cases.class)
                .where(Cases::getCreatedAt).between(weekRange[0], weekRange[1])
                .and(Cases::getIsDeleted).eq(0)
                .count();
    }

    /**
     * 获取最近6周的用例新增趋势
     */
    private List<WeekTrendVO> getWeeklyTrend() {
        List<WeekTrendVO> trendList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 获取最近6周的数据
        for (int i = 5; i >= 0; i--) {
            Calendar weekCalendar = (Calendar) calendar.clone();
            weekCalendar.add(Calendar.WEEK_OF_YEAR, -i);

            long weekStart = weekCalendar.getTimeInMillis();
            weekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
            weekCalendar.add(Calendar.MILLISECOND, -1);
            long weekEnd = weekCalendar.getTimeInMillis();

            Long count = QueryChain.of(Cases.class)
                    .where(Cases::getCreatedAt).between(weekStart, weekEnd)
                    .and(Cases::getIsDeleted).eq(0)
                    .count();

            WeekTrendVO trend = new WeekTrendVO();
            trend.setWeekStartDate(dateFormat.format(new Date(weekStart)));
            trend.setNewCaseCount(count);

            trendList.add(trend);
        }

        return trendList;
    }

    /**
     * 获取用例模块分布
     */
    private List<DistributionVO> getModuleDistribution() {
        List<DistributionVO> result = new ArrayList<>();
        
        String sql = "SELECT module as name, COUNT(id) as count FROM test_cases WHERE is_deleted = 0 AND module IS NOT NULL GROUP BY module ORDER BY count DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                DistributionVO vo = new DistributionVO();
                vo.setName(rs.getString("name"));
                vo.setCount(rs.getLong("count"));
                result.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取用例优先级分布
     */
    private List<DistributionVO> getPriorityDistribution() {
        List<DistributionVO> result = new ArrayList<>();

        String[] priorityNames = {"Low", "Medium", "High"};
        
        for (int i = 0; i < 3; i++) {
            Long count = QueryChain.of(Cases.class)
                    .where(Cases::getPriority).eq(i + 1)
                    .and(Cases::getIsDeleted).eq(0)
                    .count();
            result.add(new DistributionVO(priorityNames[i], count));
        }

        return result;
    }

    /**
     * 获取用例文件夹分布
     */
    private List<DistributionVO> getFolderDistribution() {
        List<DistributionVO> result = new ArrayList<>();
        
        String sql = "SELECT folder_id as name, COUNT(id) as count FROM test_cases WHERE is_deleted = 0 AND folder_id IS NOT NULL GROUP BY folder_id ORDER BY count DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                DistributionVO vo = new DistributionVO();
                vo.setName(String.valueOf(rs.getLong("name")));
                vo.setCount(rs.getLong("count"));
                result.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取用例创建TOP-10人员
     */
    private List<TopCreatorVO> getTopCreators() {
        List<TopCreatorVO> result = new ArrayList<>();
        
        String sql = "SELECT created_by as creatorId, COUNT(id) as caseCount FROM test_cases WHERE is_deleted = 0 AND created_by IS NOT NULL GROUP BY created_by ORDER BY caseCount DESC LIMIT 10";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TopCreatorVO vo = new TopCreatorVO();
                vo.setCreatorId(rs.getString("creatorId"));
                vo.setCaseCount(rs.getLong("caseCount"));
                result.add(vo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 获取本周的时间范围（周一 00:00:00 到 周日 23:59:59）的毫秒级时间戳
     */
    private long[] getCurrentWeekRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long weekStart = calendar.getTimeInMillis();

        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        long weekEnd = calendar.getTimeInMillis();

        return new long[]{weekStart, weekEnd};
    }
}