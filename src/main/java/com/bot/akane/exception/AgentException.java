package com.bot.akane.exception;

/**
 * Agent 执行异常的基类
 * 用于表示 Agent 在执行过程中发生的业务异常
 */
public class AgentException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public AgentException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AgentException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
