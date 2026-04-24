package com.grape.grape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置类
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 配置异步任务执行器
     * @return 线程池执行器
     */
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量
        executor.setQueueCapacity(50);
        // 线程名称前缀
        executor.setThreadNamePrefix("async-testcase-");
        // 线程空闲时间
        executor.setKeepAliveSeconds(60);
        // 初始化
        executor.initialize();
        return executor;
    }
}