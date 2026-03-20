package com.bot.akane.agent.examples;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bot.akane.agent.AgentState;

public class AgentV1 {
    // 名称
    protected String name;
    // 描述
    protected String description;
    // 系统提示词
    protected String systemPrompt;
    // ChatClient 实例
    protected ChatClient chatClient;
    // 聊天记忆
    protected ChatMemory chatMemory;
    // 状态
    protected AgentState agentState;
    // 会话 ID（用于内存管理）
    protected String sessionId;
    
    @Value("${DEFAULT_MAX_MESSAGES:20}")
    private static Integer DEFAULT_MAX_MESSAGES;

    public AgentV1() {}
    
    public AgentV1(String name,
                      String description,
                      String systemPrompt,
                      ChatClient chatClient,
                      Integer maxMessages,
                      String sessionId) {
        this.name = name;
        this.description = description;
        this.systemPrompt = systemPrompt;
        this.chatClient = chatClient;
        this.sessionId = sessionId != null ? sessionId : "default-session";
        this.agentState = AgentState.IDLE;
        
        // 初始化聊天记忆
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(maxMessages != null ? maxMessages : DEFAULT_MAX_MESSAGES)
                .build();
        
        // 添加系统提示词
        if (StringUtils.hasLength(systemPrompt)) {
            this.chatMemory.add(this.sessionId, new SystemMessage(systemPrompt));
        }
    }

    public String chat(String userInput){
        Assert.notNull(userInput, "用户输入不能为空");
        
        if(this.agentState != AgentState.IDLE) {
            throw new IllegalStateException("Agent非空闲，当前状态不允许聊天");
        }
        try{
            this.agentState = AgentState.THINKING;

            UserMessage userMessage = new UserMessage(userInput);
            //将用户输入添加到记忆中
            this.chatMemory.add(this.sessionId, userMessage);
            
            //获取聊天历史
            var history = this.chatMemory.get(this.sessionId);
            //构建提示词
            Prompt prompt = Prompt.builder()
                    .messages(history)
                    .build();
            //调用 ChatClient 进行对话
            ChatResponse response = this.chatClient
                    .prompt(prompt)
                    .call()
                    .chatResponse();
            
            Assert.notNull(response, "ChatResponse 不能为空");
            //获取模型回复
            AssistantMessage assistantMessage = response.getResult().getOutput();
            //将模型回复添加到记忆中
            this.chatMemory.add(this.sessionId, assistantMessage);
            
            this.agentState = AgentState.FINISHED;
            
            return assistantMessage.getText();
        }catch(Exception e){
            this.agentState = AgentState.ERROR;
            throw e;
        }finally {
            this.agentState = AgentState.IDLE;
        }
    }

      /**
     * 获取当前对话历史
     */
    public List<Message> getConversationHistory() {
        return chatMemory.get(sessionId);
    }
    
    /**
     * 重置对话历史
     */
    public void reset() {
        chatMemory.clear(sessionId);
        if (StringUtils.hasLength(systemPrompt)) {
            chatMemory.add(sessionId, new SystemMessage(systemPrompt));
        }
        agentState = AgentState.IDLE;
    }
    
    @Override
    public String toString() {
        return "JChatMindV1 {" +
                "name = " + name + ",\n" +
                "description = " + description + ",\n" +
                "systemPrompt = " + systemPrompt + "}";
    }
}
