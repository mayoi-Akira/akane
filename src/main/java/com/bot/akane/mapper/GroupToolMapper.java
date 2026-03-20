package com.bot.akane.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bot.akane.model.entity.Tools;

@Mapper
public interface GroupToolMapper {

    List<Tools> selectAllTools();

    List<Tools> selectEnabledToolsByGroupId(@Param("groupId") Long groupId);

    Tools selectToolByCode(@Param("toolCode") String toolCode);

    List<String> selectExistingToolCodes(@Param("toolCodes") List<String> toolCodes);

    int insertGroupConfigIfAbsent(@Param("groupId") Long groupId);

    int insertToolIfAbsent(@Param("tool") Tools tool);

    int disableMappingsByGroupId(@Param("groupId") Long groupId);

    int upsertGroupToolMapping(
            @Param("groupId") Long groupId,
            @Param("toolCode") String toolCode,
            @Param("isEnabled") Boolean isEnabled);

    int enableMappingsByGroupId(@Param("groupId") Long groupId);

    int enableAllToolsForGroup(@Param("groupId") Long groupId);

    int enableAllMappingsForAllGroups();

    int enableAllToolsForAllGroups();
}
