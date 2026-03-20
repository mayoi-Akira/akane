package com.bot.akane;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AkaneApplicationTests {
    @Value("${agent.system-prompt}")
    private String systemPrompt;
    @Value("${agent.think-prompt}")
    private String thinkPrompt;
    @Test
    void contextLoads() {
        System.out.println(1);
        System.out.println(systemPrompt);
        System.out.println(thinkPrompt);
    }

}
