package com.bot.akane;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.service.CityLocationService;

@SpringBootTest
public class LocationTest {
    @Autowired
    private CityLocationService cityLocationService;

    @Test
    public void testGetCityLocation() {
        String cityName = "金州区";
        String adm = "";

        String location = cityLocationService.getCityLocation(cityName, adm);
        System.out.println("城市经纬度信息: " + location);
    }
}
