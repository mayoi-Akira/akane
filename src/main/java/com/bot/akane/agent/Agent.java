package com.bot.akane.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bot.akane.exception.AgentStateException;
import com.bot.akane.util.TraceIdUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Agent 核心类
 * 集成了基础对话能力和工具调用能力
 * 支持 ReAct 循环：Reasoning -> Acting -> Observing
 */
@Slf4j
public class Agent implements AgentInterface {
    private static final String DEFAULT_THINK_PROMPT = """
            你是一个智能的决策模块，负责完成用户请求。
            核心原则：
            1. 先判断是否可以直接回答，能直接回答就不要调用工具
            2. 只有在确实需要外部实时信息、执行操作或精确计算时才调用工具
            3. 缺少必要参数时先追问用户，不要猜测参数
            4. 严格只调用已提供的工具，不要编造工具名
            5. 任务完成后给出简洁自然语言总结

            请根据当前对话上下文决定下一步动作：
            - 需要工具时再调用工具
            - 不需要工具时直接输出自然语言回复
            """;

    // 基础属性
    private String name;
    private String description;
    private String systemPrompt;
    private String thinkPrompt;
    private ChatClient chatClient;
    private String sessionId;
    
    // 状态管理
    private AgentStateManager stateManager;
    
    // 内存管理
    private ChatMemory chatMemory;
    private List<Message> tempMessages;
    
    // 工具相关
    private List<ToolCallback> availableTools;
    private ToolCallingManager toolCallingManager;
    private ChatOptions chatOptions;
    private ChatResponse lastChatResponse;
    
    // 执行控制
    private Integer maxSteps;
    private boolean overMaxSteps = false;

    public Agent(String name,
                 String description,
                 String systemPrompt,
                 String thinkPrompt,
                 ChatClient chatClient,
                 Integer maxMessages,
                 Integer maxSteps,
                 String sessionId,
                 List<ToolCallback> availableTools) {
        this.name = name;
        this.description = description;
        this.systemPrompt = systemPrompt;
        this.thinkPrompt = StringUtils.hasLength(thinkPrompt) ? thinkPrompt : DEFAULT_THINK_PROMPT;
        this.chatClient = chatClient;
        this.sessionId = sessionId != null ? sessionId : "default-session";
        this.maxSteps = maxSteps != null ? maxSteps : 20;
        this.availableTools = availableTools;
        
        // 初始化状态管理
        this.stateManager = new AgentStateManager();
        
        // 初始化内存管理
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(maxMessages != null ? maxMessages : 20)
                .build();
        if (StringUtils.hasLength(systemPrompt)) {
            this.chatMemory.add(this.sessionId, new SystemMessage(systemPrompt));
        }
        
        // 初始化临时消息列表
        this.tempMessages = new ArrayList<>();
        
        // 初始化工具调用
        this.chatOptions = DefaultToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
        this.toolCallingManager = ToolCallingManager.builder().build();
    }

    /**
     * 主聊天方法
     */
    @Override
    public String chat(String userInput) {
        Assert.notNull(userInput, "用户输入不能为空");
        
        if (!stateManager.isIdle()) {
            throw new AgentStateException(
                    stateManager.getCurrentState(),
                    AgentState.IDLE,
                    "Agent 非空闲状态，当前状态：" + stateManager.getCurrentState()
            );
        }
        
        try {
            stateManager.transitionTo(AgentState.THINKING);
            TraceIdUtil.setSessionId(sessionId);
            tempMessages.clear();
            
            UserMessage userMessage = new UserMessage(userInput);
            chatMemory.add(sessionId, userMessage);
            
            // ReAct 循环
            for (int step = 1; step <= maxSteps && !stateManager.isInState(AgentState.FINISHED); step++) {
                log.info("\n========== Agent Step {} ==========\n", step);
                executeStep();
                
                if (step == maxSteps) {
                    stateManager.transitionTo(AgentState.FINISHED);
                    log.warn("已达到最大步骤数 {}，强制结束", maxSteps);
                    this.overMaxSteps = true;
                }
            }
            
            return extractFinalResponse();
        } catch (Exception e) {
            stateManager.transitionTo(AgentState.ERROR);
            log.error("Agent chat failed", e);
            throw e;
        } finally {
            stateManager.reset();
            tempMessages.clear();
        }
    }

    /**
     * 执行单个步骤：思考 -> 执行
     */
    private void executeStep() {
        if (think()) {
            // 有工具调用，执行工具
            stateManager.transitionTo(AgentState.EXECUTING);
            execute();
            if (!stateManager.isInState(AgentState.FINISHED) && !stateManager.isInState(AgentState.ERROR)) {
                stateManager.transitionTo(AgentState.THINKING);
            }
        } else {
            // 没有工具调用，直接结束
            stateManager.transitionTo(AgentState.FINISHED);
        }
    }

