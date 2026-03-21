package com.bot.akane.agent.tools;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Callable;

import org.junit.jupiter.api.Test;

import com.bot.akane.agent.toolSettings.ToolExecutionManager;
import com.bot.akane.exception.ToolExecutionException;

/**
 * ToolExecutionManager 单元测试
 */
public class ToolExecutionManagerTest {

    @Test
    public void testSuccessfulExecution() {
        Callable<String> task = () -> "success";
        String result = ToolExecutionManager.executeWithRetry("TestTool", task, 5000, 3);
        assertEquals("success", result);
    }

    @Test
    public void testExecutionWithTimeout() {
        Callable<String> task = () -> {
            Thread.sleep(2000);
            return "success";
        };
        
        assertThrows(ToolExecutionException.class, () -> {
            ToolExecutionManager.executeWithRetry("TestTool", task, 1000, 0);
        });
    }

    @Test
    public void testRetryOnFailure() {
        Callable<String> task = new Callable<String>() {
            private int attempts = 0;

            @Override
            public String call() throws Exception {
                attempts++;
                if (attempts < 3) {
                    throw new RuntimeException("Temporary failure");
                }
                return "success";
            }
        };

        String result = ToolExecutionManager.executeWithRetry("TestTool", task, 5000, 3);
        assertEquals("success", result);
    }

    @Test
    public void testMaxRetriesExceeded() {
        Callable<String> task = () -> {
            throw new RuntimeException("Permanent failure");
        };

        assertThrows(ToolExecutionException.class, () -> {
            ToolExecutionManager.executeWithRetry("TestTool", task, 5000, 2);
        });
    }

    @Test
    public void testExceptionPropagation() {
        Callable<String> task = () -> {
            throw new IllegalArgumentException("Invalid argument");
        };

        ToolExecutionException exception = assertThrows(ToolExecutionException.class, () -> {
            ToolExecutionManager.executeWithRetry("TestTool", task, 5000, 0);
        });

        assertEquals("TestTool", exception.getToolName());
        assertTrue(exception.getMessage().contains("after 0 retries"));
    }

    @Test
    public void testInterruptedExecution() {
        Callable<String> task = () -> {
            Thread.sleep(10000);
            return "success";
        };

        Thread testThread = new Thread(() -> {
            assertThrows(ToolExecutionException.class, () -> {
                ToolExecutionManager.executeWithRetry("TestTool", task, 5000, 0);
            });
        });

        testThread.start();
        try {
            Thread.sleep(100);
            testThread.interrupt();
            testThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
