package com.bot.akane.agent.toolsService.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bot.akane.agent.toolsService.UserProfileService;
import com.bot.akane.mapper.UserProfileMapper;
import com.bot.akane.model.entity.UserProfileEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final UserProfileAsyncSaveToDbService userProfileAsyncSaveToDbService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String setUserProfile(String userId, String profileKey, String profileValue) {
        if (userId == null || userId.trim().isEmpty()) {
            return "用户ID不能为空。";
        }
        if (profileKey == null || profileKey.trim().isEmpty()) {
            return "信息类型不能为空。";
        }
        if (profileValue == null || profileValue.trim().isEmpty()) {
            return "信息内容不能为空。";
        }
        try {
            String key = "user_profile:" + userId;
            redisTemplate.opsForHash().put(key, profileKey, profileValue);
            userProfileAsyncSaveToDbService.upsertUserProfileAsync(userId, profileKey, profileValue);
            return "用户个人信息已更新。";
        } catch (Exception e) {
            return "更新用户个人信息失败：" + e.getMessage();
        }
    }

    @Override
    public String getUserProfile(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "用户ID不能为空。";
        }
        try {
            String key = "user_profile:" + userId;
             Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            if (map != null && !map.isEmpty()) {
                return toJson(map);
            }

            List<UserProfileEntity> list = userProfileMapper.getUserProfiles(userId);

            if (list == null || list.isEmpty()) {
                return "{}"; 
            }

            Map<String, String> resultMap = new HashMap<>();
            for (UserProfileEntity entity : list) {
                resultMap.put(entity.getProfileKey(), entity.getProfileValue());
            }

            redisTemplate.opsForHash().putAll(key, resultMap);

            return toJson(resultMap);
        } catch (Exception e) {
            return "获取用户个人信息失败：" + e.getMessage();
        }
    }

    private String toJson(Object obj) {
    try {
        return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
        return "{}";
    }
}
}
