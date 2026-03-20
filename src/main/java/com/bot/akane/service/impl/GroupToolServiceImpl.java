package com.bot.akane.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bot.akane.mapper.GroupToolMapper;
import com.bot.akane.model.entity.Tools;
import com.bot.akane.service.GroupToolService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupToolServiceImpl implements GroupToolService {

	private final GroupToolMapper groupToolMapper;

	@Override
	public String getAvailableTools() {
		List<Tools> tools = groupToolMapper.selectAllTools();
		if (tools == null || tools.isEmpty()) {
			return "暂无可用工具。";
		}

		String details = tools.stream()
				.map(this::formatToolLine)
				.collect(Collectors.joining("\n"));
		return "全部可用工具:\n" + details;
	}

	@Override
	@Transactional
	public String getToolsForGroup(String groupId) {
		Long parsedGroupId = parseGroupId(groupId);
		if (parsedGroupId == null) {
			return "群聊ID格式错误。";
		}

		List<Tools> tools = groupToolMapper.selectEnabledToolsByGroupId(parsedGroupId);
		if (tools == null || tools.isEmpty()) {
			groupToolMapper.insertGroupConfigIfAbsent(parsedGroupId);
			groupToolMapper.enableMappingsByGroupId(parsedGroupId);
			groupToolMapper.enableAllToolsForGroup(parsedGroupId);
			tools = groupToolMapper.selectEnabledToolsByGroupId(parsedGroupId);
		}

		if (tools == null || tools.isEmpty()) {
			return "暂无可用工具。";
		}

		String details = tools.stream()
				.map(this::formatToolLine)
				.collect(Collectors.joining("\n"));
		return "群 " + parsedGroupId + " 的启用工具:\n" + details;
	}

	@Override
	public String getToolDetails(String toolName) {
		if (toolName == null || toolName.trim().isEmpty()) {
			return "工具名称不能为空。";
		}

		Tools tool = groupToolMapper.selectToolByCode(toolName.trim());
		if (tool == null) {
			return "未找到工具: " + toolName;
		}

		return "工具详情:\n"
				+ "toolCode: " + tool.getToolCode() + "\n"
				+ "toolName: " + nullToEmpty(tool.getToolName()) + "\n"
				+ "description: " + nullToEmpty(tool.getDescription());
	}

	@Override
	@Transactional
	public String updateToolsForGroup(String groupId, String[] toolNames) {
		Long parsedGroupId = parseGroupId(groupId);
		if (parsedGroupId == null) {
			return "群聊ID格式错误。";
		}

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

		groupToolMapper.insertGroupConfigIfAbsent(parsedGroupId);
		groupToolMapper.disableMappingsByGroupId(parsedGroupId);
		normalizedToolCodes.forEach(toolCode ->
				groupToolMapper.upsertGroupToolMapping(parsedGroupId, toolCode, true));

		return "群 " + parsedGroupId + " 工具更新成功，已启用: " + String.join(", ", normalizedToolCodes);
	}

	private String formatToolLine(Tools tool) {
		return "- " + tool.getToolCode() + " (" + nullToEmpty(tool.getToolName()) + "): " + nullToEmpty(tool.getDescription());
	}

	private String nullToEmpty(String text) {
		return text == null ? "" : text;
	}

	private Long parseGroupId(String groupId) {
		if (groupId == null || groupId.trim().isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(groupId.trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}
}
