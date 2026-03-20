package com.bot.akane.agent.examples;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.bot.akane.agent.AgentState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentV2 extends AgentV1 {
    // 可用的工具列表
    protected List<ToolCallback> availableTools;
    
    // 工具调用管理器
    protected ToolCallingManager toolCallingManager;
    
    // ChatOptions
    protected ChatOptions chatOptions;
    
    // 最后一次的 ChatResponse
    protected ChatResponse lastChatResponse;
    
    // 最多循环次数
    private Integer MAX_STEPS;

    public AgentV2() {
        super();
    }

    public AgentV2(String name,
                      String description,
                      String systemPrompt,
                      ChatClient chatClient,
                      Integer maxMessages,
                      Integer maxSteps,
                      String sessionId,
                      List<ToolCallback> availableTools
                    ) {
        super(name, description, systemPrompt, chatClient, maxMessages, sessionId);
        this.MAX_STEPS = maxSteps;

        this.availableTools = availableTools;
        this.chatOptions = DefaultToolCallingChatOptions
                        .builder()
                        .internalToolExecutionEnabled(false)
                        .build();
        this.toolCallingManager = ToolCallingManager
                        .builder()
                        .build();
    }

    protected void logToolCalls(List<AssistantMessage.ToolCall> toolCalls) {
        if (toolCalls == null || toolCalls.isEmpty()) {
            log.info("\n\n[ToolCalling] 无工具调用");
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
        log.info("\n\n========== Tool Calling ==========\n{}\n=================================\n", logMessage);
    }

    protected Boolean think(){
        String thinkPrompt = """
                你是一个智能的决策模块，负责完成用户的完整请求。
                核心原则：
                1. 仔细分析用户的原始请求，确保完成所有要求
                2. 如果缺少必要信息，必须主动调用相应的工具获取，不要询问用户
                3. 只有在真正完成用户的完整请求后，才能给出最终回复
                4. 优先使用工具获取信息，而不是询问用户
                5. 请注意某些工具的参数是可以为空的，不需要再次向用户询问

                请根据当前对话上下文，决定下一步动作：如果需要调用工具来完成任务，请调用相应的工具。

                """;
        Prompt prompt = Prompt.builder()
                        .messages(this.chatMemory.get(sessionId))
                        .chatOptions(this.chatOptions)
                        .build();
        
        this.lastChatResponse = chatClient
                                .prompt(prompt)
                                .system(thinkPrompt)
                                .toolCallbacks(availableTools != null ? availableTools.toArray(new ToolCallback[0]) : new ToolCallback[0])
                                .call()
                                .chatClientResponse()
                                .chatResponse();
        
        Assert.notNull(lastChatResponse, "Last chat response cannot be null");
        AssistantMessage output = lastChatResponse.getResult().getOutput();
        var toolCalls = output.getToolCalls();

        logToolCalls(toolCalls);

        if (toolCalls.isEmpty()) {
            chatMemory.add(sessionId, output);
            return false;
        }
        return true;
    }

    protected void execute(){
        Assert.notNull(this.lastChatResponse, "Last chat response cannot be null");
        
        if (!this.lastChatResponse.hasToolCalls()) {
            return;
        }

        Prompt prompt = Prompt.builder()
                        .messages(this.chatMemory.get(sessionId))
                        .chatOptions(this.chatOptions)
                        .build();
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, this.lastChatResponse);
        chatMemory.clear(sessionId);
        chatMemory.add(sessionId, toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) toolExecutionResult
                .conversationHistory()
                .get(toolExecutionResult.conversationHistory().size() - 1);
        
        String result = toolResponseMessage.getResponses()
                .stream()
                .map(resp -> "工具 " + resp.name() + " 的返回结果为：" + resp.responseData())
                .collect(Collectors.joining("\n"));
        log.info("工具调用结果：{}", result);


        if (toolResponseMessage.getResponses()
                .stream()
                .anyMatch(resp -> resp.name().equals("terminate"))) {
            this.agentState = AgentState.FINISHED;
            log.info("任务结束");
        }
    }

    protected void step() {
        if (think()) {
            // 有工具调用，执行工具
            execute();
        } else {
            // 没有工具调用，直接结束
            agentState = AgentState.FINISHED;
        }
    }

    @Override
    public String chat(String userInput){
        Assert.notNull(userInput, "用户输入不能为空");
        
        if (agentState != AgentState.IDLE) {
            throw new IllegalStateException("Agent 非空闲状态，当前状态：" + agentState);
        }
        try{
            this.agentState = AgentState.THINKING;
            UserMessage userMessage = new UserMessage(userInput);
            this.chatMemory.add(this.sessionId, userMessage);

            for(int s = 1; s <= MAX_STEPS && agentState != AgentState.FINISHED; s++){
                log.info("\n\n========== Agent Step {} ==========\n", s);
                step();
                if(s == MAX_STEPS){
                    this.agentState = AgentState.FINISHED;
                    log.warn("已达到最大步骤数 {}，强制结束", MAX_STEPS);
                }
            }
            var history = this.chatMemory.get(this.sessionId);

            String AIResponse = history.stream()
                                .filter(msg -> msg instanceof AssistantMessage) // 只留 AI 说的
                                .reduce((first, second) -> second)              // 只取最后一条
                                .map(msg -> ((AssistantMessage) msg).getText()) // 提取文字
                                .orElse("");                             // 没找到就给空字符串;
            log.info("\n\n========== Final AI Response ==========\n{}\n=====================================\n", AIResponse);
            return AIResponse;
        }catch(Exception e){
            this.agentState = AgentState.ERROR;
            throw e;
        }finally {
            this.agentState = AgentState.IDLE;
        }
    }
}
