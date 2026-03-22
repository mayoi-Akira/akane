package com.bot.akane.service;

public interface GroupChatService {
    /**
     * 聊天
     * @param groupId 群ID
     * @param userId 用户ID
     * @param messageId 消息ID
     * @param userMessage 用户输入的消息
     * @return 机器人的回复消息
     */
    String chat(String groupId, String userId, String messageId, String userMessage);
}
