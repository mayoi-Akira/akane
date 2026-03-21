package com.bot.akane.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.mapper.GroupToolMapper;
import com.bot.akane.model.entity.Tool;
import com.bot.akane.model.entity.GroupToolConfig;
import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupToolServiceImpl implements GroupToolService {

	private final GroupToolMapper groupToolMapper;

	@Override
	public List<String> getAvailableTools() {
		List<Tool> tools = groupToolMapper.selectAllTools();
		if (tools == null || tools.isEmpty()) {
			log.info("暂无可用工具。");
			return List.of();
		}

		String details = tools.stream()
				.map(this::formatToolLine)
				.collect(Collectors.joining("\n"));
		log.info("全部可用工具:\n{}", details);
		return tools.stream().map(Tool::getToolCode).toList();
	}

	@Override
	@Transactional
	public List<String> getToolsForGroup(String groupId) {
		if (groupId == null || groupId.trim().isEmpty()) {
			log.warn("群聊ID格式错误，无法获取群工具列表。");
			return List.of();
		}
		String cleanGroupId = groupId.trim();

		List<Tool> tools = groupToolMapper.selectEnabledToolsByGroupId(cleanGroupId);
		if (tools == null || tools.isEmpty()) {
			groupToolMapper.insertGroupIfAbsent(cleanGroupId);
			groupToolMapper.enableAllToolsForGroup(cleanGroupId);
			tools = groupToolMapper.selectEnabledToolsByGroupId(cleanGroupId);
		}

		if (tools == null || tools.isEmpty()) {
			log.info("群 " + cleanGroupId + " 暂无可用工具。");
			return List.of();
		}

		String details = tools.stream()
				.map(this::formatToolLine)
				.collect(Collectors.joining("\n"));
		log.info("群 " + cleanGroupId + " 的启用工具:\n" + details);
		return tools.stream().map(Tool::getToolCode).toList();
	}

	@Override
	@Transactional
	public String updateToolsForGroup(String groupId, String[] toolNames) {
		if (groupId == null || groupId.trim().isEmpty()) {
			return "群聊ID不能为空。";
		}
		String cleanGroupId = groupId.trim();

		if (toolNames == null || toolNames.length == 0) {
			return "工具列表不能为空。";
		}

		List<String> normalizedToolCodes = Arrays.stream(toolNames)
				.filter(name -> name != null && !name.trim().isEmpty())
				.map(String::trim)
				.distinct()
				.toList();

		if (normalizedToolCodes.isEmpty()) {
			return "工具列表不能为空。";
		}

		List<String> existingToolCodes = groupToolMapper.selectExistingToolCodes(normalizedToolCodes);
		Set<String> existingSet = Set.copyOf(existingToolCodes);
		List<String> missingToolCodes = normalizedToolCodes.stream()
				.filter(code -> !existingSet.contains(code))
				.toList();
		if (!missingToolCodes.isEmpty()) {
			return "以下工具不存在: " + String.join(", ", missingToolCodes);
		}

		groupToolMapper.insertGroupIfAbsent(cleanGroupId);
		groupToolMapper.disableAllToolsForGroup(cleanGroupId);
		normalizedToolCodes.forEach(toolCode ->
				groupToolMapper.upsertGroupToolConfig(cleanGroupId, toolCode, ToolType.ENABLE));

		return "群 " + cleanGroupId + " 工具更新成功，已启用: " + String.join(", ", normalizedToolCodes);
	}

	@Override
	public List<GroupToolConfig> getGroupToolMappings(String groupId) {
		if (groupId == null || groupId.trim().isEmpty()) {
			log.warn("群聊ID不能为空。");
			return List.of();
		}
		return groupToolMapper.selectGroupToolConfigsByGroupId(groupId.trim());
	}

	private String formatToolLine(Tool tool) {
		return "- " + tool.getToolCode() + " (" + nullToEmpty(tool.getToolName()) + "): " + nullToEmpty(tool.getDescription());
	}

	private String nullToEmpty(String text) {
		return text == null ? "" : text;
	}
}
