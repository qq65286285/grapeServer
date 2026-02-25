package com.grape.grape.utils.scrcpy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务线程管理器
 */
public class TaskManager {
    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    // 存储所有启动的线程，key 为任务标识，value 为线程列表
    private static final Map<String, List<Thread>> threadMap = new ConcurrentHashMap<>();

    /**
     * 启动子线程
     *
     * @param key     任务标识
     * @param threads 要启动的线程
     */
    public static void startChildThread(String key, Thread... threads) {
        List<Thread> threadList = threadMap.computeIfAbsent(key, k -> new ArrayList<>());

        for (Thread thread : threads) {
            thread.start();
            threadList.add(thread);
            logger.info("启动线程: {} (任务: {})", thread.getName(), key);
        }
    }

    /**
     * 停止任务的所有线程
     *
     * @param key 任务标识
     */
    public static void stopTask(String key) {
        List<Thread> threadList = threadMap.remove(key);
        if (threadList != null) {
            for (Thread thread : threadList) {
                if (thread.isAlive()) {
                    thread.interrupt();
                    logger.info("停止线程: {} (任务: {})", thread.getName(), key);
                }
            }
        }
    }

    /**
     * 获取任务的线程列表
     *
     * @param key 任务标识
     * @return 线程列表
     */
    public static List<Thread> getThreads(String key) {
        return threadMap.get(key);
    }

    /**
     * 清理所有任务
     */
    public static void clear() {
        threadMap.forEach((key, threads) -> {
            threads.forEach(thread -> {
                if (thread.isAlive()) {
                    thread.interrupt();
                }
            });
        });
        threadMap.clear();
        logger.info("清理所有任务线程");
    }
}
