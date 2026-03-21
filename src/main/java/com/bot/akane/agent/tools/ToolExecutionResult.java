package com.bot.akane.agent.tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具执行结果
 * 用于封装工具执行的结果和元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolExecutionResult {
    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 执行是否成功
     */
    private boolean success;

    /**
     * 执行结果数据
     */
    private Object resultData;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private long executionTimeMs;

    /**
     * 重试次数
     */
    private int retryCount;

    /**
     * 执行时间戳
     */
    private long timestamp;

    /**
     * 验证结果是否有效
     */
    public boolean isValid() {
        return success && resultData != null;
    }

    /**
     * 获取结果数据的字符串表示
     */
    public String getResultAsString() {
        if (resultData == null) {
            return "";
        }
        return resultData.toString();
    }
}
