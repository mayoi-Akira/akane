package com.bot.akane.agent.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具元数据
 * 用于描述工具的详细信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolMetadata {
    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 工具类型
     */
    private ToolDefaultType type;

    /**
     * 工具版本
     */
    private String version;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 工具分类
     */
    private String category;

    /**
     * 工具超时时间（毫秒）
     */
    private long timeoutMs;

    /**
     * 最大重试次数
     */
    private int maxRetries;

    /**
     * 工具作者
     */
    private String author;

    /**
     * 工具创建时间
     */
    private long createdAt;
}
