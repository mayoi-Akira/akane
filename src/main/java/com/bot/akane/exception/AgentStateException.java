package com.bot.akane.exception;

import com.bot.akane.agent.AgentState;

/**
 * Agent 状态异常
 * 用于表示 Agent 状态转换或状态检查失败
 */
public class AgentStateException extends AgentException {
    private final AgentState currentState;
    private final AgentState expectedState;

    public AgentStateException(AgentState currentState, String errorMessage) {
        super("AGENT_STATE_ERROR", errorMessage);
        this.currentState = currentState;
        this.expectedState = null;
    }

    public AgentStateException(AgentState currentState, AgentState expectedState, String errorMessage) {
        super("AGENT_STATE_ERROR", errorMessage);
        this.currentState = currentState;
        this.expectedState = expectedState;
    }

    public AgentState getCurrentState() {
        return currentState;
    }

    public AgentState getExpectedState() {
        return expectedState;
    }
}
