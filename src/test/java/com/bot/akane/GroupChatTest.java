package com.bot.akane;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.service.GroupChatService;

@SpringBootTest
public class GroupChatTest {
    @Autowired
    private GroupChatService groupChat;

    @Test
    public void testGroupChat() {
        String response = groupChat.chat("609494853", "", "", "明天大连天气怎么样？");
        System.out.println("机器人回复609494853: " + response);

        response = groupChat.chat("11111", "", "", "现在几点了？");
        System.out.println("机器人回复11111: " + response);

        response = groupChat.chat("609494853", "", "", "中山区的天气怎么样？");
        System.out.println("机器人回复609494853: " + response);
    }
}
