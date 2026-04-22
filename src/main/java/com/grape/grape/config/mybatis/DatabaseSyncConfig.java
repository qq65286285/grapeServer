package com.grape.grape.config.mybatis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库结构同步配置类
 * 用于在应用启动时检查并同步数据库结构
 */
@Configuration
public class DatabaseSyncConfig {

    @Value("${spring.jpa.auto-sync-schema:false}")
    private boolean autoSyncSchema;

    private final DataSource dataSource;

    public DatabaseSyncConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public CommandLineRunner syncDatabaseSchema() {
        return args -> {
            if (autoSyncSchema) {
                System.out.println("=== 开始同步数据库结构 ===");
                try {
                    // 检查数据库连接
                    try (Connection connection = dataSource.getConnection()) {
                        System.out.println("✅ 数据库连接成功");
                        
                        // 打印数据库信息
                        System.out.println("数据库URL: " + connection.getMetaData().getURL());
                        System.out.println("数据库用户名: " + connection.getMetaData().getUserName());
                        
                        // 检查是否存在grape数据库
                        try (Statement statement = connection.createStatement()) {
                            ResultSet resultSet = statement.executeQuery("SHOW DATABASES LIKE 'grape'");
                            if (resultSet.next()) {
                                System.out.println("✅ grape数据库已存在");
                                
                                // 修改test_plan_case_snapshot表的executor_id列类型为VARCHAR(36)
                                try {
                                    String alterTableSQL = "ALTER TABLE test_plan_case_snapshot MODIFY COLUMN executor_id VARCHAR(36)";
                                    statement.executeUpdate(alterTableSQL);
                                    System.out.println("✅ 成功修改test_plan_case_snapshot表的executor_id列为VARCHAR(36)");
                                } catch (SQLException e) {
                                    // 如果列已经是VARCHAR类型，会抛出异常，这里捕获并忽略
                                    System.out.println("⚠️ 修改executor_id列类型时出现异常: " + e.getMessage());
                                }

                                // 创建test_plan_execute_step_attachment表
                                try {
                                    String createTableSQL = "CREATE TABLE IF NOT EXISTS test_plan_execute_step_attachment (" +
                                            "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                                            "execute_step_id BIGINT," +
                                            "execute_id BIGINT," +
                                            "file_name VARCHAR(255)," +
                                            "file_size BIGINT," +
                                            "file_type VARCHAR(50)," +
                                            "mime_type VARCHAR(100)," +
                                            "storage_type INT," +
                                            "storage_path VARCHAR(255)," +
                                            "file_url VARCHAR(512)," +
                                            "file_md5 VARCHAR(32)," +
                                            "preview_url VARCHAR(512)," +
                                            "thumbnail_url VARCHAR(512)," +
                                            "sort_order INT," +
                                            "remark VARCHAR(255)," +
                                            "created_by BIGINT," +
                                            "created_at BIGINT," +
                                            "updated_by BIGINT," +
                                            "updated_at BIGINT," +
                                            "is_deleted INT DEFAULT 0" +
                                            ")";
                                    statement.executeUpdate(createTableSQL);
                                    System.out.println("✅ 成功创建test_plan_execute_step_attachment表");
                                } catch (SQLException e) {
                                    // 如果表已经存在，会抛出异常，这里捕获并忽略
                                    System.out.println("⚠️ 创建test_plan_execute_step_attachment表时出现异常: " + e.getMessage());
                                }

                                // 删除test_plan_case_snapshot_step表中的actual_result字段
                                try {
                                    String alterTableSQL = "ALTER TABLE test_plan_case_snapshot_step DROP COLUMN IF EXISTS actual_result";
                                    statement.executeUpdate(alterTableSQL);
                                    System.out.println("✅ 成功删除test_plan_case_snapshot_step表中的actual_result字段");
                                } catch (SQLException e) {
                                    // 如果字段不存在，会抛出异常，这里捕获并忽略
                                    System.out.println("⚠️ 删除actual_result字段时出现异常: " + e.getMessage());
                                }

                                // 为test_plan_execute_step表添加snapshot_step_id字段
                                try {
                                    String alterTableSQL = "ALTER TABLE test_plan_execute_step ADD COLUMN snapshot_step_id BIGINT";
                                    statement.executeUpdate(alterTableSQL);
                                    System.out.println("✅ 成功为test_plan_execute_step表添加snapshot_step_id字段");
                                } catch (SQLException e) {
                                    // 如果字段已经存在，会抛出异常，这里捕获并忽略
                                    System.out.println("⚠️ 添加snapshot_step_id字段时出现异常: " + e.getMessage());
                                }
                            } else {
                                System.out.println("❌ grape数据库不存在");
                            }
                        }
                    }
                    
                    // 这里可以添加实际的数据库结构同步逻辑
                    // 例如，执行SQL脚本创建表结构
                    
                    System.out.println("✅ 数据库结构同步成功");
                } catch (Exception e) {
                    System.out.println("❌ 数据库结构同步失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ 数据库结构同步已禁用");
            }
        };
    }
}
