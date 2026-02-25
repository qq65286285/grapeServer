package com.grape.grape.utils.common;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 投屏线程映射管理
 * 用于存储 WebSocket 会话与投屏线程的对应关系
 */
public class ScreenMap {
    private static final Map<Session, Thread> map = new ConcurrentHashMap<>();

    public static Map<Session, Thread> getMap() {
        return map;
    }

    public static void put(Session session, Thread thread) {
        map.put(session, thread);
    }

    public static Thread get(Session session) {
        return map.get(session);
    }

    public static void remove(Session session) {
        map.remove(session);
    }

    public static void clear() {
        map.clear();
    }
}
