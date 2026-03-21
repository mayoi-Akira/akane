package com.bot.akane.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bot.akane.agent.AgentState;

/**
 * 异常体系单元测试
 */
public class ExceptionTest {

    @Test
    public void testAgentException() {
        AgentException exception = new AgentException("TEST_ERROR", "Test error message");
        assertEquals("TEST_ERROR", exception.getErrorCode());
        assertEquals("Test error message", exception.getErrorMessage());
    }

    @Test
    public void testAgentExceptionWithCause() {
        Exception cause = new RuntimeException("Cause");
        AgentException exception = new AgentException("TEST_ERROR", "Test error", cause);
        assertEquals("TEST_ERROR", exception.getErrorCode());
        assertEquals("Test error", exception.getErrorMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testToolExecutionException() {
        ToolExecutionException exception = new ToolExecutionException("MyTool", "Tool failed");
        assertEquals("TOOL_EXECUTION_ERROR", exception.getErrorCode());
        assertEquals("MyTool", exception.getToolName());
    }

    @Test
    public void testToolExecutionExceptionWithArguments() {
        ToolExecutionException exception = new ToolExecutionException(
                "MyTool",
                "{\"param\":\"value\"}",
                "Tool failed",
                new RuntimeException("Cause")
        );
        assertEquals("MyTool", exception.getToolName());
        assertEquals("{\"param\":\"value\"}", exception.getToolArguments());
    }

    @Test
    public void testAgentStateException() {
        AgentStateException exception = new AgentStateException(
                AgentState.THINKING,
                "Invalid state"
        );
        assertEquals("AGENT_STATE_ERROR", exception.getErrorCode());
        assertEquals(AgentState.THINKING, exception.getCurrentState());
        assertNull(exception.getExpectedState());
    }

    @Test
    public void testAgentStateExceptionWithExpected() {
        AgentStateException exception = new AgentStateException(
                AgentState.THINKING,
                AgentState.IDLE,
                "Invalid state transition"
        );
        assertEquals(AgentState.THINKING, exception.getCurrentState());
        assertEquals(AgentState.IDLE, exception.getExpectedState());
    }

    @Test
    public void testExceptionHierarchy() {
        AgentException agentException = new AgentException("ERROR", "message");
        assertTrue(agentException instanceof RuntimeException);

        ToolExecutionException toolException = new ToolExecutionException("Tool", "failed");
        assertTrue(toolException instanceof AgentException);

        AgentStateException stateException = new AgentStateException(AgentState.IDLE, "error");
        assertTrue(stateException instanceof AgentException);
    }
}
