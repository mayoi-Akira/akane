package com.bot.akane.agent.toolsService.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bot.akane.mapper.LocationMapper;
import com.bot.akane.model.entity.LocationEntiy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationAsyncPersistenceService {

    private final LocationMapper locationMapper;

    @Async
    public void upsertLocationAsync(String cityName, String adm, String lonAndLat) {
        LocationEntiy locationEntity = new LocationEntiy();
        locationEntity.setCityName(cityName);
        locationEntity.setAdm(adm);
        locationEntity.setLonAndLat(lonAndLat);
        locationMapper.upsertLocation(locationEntity);
    }
}
