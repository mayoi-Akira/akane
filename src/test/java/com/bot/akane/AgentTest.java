package com.bot.akane;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.agent.Agent;
import com.bot.akane.agent.tools.DateTimeTool;
import com.bot.akane.agent.tools.EmailTool;
import com.bot.akane.agent.tools.WeatherTools;

@SpringBootTest
public class AgentTest {
    @Autowired
    @Qualifier("deepseek-chat")
    private ChatClient chatClient;


    @Autowired
    private DateTimeTool dateTimeTool;

    @Autowired
    private WeatherTools weatherTools;

    @Autowired
    private EmailTool emailTool;

    @Value("${Agent.system-prompt}")
    private String prompt;

    @Value("${Agent.think-prompt:}")
    private String thinkPrompt;

    @Test
    public void testAgentV2() {
        String userInput = "给akane36@163.com发一封邮件，内容是大连市金州区明天的天气预报。";
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(dateTimeTool, weatherTools,emailTool)
                .build()
                .getToolCallbacks();
        Agent agent = new Agent(
                "test-agent-v2",
                "测试",
                prompt,
            thinkPrompt,
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
