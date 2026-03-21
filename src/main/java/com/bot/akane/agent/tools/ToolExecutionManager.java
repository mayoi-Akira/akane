package com.bot.akane.agent.tools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.bot.akane.exception.ToolExecutionException;

import lombok.extern.slf4j.Slf4j;

/**
 * 工具执行管理器
 * 负责工具的执行、重试和超时控制
 */
@Slf4j
public class ToolExecutionManager {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 执行工具，支持重试和超时
     */
    public static <T> T executeWithRetry(
            String toolName,
            Callable<T> task,
            long timeoutMs,
            int maxRetries) {
        
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount <= maxRetries) {
            try {
                log.debug("Executing tool: {}, attempt: {}/{}", toolName, retryCount + 1, maxRetries + 1);
                return executeWithTimeout(task, timeoutMs);
            } catch (TimeoutException e) {
                lastException = e;
                log.warn("Tool execution timeout: {}, attempt: {}/{}", toolName, retryCount + 1, maxRetries + 1);
                retryCount++;
                if (retryCount <= maxRetries) {
                    try {
                        Thread.sleep(1000 * retryCount); // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ToolExecutionException(toolName, "Tool execution interrupted", ie);
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.error("Tool execution failed: {}, attempt: {}/{}", toolName, retryCount + 1, maxRetries + 1, e);
                retryCount++;
                if (retryCount <= maxRetries) {
                    try {
                        Thread.sleep(1000 * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ToolExecutionException(toolName, "Tool execution interrupted", ie);
                    }
                }
            }
        }

        throw new ToolExecutionException(
                toolName,
                String.format("Tool execution failed after %d retries", maxRetries),
                lastException
        );
    }

    /**
     * 执行工具，支持超时
     */
    private static <T> T executeWithTimeout(Callable<T> task, long timeoutMs) throws Exception {
        Future<T> future = executorService.submit(task);
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            throw (Exception) e.getCause();
        }
    }

    /**
     * 关闭执行器
     */
    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
