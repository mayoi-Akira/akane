package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.agent.toolsService.NowCoderToolsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NowCoderTools implements ToolInterface {
    
    private final NowCoderToolsService nowCoderToolsService;

    @Override
    public String getName() {
        return "nowcoder_tools";
    }

    @Override
    public String getDescription() {
        return "牛客竞赛相关的工具集合";
    }

    @Override
    public ToolType getType() {
        return ToolType.ENABLE;
    }

    @Override
    public String getCode() {
        return "7";
    }

    @Tool(name = "getNowCoderContestInfo", description = "获取牛客网竞赛信息，参数为用户牛客ID")
    public String getNowCoderContestInfo(String userId) {
        return nowCoderToolsService.getNowCoderContestInfo(userId);
    }
    @Tool(name = "getNowCoderContestRecord", description = "获取牛客网竞赛记录，参数为用户牛客ID、记录数量、是否仅返回rated记录；recordCount传入-1表示查询全部记录")
    public String getNowCoderContestRecord(String userId,
        @ToolParam(description = "需要查询的记录数量，传入-1表示查询全部记录，若用户未指明数量设为10") int recordCount,
        @ToolParam(description = "是否仅返回计rating的比赛记录，true表示过滤掉ratingStr为不计的记录") boolean onlyRated) {
        return nowCoderToolsService.getNowCoderContestRecord(userId, recordCount, onlyRated);
    }

}
