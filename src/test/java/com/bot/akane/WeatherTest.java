package com.bot.akane;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.service.CityLocationService;
import com.bot.akane.service.WeatherService;

@SpringBootTest
public class WeatherTest {
    @Autowired
    private CityLocationService cityLocationService;
    @Autowired
    private WeatherService weatherService;

    @Test
    public void testGetCityLocation() {
        String cityName = "金州区";
        String adm = "";

        String location = cityLocationService.getCityLocation(cityName, adm);
        System.out.println("城市经纬度信息: " + location);
    }

    @Test
    public void testGetWeatherForDays() {
        String cityName = "金州区";
        String adm = "";
        int days = 3;
        String weatherInfo = weatherService.getWeatherForDays(cityName, adm, days);
        System.out.println("未来" + days + "天的天气信息: " + weatherInfo);
    }

    @Test
    public void testGetWeatherForHours() {
        String cityName = "金州区";
        String adm = "";
        String weatherInfo = weatherService.getWeatherForHours(cityName, adm, 2);
        System.out.println("未来2小时的天气信息: " + weatherInfo);
    }
}
