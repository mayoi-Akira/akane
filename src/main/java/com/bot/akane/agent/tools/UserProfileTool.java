package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.agent.toolsService.UserProfileService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserProfileTool implements ToolInterface {

    private final UserProfileService userProfileService;

    @Override
    public String getName() {
        return "UserProfileTool";
    }

    @Override
    public String getDescription() {
        return "用于存储和查询用户的个人信息，如昵称、邮箱和各平台绑定信息等。";
    }

    @Override
    public ToolType getType() {
        return ToolType.ENABLE;
    }

    @Override
    public String getCode(){
        return "5";
    }

    @Tool(name = "setUserProfile", description = "设置用户的个人信息，参数分别为用户ID、信息类型和信息内容。当用户提到了他的某个信息或资料时使用这个工具记录下来")
    public String setUserProfile(
            @ToolParam(description = "用户ID")String userId, 
            @ToolParam(description = "信息类型, 例如:nick_name, 再例如: github_url")String profileKey,
            @ToolParam(description = "信息内容, 例如: 张三, 再例如: github.com/mayoi-Akira")String profileValue) {
        return userProfileService.setUserProfile(userId, profileKey, profileValue);
    }

    @Tool(name = "getUserProfile", description = "获取用户的个人信息，当用户的问题涉及到他自己的某个信息或资料时，优先尝试使用这个工具查询出来而不是询问用户")
    public String getUserProfile(@ToolParam(description = "用户ID")String userId) {
        return userProfileService.getUserProfile(userId);
    }

    @Tool(name = "deleteUserProfile", description = "删除用户的个人信息，当用户要求删除某个信息或资料时使用这个工具")
    public String deleteUserProfile(
            @ToolParam(description = "用户ID")String userId, 
            @ToolParam(description = "信息类型, 例如:nick_name, 再例如: github_url")String profileKey) {
        return userProfileService.deleteUserProfile(userId, profileKey);
    }
    
}
