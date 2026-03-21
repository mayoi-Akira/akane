package com.bot.akane.model.entity;

import com.bot.akane.agent.tools.ToolDefaultType;
import lombok.Data;

@Data
public class GroupToolMapping {
    private Integer id;
    private Integer toolId;
    private String toolName;
    private String toolDesc;
    private ToolDefaultType isEnabled;
}
