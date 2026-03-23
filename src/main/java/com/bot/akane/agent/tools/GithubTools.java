package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.agent.toolsService.GithubToolsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubTools implements ToolInterface {

    private final GithubToolsService githubToolsService;

    @Override
    public String getName() {
        return "GitHubTools";
    }

    @Override
    public String getDescription() {
        return "提供部分与GitHub相关的工具集合，包含但不限于仓库信息查询、提交记录查询等功能。";
    }

    @Override
    public ToolType getType() {
        return ToolType.ENABLE;
    }

    @Override
    public String getCode() {
        return "6";
    }

    @Tool(name = "getRepoInfo", description = "获取某用户的GitHub仓库信息，参数为GitHub用户名")
    public String getRepoInfo(String username) {
        return githubToolsService.getRepoInfo(username);
    }
}
