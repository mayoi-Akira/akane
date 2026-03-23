package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.agent.toolSettings.ToolInterface;

// @Component
public class TerminateTool implements ToolInterface {

    @Override
    public String getName() {
        return "terminate";
    }

    @Override
    public String getDescription() {
        return "跳出 Agent Loop 的工具";
    }

    @Override
    public ToolType getType() {
        return ToolType.FORCE;
    }

    @Override
    public String getCode() {
        return "1";
    }

    // @Tool(name = "terminate", description = "如果你觉得当前所有的任务已经执行完毕了，就执行这个工具调用")
    public void terminate() {}
}
