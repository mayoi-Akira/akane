package com.bot.akane.agent;

import java.util.List;

import org.springframework.ai.chat.messages.Message;

/**
 * Agent 接口
 * 定义 Agent 的核心行为契约
 */
public interface AgentInterface {
    /**
     * 执行聊天
     * @param userInput 用户输入
     * @return AI 响应
     */
    String chat(String userInput);

    /**
     * 获取对话历史
     */
    List<Message> getConversationHistory();

    /**
     * 重置对话
     */
    void reset();

    /**
     * 获取当前状态
     */
    AgentState getState();

    /**
     * 获取 Agent 名称
     */
    String getName();
}
