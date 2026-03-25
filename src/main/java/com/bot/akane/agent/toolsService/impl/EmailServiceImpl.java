package com.bot.akane.agent.toolsService.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bot.akane.agent.toolsService.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    @Async
    public void sendEmailAsync(String to, String subject, String content) {
        try {
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom(from);

            // 发送邮件
            System.out.println("mail password=" + from);
            mailSender.send(message);

            log.info("异步发送邮件成功，收件人: {}, 主题: {}", to, subject);
        } catch (Exception e) {
            log.error("异步发送邮件失败，收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage(), e);
        }
       
    }
}