package com.bot.akane.agent;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;

import com.bot.akane.agent.examples.AgentV2;


public class Agent extends AgentV2{
    public Agent(String name,
                 String description,
                 String systemPrompt,
                 ChatClient chatClient,
                 Integer maxMessages,
                 Integer maxSteps,
                 String sessionId,
                 List<ToolCallback> availableTools) {
        super(name, description, systemPrompt, chatClient, maxMessages, maxSteps, sessionId, availableTools);
    }
    

}
