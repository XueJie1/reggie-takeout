package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.User;

import java.util.Map;

/**
 * 用户信息 Service 接口
 */
public interface UserService extends IService<User> {

    /**
     * 发送邮箱验证码
     * @param email 目标邮箱
     * @param code  验证码
     */
    void sendCode(String email, String code);

    /**
     * 用户登录
     * @param map 包含 email 和 code 的 Map
     * @return 登录成功的用户信息
     */
    User login(Map<String, String> map);
}
