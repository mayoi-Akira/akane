package com.bot.akane.agent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.tools.ToolInterface;
import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgentManager {
    private final ApplicationContext applicationContext;
    private final GroupToolService groupToolService;
    private final Map<String, Agent> agentCache = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("deepseek-chat")
    private ChatClient chatClient;

    @Value("${system-prompt: 你是一个AI助手，现在处于QQ聊天环境，请根据内容调用合适的工具并作出合适的回答。}")
    private String systemPrompt;

    @Value("${agent.name:akane}")
    private String agentName;
    
    public String chat(String groupId, String userInput) {
        Agent agent = agentCache.computeIfAbsent(groupId, id -> createNewAgent(id));
        return agent.chat(userInput);
    }

    private Agent createNewAgent(String groupId) {
        List<String> enabledToolCodes = groupToolService.getToolsForGroup(groupId);
        Object[] activeToolObjects = applicationContext.getBeansOfType(ToolInterface.class)
                .values()
                .stream()
            .filter(tool -> enabledToolCodes.contains(tool.getName()))
                .toArray();
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(activeToolObjects)
                .build()
                .getToolCallbacks();

        Agent agent = new Agent(
                agentName + "-" + groupId,
                "Agent for Group " + groupId,
                systemPrompt,
                chatClient,
                10,
                20,
                "session-" + groupId,
                Arrays.asList(toolCallbacks)
        );
        return agent;
    }
}
