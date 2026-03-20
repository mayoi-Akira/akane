package com.bot.akane.service;

import java.util.List;

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
    * 获取指定工具的详细信息
    * @param toolName 工具名称
    * @return 包含工具详细信息的字符串，包括名称、描述和使用方法
    */
    String getToolDetails(String toolName);

    /**
     * 更新指定群聊的工具列表
     * @param groupId 群聊ID
     * @param toolNames 工具名称列表
     * @return 更新结果的字符串，包含成功或失败的信息
     */
    String updateToolsForGroup(String groupId, String[] toolNames);
}
