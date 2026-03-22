package com.bot.akane.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bot.akane.model.entity.UserProfileEntity;

@Mapper
public interface UserProfileMapper {

    int upsertUserProfile(UserProfileEntity userProfile);

    List<UserProfileEntity> getUserProfiles(String userId);

}
