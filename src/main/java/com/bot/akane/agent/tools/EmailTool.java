package com.bot.akane.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolSettings.ToolDefaultType;
import com.bot.akane.agent.toolSettings.ToolInterface;
import com.bot.akane.agent.toolsService.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor 
public class EmailTool implements ToolInterface{
    
    private final EmailService emailService;

    @Override
    public String getName(){return "EmailTool";}

    @Override
    public String getDescription(){return "用于发送邮件的工具，发送邮件给指定的收件人。发送采用异步方式，不会阻塞。";}

    @Override
    public ToolDefaultType getType() {
        return ToolDefaultType.ENABLE;
    }

    @Tool(name="sendEmail", description="发送邮件给指定的收件人，参数包括：to（收件人邮箱地址），subject（邮件主题），content（邮件内容）。邮件采用异步方式发送，工具调用会立即返回，实际发送在后台执行。")
    public String sendEmail(String to, String subject, String content) {
        if (to == null || to.trim().isEmpty()) {
            return "错误：收件人邮箱地址不能为空";
        }
        if (subject == null || subject.trim().isEmpty()) {
            return "错误：邮件主题不能为空";
        }
        if (content == null || content.trim().isEmpty()) {
            return "错误：邮件内容不能为空";
        }
        if (!to.contains("@")) {
            return "错误：收件人邮箱地址格式不正确";
        }
        emailService.sendEmailAsync(to, subject, content);
        return String.format("邮件已提交发送！\n收件人: %s\n主题: %s\n邮件正在后台异步发送中...", to, subject);
    }
}
