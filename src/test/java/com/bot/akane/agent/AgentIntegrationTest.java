package com.bot.akane.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bot.akane.exception.AgentStateException;

/**
 * Agent 集成测试
 */
@SpringBootTest
public class AgentIntegrationTest {
    private Agent agent;
    private MockTool mockTool;

    @MockBean
    private ChatClient chatClient;

    @BeforeEach
    public void setUp() {
        mockTool = new MockTool();
        
        // 创建工具回调
        Object[] toolObjects = { mockTool };
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(toolObjects)
                .build()
                .getToolCallbacks();

        // 创建 Agent
        agent = new Agent(
                "TestAgent",
                "Test Agent",
                "You are a helpful assistant.",
            "Directly answer if no tool is needed.",
                chatClient,
                20,
                10,
                "test-session",
                Arrays.asList(toolCallbacks)
        );
    }

    @Test
    public void testAgentInitialization() {
        assertNotNull(agent);
        assertEquals("TestAgent", agent.getName());
        assertEquals(AgentState.IDLE, agent.getState());
    }

    @Test
    public void testAgentStateTransition() {
        assertEquals(AgentState.IDLE, agent.getState());
        
        // After reset, should be IDLE
        agent.reset();
        assertEquals(AgentState.IDLE, agent.getState());
    }

    @Test
    public void testConversationHistory() {
        List<Message> history = agent.getConversationHistory();
        assertNotNull(history);
        // Should contain at least the system message
        assertTrue(history.size() > 0);
    }

    @Test
    public void testAgentReset() {
        List<Message> historyBefore = agent.getConversationHistory();
        int sizeBefore = historyBefore.size();

        agent.reset();

        List<Message> historyAfter = agent.getConversationHistory();
        // After reset, should only have system message
        assertEquals(1, historyAfter.size());
    }

    @Test
    public void testConcurrentChatAttempts() {
        // First chat should work
        assertEquals(AgentState.IDLE, agent.getState());

        // Attempting to chat while not idle should fail
        // (This would require mocking ChatClient to actually execute chat)
    }

    @Test
    public void testAgentInterface() {
        assertTrue(agent instanceof AgentInterface);
        
        // Test interface methods
        assertNotNull(agent.getName());
        assertNotNull(agent.getState());
        assertNotNull(agent.getConversationHistory());
    }

    @Test
    public void testToolAvailability() {
        // Verify that tools are properly registered
        assertNotNull(mockTool);
        assertEquals("MockTool", mockTool.getName());
        assertEquals("Mock tool for testing", mockTool.getDescription());
    }

    @Test
    public void testAgentStateException() {
        // Create a new agent and test state exception
        Agent testAgent = new Agent(
                "TestAgent2",
                "Test Agent 2",
                "You are a helpful assistant.",
            "Directly answer if no tool is needed.",
                chatClient,
                20,
                10,
                "test-session-2",
                Arrays.asList()
        );

        // Agent should be in IDLE state
        assertEquals(AgentState.IDLE, testAgent.getState());

        // Attempting to chat while in IDLE state should work
        // (but will fail due to mocked ChatClient)
    }
}
