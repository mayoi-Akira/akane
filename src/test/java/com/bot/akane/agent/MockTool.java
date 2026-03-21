package com.bot.akane.agent;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolDefaultType;
import com.bot.akane.agent.toolSettings.ToolInterface;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试用工具
 */
@TestComponent
@Slf4j
public class MockTool implements ToolInterface {

    @Override
    public String getName() {
        return "MockTool";
    }

    @Override
    public String getDescription() {
        return "Mock tool for testing";
    }

    @Override
    public ToolDefaultType getType() {
        return ToolDefaultType.ENABLE;
    }

    @Tool(name = "echo", description = "Echo the input")
    public String echo(String message) {
        log.info("Echo called with message: {}", message);
        return "Echo: " + message;
    }

    @Tool(name = "add", description = "Add two numbers")
    public String add(int a, int b) {
        log.info("Add called with a={}, b={}", a, b);
        return "Result: " + (a + b);
    }

    @Tool(name = "delay", description = "Delay for specified milliseconds")
    public String delay(long ms) {
        try {
            Thread.sleep(ms);
            return "Delayed for " + ms + "ms";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }
    }
}
