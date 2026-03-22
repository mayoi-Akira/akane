package com.bot.akane.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bot.akane.agent.AgentManager;
import com.bot.akane.model.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/agent")
@RequiredArgsConstructor 
public class AgentController {
    
    private final AgentManager agentManager;

    @GetMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetAgent(@RequestParam String groupId) {
        log.info("Received request to reset agent.");
        try {
            String result = agentManager.resetAgent(groupId);
            log.info("Agent reset successfully.");
            return ResponseEntity.ok(ApiResponse.success(result, null));
        } catch (Exception e) {
            log.error("Error resetting agent: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "Agent重置失败，请稍后再试", null));
        }
    }
}
