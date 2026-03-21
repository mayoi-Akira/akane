package com.bot.akane.exception;

/**
 * 工具执行异常
 * 用于表示工具调用或执行过程中发生的异常
 */
public class ToolExecutionException extends AgentException {
    private final String toolName;
    private final String toolArguments;

    public ToolExecutionException(String toolName, String errorMessage) {
        super("TOOL_EXECUTION_ERROR", errorMessage);
        this.toolName = toolName;
        this.toolArguments = null;
    }

    public ToolExecutionException(String toolName, String errorMessage, Throwable cause) {
        super("TOOL_EXECUTION_ERROR", errorMessage, cause);
        this.toolName = toolName;
        this.toolArguments = null;
    }

    public ToolExecutionException(String toolName, String toolArguments, String errorMessage, Throwable cause) {
        super("TOOL_EXECUTION_ERROR", errorMessage, cause);
        this.toolName = toolName;
        this.toolArguments = toolArguments;
    }

    public String getToolName() {
        return toolName;
    }

    public String getToolArguments() {
        return toolArguments;
    }
}
