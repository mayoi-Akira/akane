package com.bot.akane.agent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bot.akane.exception.AgentStateException;

/**
 * AgentStateManager 单元测试
 */
public class AgentStateManagerTest {
    private AgentStateManager stateManager;

    @BeforeEach
    public void setUp() {
        stateManager = new AgentStateManager();
    }

    @Test
    public void testInitialState() {
        assertEquals(AgentState.IDLE, stateManager.getCurrentState());
        assertTrue(stateManager.isIdle());
    }

    @Test
    public void testValidTransition() {
        stateManager.transitionTo(AgentState.THINKING);
        assertEquals(AgentState.THINKING, stateManager.getCurrentState());
    }

    @Test
    public void testInvalidTransition() {
        stateManager.transitionTo(AgentState.THINKING);
        assertThrows(AgentStateException.class, () -> {
            stateManager.transitionTo(AgentState.IDLE);
        });
    }

    @Test
    public void testCompleteTransitionCycle() {
        // IDLE -> THINKING
        stateManager.transitionTo(AgentState.THINKING);
        assertEquals(AgentState.THINKING, stateManager.getCurrentState());

        // THINKING -> EXECUTING
        stateManager.transitionTo(AgentState.EXECUTING);
        assertEquals(AgentState.EXECUTING, stateManager.getCurrentState());

        // EXECUTING -> FINISHED
        stateManager.transitionTo(AgentState.FINISHED);
        assertEquals(AgentState.FINISHED, stateManager.getCurrentState());

        // FINISHED -> IDLE
        stateManager.reset();
        assertEquals(AgentState.IDLE, stateManager.getCurrentState());
    }

    @Test
    public void testIsInState() {
        assertTrue(stateManager.isInState(AgentState.IDLE));
        assertFalse(stateManager.isInState(AgentState.THINKING));

        stateManager.transitionTo(AgentState.THINKING);
        assertTrue(stateManager.isInState(AgentState.THINKING));
        assertFalse(stateManager.isInState(AgentState.IDLE));
    }

    @Test
    public void testReset() {
        stateManager.transitionTo(AgentState.THINKING);
        stateManager.transitionTo(AgentState.EXECUTING);
        stateManager.transitionTo(AgentState.FINISHED);

        stateManager.reset();
        assertEquals(AgentState.IDLE, stateManager.getCurrentState());
    }

    @Test
    public void testErrorState() {
        stateManager.transitionTo(AgentState.THINKING);
        stateManager.transitionTo(AgentState.ERROR);
        assertEquals(AgentState.ERROR, stateManager.getCurrentState());

        stateManager.reset();
        assertEquals(AgentState.IDLE, stateManager.getCurrentState());
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            stateManager.transitionTo(AgentState.THINKING);
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(100);
                assertEquals(AgentState.THINKING, stateManager.getCurrentState());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
