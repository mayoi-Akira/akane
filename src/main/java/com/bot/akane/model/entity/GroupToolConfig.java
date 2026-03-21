package com.bot.akane.model.entity;

import com.bot.akane.agent.toolSettings.ToolType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupToolConfig {
    private String groupId;
    private String toolCode;
    private String toolName;
    private ToolType status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
