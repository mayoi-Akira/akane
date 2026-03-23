package com.bot.akane.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bot.akane.model.entity.GroupToolConfig;
import com.bot.akane.model.request.ToolUpdateRequest;
import com.bot.akane.model.response.ApiResponse;
import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
@Slf4j
public class ToolsController {
    private final GroupToolService groupToolService;
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<GroupToolConfig>>> getGroupToolMappings(@RequestParam String groupId) {
        if (groupId == null || groupId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "群聊ID不能为空。", List.of()));
        }
        
        List<GroupToolConfig> configs = groupToolService.getGroupToolMappings(groupId);
        log.info("获取群聊工具配置成功，群聊ID: {}", configs.toString());
        return ResponseEntity.ok(ApiResponse.success("获取成功", configs));
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateGroupTools(@RequestBody ToolUpdateRequest request) {
        if (request.getGroupId() == null || request.getGroupId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "群聊ID不能为空。", null));
        }
        if (request.getToolCodes() == null || request.getToolCodes().length == 0) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "工具列表不能为空。", null));
        }
        if (request.getEnable() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "启用状态不能为空。", null));
        }

        String resultMessage = groupToolService.updateToolsForGroup(request.getGroupId(), request.getToolCodes(), request.getEnable());
        return ResponseEntity.ok(ApiResponse.success( resultMessage, null));

    }
}
