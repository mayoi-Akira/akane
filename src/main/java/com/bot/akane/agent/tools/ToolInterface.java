package com.bot.akane.agent.tools;

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
    ToolDefaultType getType();

    /**
     * 获取工具元数据
     */
    default ToolMetadata getMetadata() {
        return ToolMetadata.builder()
                .name(getName())
                .description(getDescription())
                .type(getType())
                .version("1.0.0")
                .enabled(true)
                .category("default")
                .timeoutMs(30000)
                .maxRetries(3)
                .author("system")
                .createdAt(System.currentTimeMillis())
                .build();
    }

    /**
     * 验证工具是否可用
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * 获取工具的超时时间（毫秒）
     */
    default long getTimeoutMs() {
        return getMetadata().getTimeoutMs();
    }

    /**
     * 获取工具的最大重试次数
     */
    default int getMaxRetries() {
        return getMetadata().getMaxRetries();
    }
}
