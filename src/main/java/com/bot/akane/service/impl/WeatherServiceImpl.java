package com.bot.akane.service.impl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bot.akane.service.CityLocationService;
import com.bot.akane.service.WeatherService;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WeatherServiceImpl implements WeatherService {

    private final CityLocationService cityLocationService;
    
    @Value("${weather.api-host}")
    private String apiHost;
    @Value("${weather.api-key}")
    private String apiKey;
    
    private String getDayParam(int day){
        if(day <= 3) return "3d";
        return "7d";
    }

    private String getWeatherData(String cityName, String adm, String timeFrame, int length) {
        String location = cityLocationService.getCityLocation(cityName, adm);
        if (location == null || location.isEmpty()) {
            return "无法获取城市的经纬度信息，请检查城市名称和上级名称是否正确。";
        }
        String url = apiHost + "/v7/weather/" + timeFrame + "?location=" + location;

        try{
            String responseBody = HttpRequest.get(url)
                    .header("X-QW-Api-Key", apiKey)
                    .header("Accept-Encoding", "gzip")
                    .execute()
                    .body();
            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
            if(!"200".equals(jsonResponse.getStr("code"))){
                return "天气API返回错误: " + jsonResponse.getStr("message");
            }
            JSONArray weather = null;
            if (jsonResponse.containsKey("hourly")) {
                weather = jsonResponse.getJSONArray("hourly");
            } else if (jsonResponse.containsKey("daily")) {
                weather = jsonResponse.getJSONArray("daily");
            }
            if(weather == null){
                return "未获取到天气数据。";
            }
            JSONArray weatherResult = weather.stream().limit(length).collect(Collectors.toCollection(JSONArray::new));;
            return weatherResult.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "通过API获取天气信息失败。";
        }
        
    }
    @Override
    public String getWeatherForDays(String cityName, String adm, int days) {
        if(days <= 0 || days > 7){
            return "只允许查询1到7天的天气。";
        }
        return getWeatherData(cityName, adm, getDayParam(days), days);
    }
    @Override
    public String getWeatherForHours(String cityName, String adm, int hours) {
        if(hours <= 0 || hours > 24){
            return "只允许查询1到24小时内的天气。";
        }
        return getWeatherData(cityName, adm, "24h", hours);
    }
    
}
