package com.bot.akane.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bot.akane.model.entity.Tool;
import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.model.entity.GroupToolConfig;

@Mapper
public interface GroupToolMapper {

    /**
     * 查询所有工具
     */
    List<Tool> selectAllTools();

    /**
     * 查询指定群组启用的工具
     */
    List<Tool> selectEnabledToolsByGroupId(@Param("groupId") String groupId);

    /**
     * 根据工具代码查询工具
     */
    Tool selectToolByCode(@Param("toolCode") String toolCode);

    /**
     * 批量查询存在的工具代码
     */
    List<String> selectExistingToolCodes(@Param("toolCodes") List<String> toolCodes);

    /**
     * 创建群聊配置（如果不存在）
     */
    int insertGroupIfAbsent(@Param("groupId") String groupId);

    /**
     * 创建工具（如果不存在）
     */
    int insertToolIfAbsent(@Param("tool") Tool tool);

    /**
     * 禁用群组的所有工具
     */
    int disableAllToolsForGroup(@Param("groupId") String groupId);

    /**
     * 创建或更新群组-工具配置
     */
    int upsertGroupToolConfig(
            @Param("groupId") String groupId,
            @Param("toolCode") String toolCode,
            @Param("status") ToolType status);

    /**
     * 启用群组的所有工具
     */
    int enableAllToolsForGroup(@Param("groupId") String groupId);

    /**
     * 查询群组的工具配置列表
     */
    List<GroupToolConfig> selectGroupToolConfigsByGroupId(@Param("groupId") String groupId);

    /**
     * 查询所有群组ID
     */
    List<String> selectAllGroupIds();
}
