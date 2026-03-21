package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;

import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.agent.toolSettings.ToolInterface;

@Component
public class DateTimeTool implements ToolInterface {

    @Override
    public String getName() {
        return "DateTimeTool";
    }

    @Override
    public String getDescription() {
        return "提供日期和时间相关功能的工具，包含获取当前日期、当前时间、当前日期和时间等功能。";
    }

    @Override
    public ToolType getType() {
        return ToolType.ENABLE;
    }

    @Override
    public String getCode() {
        return "3";
    }

    @Tool(name = "getCurrentDate", description = "获取当前日期，格式为yyyy-MM-dd")
    public String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }
    @Tool(name = "getCurrentTime", description = "获取当前时间，格式为HH:mm:ss")
    public String getCurrentTime() {
        return java.time.LocalTime.now().withNano(0).toString();
    }
    @Tool(name = "getCurrentDateTime", description = "获取当前日期和时间，格式为yyyy-MM-dd HH:mm:ss")
    public String getCurrentDateTime() {
        return java.time.LocalDateTime.now().withNano(0).toString().replace('T', ' ');
    }

}
