package com.bot.akane.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

import com.bot.akane.agent.toolSettings.ToolType;

@Data
public class Tool {
    private String toolCode;
    private String toolName;
    private String description;
    private ToolType toolType; 
    private LocalDateTime createdAt;
}
