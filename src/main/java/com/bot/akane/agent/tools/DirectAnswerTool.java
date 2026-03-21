package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;

import com.bot.akane.agent.toolSettings.ToolDefaultType;
import com.bot.akane.agent.toolSettings.ToolInterface;

public class DirectAnswerTool implements ToolInterface {

    @Override
    public String getName() {
        return "directAnswer";
    }

    @Override
    public String getDescription() {
        return "当用户的请求不需要执行操作时调用此工具，用以直接返回自然语言回答。";
    }

    @Override
    public ToolDefaultType getType() {
        return ToolDefaultType.FORCE;
    }

    @Tool(name = "directAnswer",description = "用于直接回答用户问题，适用于无需生成任务计划或调用其他工具的场景。")
    public void directAnswer() {}
}
