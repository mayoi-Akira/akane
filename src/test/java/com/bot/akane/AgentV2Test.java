package com.bot.akane;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.agent.examples.AgentV2;
import com.bot.akane.agent.tools.DateTimeTool;
import com.bot.akane.agent.tools.WeatherTools;

@SpringBootTest
public class AgentV2Test {
    @Autowired
    @Qualifier("deepseek-chat")
    private ChatClient chatClient;


    @Autowired
    private DateTimeTool dateTimeTool;

    @Autowired
    private WeatherTools weatherTools;

    @Test
    public void testAgentV2() {
        String userInput = "今晚大连金州会下雨吗？";
        String prompt = "你是一个智能助手，能够提供当前日期和时间，以及天气预报。";
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(dateTimeTool, weatherTools)
                .build()
                .getToolCallbacks();
        AgentV2 agent = new AgentV2(
                "test-agent-v2",
                "测试",
                prompt,
                chatClient,
                20,
                20,
                "test-session-v2",
                Arrays.asList(toolCallbacks)
        );

        String response = agent.chat(userInput);
        System.out.println("User Input: " + userInput + "\n\n");
        System.out.println("Agent Response: " + response + "\n\n");
        System.out.println("对话长度：" + agent.getConversationHistory().size());

    }
}
