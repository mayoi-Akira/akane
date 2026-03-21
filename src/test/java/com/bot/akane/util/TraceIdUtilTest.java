package com.bot.akane.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/**
 * TraceIdUtil 单元测试
 */
public class TraceIdUtilTest {

    @BeforeEach
    public void setUp() {
        TraceIdUtil.clear();
    }

    @Test
    public void testGenerateTraceId() {
        String traceId = TraceIdUtil.generateTraceId();
        assertNotNull(traceId);
        assertEquals(32, traceId.length()); // UUID without hyphens
    }

    @Test
    public void testSetAndGetTraceId() {
        String traceId = "test-trace-id-123";
        TraceIdUtil.setTraceId(traceId);
        assertEquals(traceId, TraceIdUtil.getTraceId());
    }

    @Test
    public void testAutoGenerateTraceId() {
        // First call should generate a new trace ID
        String traceId1 = TraceIdUtil.getTraceId();
        assertNotNull(traceId1);

        // Second call should return the same trace ID
        String traceId2 = TraceIdUtil.getTraceId();
        assertEquals(traceId1, traceId2);
    }

    @Test
    public void testSetAndGetSessionId() {
        String sessionId = "session-123";
        TraceIdUtil.setSessionId(sessionId);
        assertEquals(sessionId, TraceIdUtil.getSessionId());
    }

    @Test
    public void testSetAndGetGroupId() {
        String groupId = "group-456";
        TraceIdUtil.setGroupId(groupId);
        assertEquals(groupId, TraceIdUtil.getGroupId());
    }

    @Test
    public void testClear() {
        TraceIdUtil.setTraceId("trace-123");
        TraceIdUtil.setSessionId("session-123");
        TraceIdUtil.setGroupId("group-123");

        TraceIdUtil.clear();

        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("sessionId"));
        assertNull(MDC.get("groupId"));
    }

    @Test
    public void testMultipleContexts() {
        // Context 1
        TraceIdUtil.setTraceId("trace-1");
        TraceIdUtil.setSessionId("session-1");
        TraceIdUtil.setGroupId("group-1");

        assertEquals("trace-1", TraceIdUtil.getTraceId());
        assertEquals("session-1", TraceIdUtil.getSessionId());
        assertEquals("group-1", TraceIdUtil.getGroupId());

        // Clear and set new context
        TraceIdUtil.clear();
        TraceIdUtil.setTraceId("trace-2");
        TraceIdUtil.setSessionId("session-2");
        TraceIdUtil.setGroupId("group-2");

        assertEquals("trace-2", TraceIdUtil.getTraceId());
        assertEquals("session-2", TraceIdUtil.getSessionId());
        assertEquals("group-2", TraceIdUtil.getGroupId());
    }

    @Test
    public void testThreadIsolation() throws InterruptedException {
        TraceIdUtil.setTraceId("main-trace");
        TraceIdUtil.setSessionId("main-session");

        Thread thread = new Thread(() -> {
            // Thread should have its own MDC context
            assertNull(TraceIdUtil.getSessionId());
            
            TraceIdUtil.setTraceId("thread-trace");
            TraceIdUtil.setSessionId("thread-session");
            
            assertEquals("thread-trace", TraceIdUtil.getTraceId());
            assertEquals("thread-session", TraceIdUtil.getSessionId());
        });

        thread.start();
        thread.join();

        // Main thread context should be unchanged
        assertEquals("main-trace", TraceIdUtil.getTraceId());
        assertEquals("main-session", TraceIdUtil.getSessionId());
    }
}
