package com.bot.akane.agent;

import com.bot.akane.exception.AgentStateException;
import lombok.extern.slf4j.Slf4j;

/**
 * Agent 状态转换管理器
 * 负责验证和管理 Agent 的状态转换
 */
@Slf4j
public class AgentStateManager {
    private AgentState currentState;

    public AgentStateManager() {
        this.currentState = AgentState.IDLE;
    }

    /**
     * 转换到指定状态
     * @param newState 新状态
     * @throws AgentStateException 如果状态转换非法
     */
    public synchronized void transitionTo(AgentState newState) {
        if (!isValidTransition(currentState, newState)) {
            throw new AgentStateException(
                    currentState,
                    newState,
                    String.format("非法的状态转换: %s -> %s", currentState, newState)
            );
        }
        log.debug("Agent state transition: {} -> {}", currentState, newState);
        this.currentState = newState;
    }

    /**
     * 获取当前状态
     */
    public synchronized AgentState getCurrentState() {
        return currentState;
    }

    /**
     * 检查是否处于指定状态
     */
    public synchronized boolean isInState(AgentState state) {
        return currentState == state;
    }

    /**
     * 检查是否处于空闲状态
     */
    public synchronized boolean isIdle() {
        return currentState == AgentState.IDLE;
    }

    /**
     * 重置为空闲状态
     */
    public synchronized void reset() {
        this.currentState = AgentState.IDLE;
        log.debug("Agent state reset to IDLE");
    }

    /**
     * 验证状态转换是否合法
     */
    private boolean isValidTransition(AgentState from, AgentState to) {
        // 定义合法的状态转换
        return switch (from) {
            case IDLE -> to == AgentState.THINKING;
            case THINKING -> to == AgentState.EXECUTING || to == AgentState.FINISHED || to == AgentState.ERROR;
            case EXECUTING -> to == AgentState.THINKING || to == AgentState.FINISHED || to == AgentState.ERROR;
            case FINISHED, ERROR -> to == AgentState.IDLE;
            case PLANNING -> to == AgentState.THINKING || to == AgentState.ERROR;
        };
    }
}
