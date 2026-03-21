package com.bot.akane.agent.tools;

import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolsService.WeatherService;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeatherTools implements ToolInterface {

    private final WeatherService weatherService;

    @Override
    public String getName() {
        return "WeatherTools";
    }

    @Override
    public String getDescription() {
        return "提供天气查询功能的工具，包含获取未来几天和未来几小时的天气信息，注意参数中上级城市名称（adm）可为空。";
    }

    @Override
    public ToolDefaultType getType() {
        return ToolDefaultType.ENABLE;
    }

    @Tool(name = "getWeatherForDays", description = "获取指定城市未来几天的天气信息，参数分别为城市名称、上级城市名称（可为空）和未来天数")
    public String getWeatherForDays(
        @ToolParam(description = "城市名称，例如：中山区")String cityName,
        @ToolParam(description = "上级城市名称，例如：大连。若用户未提供可为空")String adm,
        @ToolParam(description = "未来天数，值为1代表今天，为2代表明天，以此类推")Integer days) {
        return weatherService.getWeatherForDays(cityName, adm, days);
    }

    @Tool(name = "getWeatherForHours", description = "获取指定城市未来几小时的天气信息，参数分别为城市名称、上级城市名称（可为空）和未来小时数")
    public String getWeatherForHours(
        @ToolParam(description = "城市名称，例如：中山区")String cityName,
        @ToolParam(description = "上级城市名称，例如：大连，若用户未提供可为空")String adm,
        @ToolParam(description = "未来小时数，例如：6")Integer hours) {
        return weatherService.getWeatherForHours(cityName, adm, hours);
    }
}
