package com.bot.akane.service;

public interface WeatherService {

    /**
     * 获取指定城市未来几天的天气信息
     *
     * @param cityName 城市名称
     * @param adm 所查城市的上级名称
     * @param days 查询未来几天的天气
     * @return 包含天气信息的字符串"
     */
    public String getWeatherForDays(String cityName, String adm, int days);

    /**
     * 获取指定城市未来几小时的天气信息
     *
     * @param cityName 城市名称
     * @param adm 所查城市的上级名称
     * @param hours 查询未来几小时的天气
     * @return 包含天气信息的字符串"
     */
    public String getWeatherForHours(String cityName, String adm, int hours);
}
