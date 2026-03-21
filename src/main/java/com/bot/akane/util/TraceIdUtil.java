package com.bot.akane.util;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * 链路追踪工具类
 * 用于在分布式系统中追踪请求的完整链路
 */
public class TraceIdUtil {
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SESSION_ID_KEY = "sessionId";
    private static final String GROUP_ID_KEY = "groupId";

    /**
     * 生成新的追踪 ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置追踪 ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 获取追踪 ID
     */
    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = generateTraceId();
            setTraceId(traceId);
        }
        return traceId;
    }

    /**
     * 设置会话 ID
     */
    public static void setSessionId(String sessionId) {
        MDC.put(SESSION_ID_KEY, sessionId);
    }

    /**
     * 获取会话 ID
     */
    public static String getSessionId() {
        return MDC.get(SESSION_ID_KEY);
    }

    /**
     * 设置群组 ID
     */
    public static void setGroupId(String groupId) {
        MDC.put(GROUP_ID_KEY, groupId);
    }

    /**
     * 获取群组 ID
     */
    public static String getGroupId() {
        return MDC.get(GROUP_ID_KEY);
    }

    /**
     * 清空所有追踪信息
     */
    public static void clear() {
        MDC.clear();
    }
}
