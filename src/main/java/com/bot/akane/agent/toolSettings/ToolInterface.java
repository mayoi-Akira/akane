package com.bot.akane.agent.toolSettings;

/**
 * 工具接口
 * 定义所有工具必须实现的契约
 */
public interface ToolInterface {
    /**
     * 获取工具名称
     */
    String getName();

    /**
     * 获取工具描述
     */
    String getDescription();

    /**
     * 获取工具类型
     */
    ToolType getType();

    /**
     * 获取工具代码
     */
    String getCode();

}
