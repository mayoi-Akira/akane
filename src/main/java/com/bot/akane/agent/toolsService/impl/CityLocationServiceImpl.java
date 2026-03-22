package com.bot.akane.agent.toolsService.impl;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import java.util.Objects;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bot.akane.agent.toolsService.CityLocationService;
import com.bot.akane.mapper.LocationMapper;
import com.bot.akane.model.entity.LocationEntiy;

@Service
@RequiredArgsConstructor
public class CityLocationServiceImpl implements CityLocationService {

    private static final String CITY_LOCATION_CACHE = "cityLocationCache";

    @Value("${weather.api-host}")
    private String apiHost;

    @Value("${weather.api-key}")
    private String apiKey;

    private final LocationMapper locationMapper;
    private final LocationAsyncSaveToDbService locationAsyncPersistenceService;
    private final CacheManager cacheManager;

    private String buildCacheKey(String cityName, String adm) {
        String normalizedCity = cityName == null ? "" : cityName.trim();
        String normalizedAdm = adm == null ? "" : adm.trim();
        return normalizedCity + "_" + normalizedAdm;
    }

    @Override
    @Cacheable( value = CITY_LOCATION_CACHE,
            key = "(#cityName == null ? '' : #cityName.trim()) + '_' + (#adm == null ? '' : #adm.trim())",
            unless = "#result == null")
    public String getCityLocation(String cityName, String adm) {
        String normalizedCity = cityName == null ? "" : cityName.trim();
        String normalizedAdm = adm == null ? "" : adm.trim();

        LocationEntiy locationEntity = locationMapper.getLocationByCityAndAdm(normalizedCity, normalizedAdm);
        if (locationEntity != null) {
            return locationEntity.getLonAndLat();
        }
        String result = getCityLocationFromApi(normalizedCity, normalizedAdm);
        if (result != null) {
            locationAsyncPersistenceService.upsertLocationAsync(normalizedCity, normalizedAdm, result);
        }
        return result;
    }

    @SuppressWarnings("null")
    @EventListener(ApplicationReadyEvent.class)
    public void syncCacheAndDbOnStartup() {
        Cache cache = cacheManager.getCache(CITY_LOCATION_CACHE);
        if (cache == null) {
            return;
        }

        for (LocationEntiy entity : locationMapper.getAllLocations()) {
            String lonAndLat = entity.getLonAndLat();
            if (!StringUtils.hasText(lonAndLat)) {
                continue;
            }
            cache.put(buildCacheKey(entity.getCityName(), entity.getAdm()), Objects.requireNonNull(lonAndLat));
        }
    }

    @Override
    public  String getCityLocationFromApi(String cityName, String adm) {
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
