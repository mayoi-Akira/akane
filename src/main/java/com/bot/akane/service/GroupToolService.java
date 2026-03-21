package com.bot.akane.service;

import java.util.List;

import com.bot.akane.model.entity.GroupToolConfig;

public interface GroupToolService {
    /**
     * 获取全部可用的工具列表
     * @return 包含工具信息的字符串列表，每个字符串包含工具名称
     */
    List<String> getAvailableTools();

    /**
     * 获取指定群聊的工具列表
     * @param groupId 群聊ID
     * @return 包含工具信息的字符串列表，每个字符串包含工具名称和描述
     */
    List<String> getToolsForGroup(String groupId);

    /**
     * 更新指定群聊的工具列表
     * @param groupId 群聊ID
     * @param toolNames 工具名称列表
     * @param toEnable 是否启用这些工具（true表示启用，false表示禁用）
     * @return 更新结果的字符串，包含成功或失败的信息
     */
    String updateToolsForGroup(String groupId, String[] toolNames, Boolean toEnable);

    /**
     * 获取指定群聊的所有工具配置信息
     * @param groupId 群聊ID
     * @return 工具配置列表
     */
    List<GroupToolConfig> getGroupToolMappings(String groupId);
}
