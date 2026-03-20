package com.bot.akane.service.impl;

import org.springframework.stereotype.Service;

import com.bot.akane.agent.AgentManager;
import com.bot.akane.service.GroupChat;
import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupChatImpl implements GroupChat{
    
    private final AgentManager agentManager;
    private final GroupToolService groupToolService;

    @Override
    public String chat(String groupId, String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "请输入消息内容。";
        }
        if (groupId == null || groupId.trim().isEmpty()) {
            return "群聊ID不能为空。";
        }
        String cleanGroupId = groupId.trim();
        String cleanUserInput = userInput.trim();
        
        try {
            // 初始化群聊工具配置：群不存在时自动创建，并默认启用全部工具。
            groupToolService.getToolsForGroup(cleanGroupId);
            return agentManager.chat(cleanGroupId, cleanUserInput);
        } catch (Exception e) {
            return "处理群聊消息失败：" + e.getMessage();
        }
        
    }
}
