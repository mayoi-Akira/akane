package com.bot.akane.agent.toolsService.impl;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bot.akane.agent.toolsService.CityLocationService;

@Service
public class CityLocationServiceImpl implements CityLocationService {

    @Value("${weather.api-host}")
    private String apiHost;

    @Value("${weather.api-key}")
    private String apiKey;

    @Override
    public String getCityLocation(String cityName, String adm) {
        // System.out.println(apiHost);
        // System.out.println(apiKey);

        String location = URLUtil.encode(cityName);
        String admParam = "";
        if(adm != null && !adm.trim().isEmpty()){
            admParam = URLUtil.encode(adm);
        }
        String url = apiHost + "/geo/v2/city/lookup?location=" + location + "&adm=" + admParam;

        try {

            String responseBody = HttpRequest.get(url)
                    .header("X-QW-Api-Key", apiKey)
                    .header("Accept-Encoding", "gzip")
                    .execute()
                    .body(); 

            // System.out.println("API响应: " + responseBody); 
            JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
            
            if ("200".equals(jsonResponse.getStr("code"))) {
                JSONArray locations = jsonResponse.getJSONArray("location");
                if (locations != null && !locations.isEmpty()) {

                    JSONObject firstResult = locations.getJSONObject(0);
                    String lat = firstResult.getStr("lat");
                    String lon = firstResult.getStr("lon");
                    
                    return lon + "," + lat;
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
    
}
