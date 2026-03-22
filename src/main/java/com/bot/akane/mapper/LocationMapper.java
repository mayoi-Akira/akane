package com.bot.akane.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bot.akane.model.entity.LocationEntiy;

@Mapper
public interface LocationMapper {

    LocationEntiy getLocationByCityAndAdm(String cityName, String adm);

    int insertLocation(LocationEntiy location);

    int upsertLocation(LocationEntiy location);

    List<LocationEntiy> getAllLocations();
    
} 
