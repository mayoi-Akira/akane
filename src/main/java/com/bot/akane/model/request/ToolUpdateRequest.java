package com.bot.akane.model.request;

import lombok.Data;

@Data
public class ToolUpdateRequest {
    private String groupId;
    private String[] toolCodes;
    private Boolean enable; // true表示启用，false表示禁用
}
