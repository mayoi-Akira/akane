package com.bot.akane.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bot.akane.model.entity.Tools;
import com.bot.akane.model.entity.GroupToolMapping;

@Mapper
public interface GroupToolMapper {

    List<Tools> selectAllTools();

    List<Tools> selectEnabledToolsByGroupId(@Param("groupId") String groupId);

    Tools selectToolByCode(@Param("toolCode") String toolCode);

    List<String> selectExistingToolCodes(@Param("toolCodes") List<String> toolCodes);

    int insertGroupConfigIfAbsent(@Param("groupId") String groupId);

    int insertToolIfAbsent(@Param("tool") Tools tool);

    int disableMappingsByGroupId(@Param("groupId") String groupId);

    int upsertGroupToolMapping(
            @Param("groupId") String groupId,
            @Param("toolCode") String toolCode,
            @Param("isEnabled") Boolean isEnabled);

    int enableMappingsByGroupId(@Param("groupId") String groupId);

    int enableAllToolsForGroup(@Param("groupId") String groupId);

    List<GroupToolMapping> selectGroupToolMappingsByGroupId(@Param("groupId") String groupId);
}
