package com.bot.akane.config;

import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bot.akane.agent.tools.ToolInterface;
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

        for (ToolInterface toolBean : toolBeans.values()) {
            Tools tool = new Tools();
            tool.setToolCode(toolBean.getName());
            tool.setToolName(toolBean.getName());
            tool.setDescription(toolBean.getDescription());
            groupToolMapper.insertToolIfAbsent(tool);
        }

        // 所有已存在群默认可用全部工具
        groupToolMapper.enableAllMappingsForAllGroups();
        groupToolMapper.enableAllToolsForAllGroups();

        log.info("工具初始化完成，工具数量: {}，已为所有群启用全部工具", toolBeans.size());
    }
}
