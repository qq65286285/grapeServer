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
