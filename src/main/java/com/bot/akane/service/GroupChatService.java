package com.bot.akane.service;

public interface GroupChatService {
    /**
     * 聊天
     * @param groupId 群ID
     * @param userInput 用户输入的消息
     * @return 机器人的回复消息
     */
    String chat(String groupId, String userInput);
}
