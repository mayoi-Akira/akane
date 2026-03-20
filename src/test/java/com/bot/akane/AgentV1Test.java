package com.bot.akane;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.agent.examples.AgentV1;

@SpringBootTest
public class AgentV1Test {
    @Autowired
    @Qualifier("deepseek-chat")
    private ChatClient chatClient;

    @Test
    public void testAgentV1() {
        AgentV1 agent = new AgentV1(
                "TestAgent",
                "这是一个测试代理",
                "你是一个有用的助手，帮助用户完成任务。",
                chatClient,
                10,
                "test-session"
        );

        try {
            String userInput = "你好，你是谁？";

            String response = agent.chat(userInput);
            System.out.println("Agent回复：" + response);

        } catch (Exception e) {
            Assertions.fail("测试过程中发生异常: " + e.getMessage());
        }
    }
}