    /**
     * 思考阶段：决定是否需要调用工具
     */
    private Boolean think() {
        Prompt prompt = Prompt.builder()
                .messages(buildFullMessages())
                .chatOptions(this.chatOptions)
                .build();
        
        this.lastChatResponse = chatClient
                .prompt(prompt)
            .system(this.thinkPrompt)
                .toolCallbacks(availableTools != null ? availableTools.toArray(new ToolCallback[0]) : new ToolCallback[0])
                .call()
                .chatClientResponse()
                .chatResponse();
        
        Assert.notNull(lastChatResponse, "Last chat response cannot be null");
        AssistantMessage output = lastChatResponse.getResult().getOutput();
        var toolCalls = output.getToolCalls();
        
        logToolCalls(toolCalls);
        
        if (toolCalls.isEmpty()) {
            // 没有工具调用，将 AI 回复添加到 chatMemory
            chatMemory.add(sessionId, output);
            return false;
        }
        
        // 有工具调用，先添加到 tempMessages
        tempMessages.add(output);
        return true;
    }

    /**
     * 执行阶段：调用工具并处理结果
     */
    private void execute() {
        Assert.notNull(this.lastChatResponse, "Last chat response cannot be null");
        
        if (!this.lastChatResponse.hasToolCalls()) {
            return;
        }
        
        Prompt prompt = Prompt.builder()
                .messages(buildFullMessages())
                .chatOptions(this.chatOptions)
                .build();
        
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, this.lastChatResponse);
        
        List<Message> conversationHistory = toolExecutionResult.conversationHistory();
        Message lastMessage = conversationHistory.get(conversationHistory.size() - 1);
        tempMessages.add(lastMessage);
        
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) lastMessage;
        
        String result = toolResponseMessage.getResponses()
                .stream()
                .map(resp -> "工具 " + resp.name() + " 的返回结果为：" + resp.responseData())
                .collect(Collectors.joining("\n"));
        log.info("工具调用结果：{}", result);
        
        // if (toolResponseMessage.getResponses()
        //         .stream()
        //         .anyMatch(resp -> resp.name().equals("terminate"))) {
        //     stateManager.transitionTo(AgentState.FINISHED);
        //     log.info("任务结束");
        // }
    }

    /**
     * 构建完整的消息列表（chatMemory + tempMessages）
     */
    private List<Message> buildFullMessages() {
        List<Message> fullMessages = new ArrayList<>(this.chatMemory.get(sessionId));
        fullMessages.addAll(this.tempMessages);
        return fullMessages;
    }

    /**
     * 记录工具调用信息
     */
    private void logToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            log.info("\n[ToolCalling] 无工具调用");
            return;
        }
        String logMessage = IntStream.range(0, toolCalls.size())
                .mapToObj(i -> {
                    AssistantMessage.ToolCall call = toolCalls.get(i);
                    return String.format(
                            "[ToolCalling #%d]\n- name      : %s\n- arguments : %s",
                            i + 1,
                            call.name(),
                            call.arguments()
                    );
                })
                .collect(Collectors.joining("\n\n"));
        log.info("\n========== Tool Calling ==========\n{}\n=================================\n", logMessage);
    }

    /**
     * 提取最终响应
     */
    private String extractFinalResponse() {
        var history = this.chatMemory.get(this.sessionId);
        
        String AIResponse = history.stream()
                .filter(msg -> msg instanceof AssistantMessage)
                .reduce((first, second) -> second)
                .map(msg -> ((AssistantMessage) msg).getText())
                .orElse("");
        
        log.info("\n========== Final AI Response ==========\n{}\n=====================================\n", AIResponse);
        
        if (overMaxSteps) {
            AIResponse = "任务执行未完成，已达到最大步骤数限制，请尝试提供更多信息或简化问题。";
            overMaxSteps = false;
        }
        
        return AIResponse;
    }

    /**
     * 获取当前对话历史
     */
    @Override
    public List<Message> getConversationHistory() {
        return chatMemory.get(sessionId);
    }

    /**
     * 重置对话历史
     */
    @Override
    public void reset() {
        chatMemory.clear(sessionId);
        if (StringUtils.hasLength(systemPrompt)) {
            chatMemory.add(sessionId, new SystemMessage(systemPrompt));
        }
        stateManager.reset();
        tempMessages.clear();
    }

    /**
     * 获取当前状态
     */
    @Override
    public AgentState getState() {
        return stateManager.getCurrentState();
    }

    /**
     * 获取 Agent 名称
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Agent {" +
                "name = " + name + ",\n" +
                "description = " + description + ",\n" +
                "systemPrompt = " + systemPrompt + "}";
    }
}
