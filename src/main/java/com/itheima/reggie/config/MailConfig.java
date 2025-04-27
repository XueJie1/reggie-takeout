package com.itheima.reggie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mybatis-plus.mail.host}")
    private String host;

    @Value("${mybatis-plus.mail.port}")
    private int port;

    @Value("${mybatis-plus.mail.username}")
    private String username;

    @Value("${mybatis-plus.mail.password}")
    private String password;

    @Value("${mybatis-plus.mail.properties.mail.smtp.ssl.enable}")
    private boolean sslEnable;

    // 如果 application.yml 中有配置 'mail.smtp.auth' 和 'mail.transport.protocol'，也可以注入
    // @Value("${spring.mail.properties.mail.smtp.auth}")
    // private String smtpAuth;
    // @Value("${spring.mail.properties.mail.transport.protocol}")
    // private String transportProtocol;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password); // 使用授权码

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp"); // 显式设置协议
        props.put("mail.smtp.auth", "true"); // 启用 SMTP 认证
        props.put("mail.smtp.ssl.enable", String.valueOf(sslEnable)); // 根据配置启用 SSL
        // 根据需要可以添加其他属性，例如超时设置
        // props.put("mail.smtp.connectiontimeout", "5000");
        // props.put("mail.smtp.timeout", "5000");
        // props.put("mail.smtp.writetimeout", "5000");
        // props.put("mail.debug", "true"); // 开启 debug 日志，方便排查问题

        return mailSender;
    }
}
