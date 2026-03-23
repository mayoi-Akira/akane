package com.bot.akane.agent.toolsService;

public interface UserProfileService {

    /**
     * 更新用户画像信息，如果不存在则插入新记录
     * @param userId 用户ID
     * @param profileKey 画像信息的键，例如 "nick_name" 或 "github_url"
     * @param profileValue 画像信息的值，例如 "张三" 或 "github.com/mayoi-Akira"
     * @return 操作结果的反馈信息
     */
    public String setUserProfile(String userId, String profileKey, String profileValue);

    /**
     * 获取用户画像信息
     * @param userId 用户ID
     * @return 所有该用户的画像信息，返回JSON字符串，格式为 {"profileKey1": "profileValue1", "profileKey2": "profileValue2", ...}
     */
    public String getUserProfile(String userId);

    /**
     * 删除用户画像信息
     * @param userId 用户ID
     * @param profileKey 画像信息的键，例如 "nick_name" 或 "github_url"
     * @return 操作结果的反馈信息
     */
    public String deleteUserProfile(String userId, String profileKey);

}
