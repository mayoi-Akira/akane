package com.bot.akane.agent;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.agent.toolsService.impl.UserProfileServiceImpl;
import com.bot.akane.exception.AgentException;
import com.bot.akane.service.GroupToolService;
import com.bot.akane.util.TraceIdUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentManager {
    private final ApplicationContext applicationContext;
    private final GroupToolService groupToolService;
    private final Map<String, Agent> agentCache = new ConcurrentHashMap<>();
    private final UserProfileServiceImpl userProfileService;

    @Autowired
    @Qualifier("deepseek-chat")
    private ChatClient chatClient;

    @Value("${Agent.system-prompt}")
    private String systemPrompt;

    @Value("${Agent.think-prompt:}")
    private String thinkPrompt;

    @Value("${agent.name:akane}")
    private String agentName;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String GROUP_CHAT_LOCK_KEY_PREFIX = "akane:chat:lock:";
    private static final Duration GROUP_CHAT_LOCK_TTL = Duration.ofMinutes(5);
    
    public String chat(String groupId, String userId, String messageId, String userMessage) {
        TraceIdUtil.setGroupId(groupId);
        log.info("Agent chat started, groupId: {}, userMessage length: {}", groupId, userMessage.length());
        
        String lockKey = GROUP_CHAT_LOCK_KEY_PREFIX + groupId;
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", GROUP_CHAT_LOCK_TTL);
        if (!Boolean.TRUE.equals(lockAcquired)) {
            log.warn("Failed to acquire lock for groupId: {}", groupId);
            return "<quote id=\"" + messageId + "\"/> akane 思考中，请稍后...";
        }

        Agent agent = agentCache.computeIfAbsent(groupId, id -> createNewAgent(id));
        try {
            String userProfileInfo = userProfileService.getUserProfile(userId);

            if(userProfileInfo == null || userProfileInfo.trim().isEmpty()) {
                userProfileInfo = "无相关资料。";
            } 
            String userInput = "{userProfileInfo: " + userProfileInfo + ", userMessage: " + userMessage + "}";
            
            String response = agent.chat(userInput);
            log.info("Agent chat completed successfully, groupId: {}, response length: {}", groupId, response.length());
            return response;
        } catch (AgentException e) {
            log.error("Agent execution failed with business exception, groupId: {}, errorCode: {}", 
                      groupId, e.getErrorCode(), e);
            throw e;
        } catch (Exception e) {
            log.error("Agent execution failed with unexpected exception, groupId: {}", groupId, e);
            throw new AgentException("AGENT_EXECUTION_ERROR", "Agent 执行失败: " + e.getMessage(), e);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    public String resetAgent(String groupId, String messageId) {
        String lockKey = GROUP_CHAT_LOCK_KEY_PREFIX + groupId;
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", GROUP_CHAT_LOCK_TTL);
        if (!Boolean.TRUE.equals(lockAcquired)) {
            log.warn("Failed to acquire lock for groupId: {}", groupId);
            return "<quote id=\"" + messageId + "\"/>akane 还在思考中，请稍后再试...";
        }
        log.info("Resetting agent for groupId: {}", groupId);
        // agentCache.get(groupId).reset();
        redisTemplate.delete(lockKey);
        agentCache.remove(groupId);
        return "你是谁...？";
    }

    private Agent createNewAgent(String groupId) {
        log.debug("Creating new agent for groupId: {}", groupId);
        List<String> enabledToolCodes = groupToolService.getToolsForGroup(groupId);
        log.debug("Enabled tools for groupId {}: {}", groupId, enabledToolCodes);
        
        Object[] activeToolObjects = applicationContext.getBeansOfType(ToolInterface.class)
                .values()
                .stream()
            .filter(tool -> enabledToolCodes.contains(tool.getName()))
                .toArray();
        
        log.debug("Active tool objects count: {}", activeToolObjects.length);
        
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(activeToolObjects)
                .build()
                .getToolCallbacks();

        Agent agent = new Agent(
                agentName + "-" + groupId,
                "Agent for Group " + groupId,
                systemPrompt,
            thinkPrompt,
                chatClient,
                10,
                20,
                "session-" + groupId,
                Arrays.asList(toolCallbacks)
        );
        log.info("Agent created successfully for groupId: {}, agentName: {}", groupId, agent.getName());
        return agent;
    }

    
}
