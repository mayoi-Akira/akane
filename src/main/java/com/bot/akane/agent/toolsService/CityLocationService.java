package com.bot.akane.agent.toolsService;

public interface CityLocationService {
    
    /**
     * 通过api获取城市的经纬度信息
     *
     * @param cityName 城市名称
     * @param adm 所查城市的上级名称
     * @return 包含经纬度信息的字符串，格式为 "lon=xxx,lat=xxx"
     */
    String getCityLocationFromApi(String cityName, String adm);

    /**
     * 优先通过缓存获取城市的经纬度信息
     *
     * @param cityName 城市名称
     * @param adm 所查城市的上级名称
     * @return 包含经纬度信息的字符串，格式为 "lon=xxx,lat=xxx"
     */
    String getCityLocation(String cityName, String adm);
}
