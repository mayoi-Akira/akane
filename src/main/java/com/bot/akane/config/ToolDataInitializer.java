package com.bot.akane.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bot.akane.agent.toolSettings.ToolDefaultType;
import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.mapper.GroupToolMapper;
import com.bot.akane.model.entity.Tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolDataInitializer implements ApplicationRunner {

    private final ApplicationContext applicationContext;
    private final GroupToolMapper groupToolMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, ToolInterface> toolBeans = applicationContext.getBeansOfType(ToolInterface.class);
        if (toolBeans.isEmpty()) {
            log.warn("未扫描到 ToolInterface 工具 Bean，跳过工具初始化。");
            return;
        }

        // 第一步：更新所有工具列表
        Map<String, ToolInterface> toolMap = new java.util.HashMap<>();
        for (ToolInterface toolBean : toolBeans.values()) {
            Tools tool = new Tools();
            tool.setToolCode(toolBean.getName());
            tool.setToolName(toolBean.getName());
            tool.setDescription(toolBean.getDescription());
            groupToolMapper.insertToolIfAbsent(tool);
            toolMap.put(toolBean.getName(), toolBean);
        }
        log.info("工具列表更新完成，工具数量: {}", toolBeans.size());

        // 第二步：为所有 group 添加不存在的 tool 映射
        List<String> allGroupIds = groupToolMapper.selectAllGroupIds();
        for (String groupId : allGroupIds) {
            for (Map.Entry<String, ToolInterface> entry : toolMap.entrySet()) {
                String toolCode = entry.getKey();
                ToolInterface toolBean = entry.getValue();
                
                // 根据工具的 getType() 值设置启用状态
                ToolDefaultType toolStatus = toolBean.getType();
                groupToolMapper.upsertGroupToolMapping(groupId, toolCode, toolStatus);
            }
        }
        log.info("group_tool_mapping 映射更新完成");

    }
}
