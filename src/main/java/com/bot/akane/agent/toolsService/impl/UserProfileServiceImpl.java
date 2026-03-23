package com.bot.akane.agent.toolsService.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bot.akane.agent.toolsService.UserProfileService;
import com.bot.akane.mapper.UserProfileMapper;
import com.bot.akane.model.entity.UserProfileEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
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
            
            // 先更新数据库
            UserProfileEntity entity = new UserProfileEntity();
            entity.setUserId(userId);
            entity.setProfileKey(profileKey);
            entity.setProfileValue(profileValue);
            userProfileMapper.upsertUserProfile(entity);
            
            // 再更新缓存
            redisTemplate.opsForHash().put(key, profileKey, profileValue);
            
            return "用户个人信息已更新。";
        } catch (Exception e) {
            log.error("更新用户个人信息失败 userId={}, profileKey={}, profileValue={}", userId, profileKey, profileValue, e);
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
                map.put("userId", userId);
                return toJson(map);
            }

            List<UserProfileEntity> list = userProfileMapper.getUserProfiles(userId);

            if (list == null || list.isEmpty()) {
                return "{\"userId\": \"" + userId + "\"}";
            }

            Map<String, String> resultMap = new HashMap<>();
            for (UserProfileEntity entity : list) {
                resultMap.put(entity.getProfileKey(), entity.getProfileValue());
            }

            redisTemplate.opsForHash().putAll(key, resultMap);
            resultMap.put("userId", userId);
            return toJson(resultMap);
        } catch (Exception e) {
            log.error("获取用户个人信息失败 userId={}", userId, e);
            return "获取用户个人信息失败：" + e.getMessage();
        }
    }

    @Override
    public String deleteUserProfile(String userId, String profileKey) {
        if (userId == null || userId.trim().isEmpty()) {
            return "用户ID不能为空。";
        }
        if (profileKey == null || profileKey.trim().isEmpty()) {
            return "信息类型不能为空。";
        }

        String key = "user_profile:" + userId;

        try {
            // 执行软删除，返回受影响的行数
            int result = userProfileMapper.deleteUserProfile(userId, profileKey);
            
            if (result == 0) {
                return "用户个人信息不存在或已删除。";
            }

            // 删除成功后清除 Redis 缓存
            redisTemplate.opsForHash().delete(key, profileKey);

            return "用户个人信息已删除。";

        } catch (Exception e) {
            log.error("删除用户信息失败 userId={}, profileKey={}", userId, profileKey, e);
            return "删除用户个人信息失败，请稍后重试。";
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
