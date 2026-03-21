package com.bot.akane.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bot.akane.agent.toolSettings.ToolType;
import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.mapper.GroupToolMapper;
import com.bot.akane.model.entity.Tool;

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

        // 第一步：初始化所有工具到数据库
        for (ToolInterface toolBean : toolBeans.values()) {
            Tool tool = new Tool();
            tool.setToolCode(toolBean.getCode());
            tool.setToolName(toolBean.getName());
            tool.setDescription(toolBean.getDescription());
            tool.setToolType(toolBean.getType());
            groupToolMapper.insertToolIfAbsent(tool);
        }
        log.info("工具初始化完成，工具数量: {}", toolBeans.size());

        // 第二步：为所有群组配置工具映射
        List<String> allGroupIds = groupToolMapper.selectAllGroupIds();
        for (String groupId : allGroupIds) {
            for (ToolInterface toolBean : toolBeans.values()) {
                String toolCode = toolBean.getCode();
                ToolType toolStatus = toolBean.getType();
                groupToolMapper.upsertGroupToolConfig(groupId, toolCode, toolStatus);
            }
        }
        log.info("群组工具配置初始化完成");
    }
}
