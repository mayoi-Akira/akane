package com.bot.akane.agent.toolsService.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bot.akane.mapper.UserProfileMapper;
import com.bot.akane.model.entity.UserProfileEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileAsyncSaveToDbService {
    private final UserProfileMapper userProfileMapper;
    @Async
    public void upsertUserProfileAsync(String userId, String profileKey, String profileValue) {
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setUserId(userId);
        userProfileEntity.setProfileKey(profileKey);
        userProfileEntity.setProfileValue(profileValue);
        userProfileMapper.upsertUserProfile(userProfileEntity);
    }
}
