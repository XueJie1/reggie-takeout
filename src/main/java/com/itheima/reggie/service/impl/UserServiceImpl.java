package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit; // 引入 TimeUnit 用于后续可能的缓存过期设置

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mybatis-plus.mail.properties.mail.from}")
    private String from;

    // 验证码缓存（实际项目中建议使用 Redis）
    // 这里为了简化，暂时不实现缓存，验证码的存储和验证将在 Controller 中结合 Session 处理
    // private Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    /**
     * 发送邮箱验证码
     * @param email 目标邮箱
     * @param code  验证码 (由 Controller 生成)
     */
    @Override
    public void sendCode(String email, String code) {
        log.info("准备发送邮箱验证码: email={}, code={}", email, code);

        // 1. 构造邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("【瑞吉外卖】登录验证码");
        message.setText("【瑞吉外卖】您本次的登录验证码是：" + code + "，有效期5分钟，请勿泄露。");

        // 2. 发送邮件
        try {
            mailSender.send(message);
            log.info("邮箱验证码发送成功: {}", email);
        } catch (Exception e) {
            log.error("发送邮件时发生异常：", e);
            // 注意：这里抛出异常，让 Controller 感知发送失败
            throw new CustomException("验证码发送失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     * @param map 包含 email 和 code 的 Map
     * @return 登录成功的用户信息
     */
    @Override
    public User login(Map<String, String> map) {
        String email = map.get("email");
        String code = map.get("code");

        // 验证码的校验逻辑将在 Controller 中完成，这里假设已校验通过

        // 1. 根据邮箱查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = this.getOne(queryWrapper);

        // 2. 如果用户不存在，则自动注册
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setStatus(1); // 默认状态为正常
            // 可以设置默认昵称、头像等
            // user.setName("用户" + email.substring(0, email.indexOf('@')));
            this.save(user);
            log.info("新用户注册成功: email={}", email);
        } else {
            log.info("用户登录成功: email={}", email);
        }

        // 3. 检查用户状态 (如果需要)
        if (user.getStatus() == 0) {
            throw new CustomException("账号已被禁用");
        }

        return user;
    }
}
