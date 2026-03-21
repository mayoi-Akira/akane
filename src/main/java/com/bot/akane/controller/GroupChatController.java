package com.bot.akane.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bot.akane.exception.AgentException;
import com.bot.akane.model.request.ChatRequest;
import com.bot.akane.model.response.ApiResponse;
import com.bot.akane.service.GroupChatService;
import com.bot.akane.util.TraceIdUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupChatController {
    private final GroupChatService groupChatService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, Object>>> chat(@RequestBody ChatRequest request) {
        log.info("Chat request received: {}", request);
        String groupId = request.getGroupId();
        String userInput = request.getUserInput();
        
        if(groupId == null || groupId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "群聊ID不能为空。", Map.of()));
        }
        if(userInput == null || userInput.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "消息内容不能为空。", Map.of()));
        }

        try {
            TraceIdUtil.setGroupId(groupId);
            String response = groupChatService.chat(groupId, userInput);
            return ResponseEntity.ok(ApiResponse.success("聊天成功", Map.of(
                    "groupId", groupId.trim(),
                    "reply", response
            )));
        } catch (AgentException e) {
            log.error("Agent business exception, groupId={}, errorCode={}", groupId, e.getErrorCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(500, "聊天服务执行失败：" + e.getErrorMessage(), Map.of()));
        } catch (Exception e) {
            log.error("Unexpected exception in chat controller, groupId={}", groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(500, "聊天服务暂时不可用，请稍后重试。", Map.of()));
        }
    }
}
