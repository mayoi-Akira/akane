package com.bot.akane.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bot.akane.model.entity.GroupToolMapping;
import com.bot.akane.model.response.ApiResponse;
import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
public class ToolsController {
    private final GroupToolService groupToolService;
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<GroupToolMapping>>> getGroupToolMappings(@RequestParam String groupId) {
        if (groupId == null || groupId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(400, "群聊ID不能为空。", List.of()));
        }
        
        List<GroupToolMapping> mappings = groupToolService.getGroupToolMappings(groupId);
        return ResponseEntity.ok(ApiResponse.success("获取成功", mappings));
    }
}
