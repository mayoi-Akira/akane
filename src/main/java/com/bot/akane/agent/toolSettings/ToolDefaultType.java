package com.bot.akane.agent.toolSettings;

/**
 * 工具的默认开启状态
 */
public enum ToolDefaultType {
    /**
     * 正常启用 - 工具可被启用或禁用
     */
    ENABLE,
    
    /**
     * 禁用 - 工具默认禁用
     */
    DISABLE,
    
    /**
     * 强制开启 - 工具强制启用，不可被禁用
     */
    FORCE,
    
}
