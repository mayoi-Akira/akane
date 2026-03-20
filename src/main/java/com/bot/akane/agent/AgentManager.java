package com.bot.akane.agent;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgentManager {
    private final ApplicationContext applicationContext;
    private final GroupToolService groupToolService;
    
}
