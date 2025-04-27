package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.common.CustomException; // 引入 CustomException
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils; // 使用 commons-lang 校验
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate; // 引入 StringRedisTemplate
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random; // 用于生成验证码
import java.util.concurrent.TimeUnit; // 用于设置 Redis 有效期

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate; // 注入 StringRedisTemplate

    /**
     * 发送邮箱验证码
     * @param user 包含 email 的 User 对象 (前端通常会传一个对象)
     * @return 操作结果
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
        // 1. 获取邮箱号
        String email = user.getEmail();

        if (StringUtils.isNotEmpty(email)) {
            // 2. 生成随机的4位验证码
            String code = String.valueOf(new Random().nextInt(9000) + 1000); // 生成4位验证码
            log.info("Controller 生成邮箱验证码: email={}, code={}", email, code);

            try {
                // 3. 调用 Service 发送邮件
                // userService.sendCode(email, code);

                // 4. 将生成的验证码保存到 Redis，使用 "vCode_" + email 作为 Key，并设置有效期为 5 分钟
                String redisKey = "vCode_" + email;
                redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
                log.info("将验证码存入 Redis: key={}, code={}", redisKey, code);

                return R.success("邮箱验证码发送成功");

            } catch (CustomException e) {
                // 如果 Service 抛出异常 (例如邮件发送失败)
                log.error("发送验证码失败: {}", e.getMessage());
                return R.error(e.getMessage()); // 将 Service 的错误信息返回给前端
            } catch (Exception e) {
                // 其他未知异常
                log.error("发送验证码时发生未知异常", e);
                return R.error("验证码发送失败，请稍后重试");
            }
        }

        return R.error("邮箱地址不能为空"); // 或者其他合适的错误信息
    }

    /**
     * 移动端用户登录
     * @param map 包含 email 和 code 的 Map
     * @param session HttpSession 对象，用于存储用户登录状态
     * @return 登录结果，包含用户信息
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        log.info("用户登录请求参数: {}", map);

        // 1. 获取邮箱
        String email = map.get("email");

        // 2. 获取验证码
        String code = map.get("code");

        // 3. 从 Redis 中获取保存的验证码，使用 "vCode_" + email 作为 Key
        String redisKey = "vCode_" + email;
        String codeInRedis = redisTemplate.opsForValue().get(redisKey);
        log.info("从 Redis 获取验证码: key={}, value={}", redisKey, codeInRedis);

        // 4. 进行验证码的比对 (页面提交的验证码和 Redis 中保存的验证码比对)
        if (codeInRedis != null && codeInRedis.equals(code)) {
            // 5. 如果能够比对成功，说明登录成功
            // 调用 Service 层进行登录或注册
            User user = userService.login(map); // Service 层现在只负责查库和注册

            // 6. 将登录用户 id 存入 Session
            session.setAttribute("user", user.getId());

            // 登录成功后，可以从 Redis 中删除验证码
            log.info("登录成功，从 Redis 移除验证码: key={}", redisKey);
            redisTemplate.delete(redisKey);

            return R.success(user);
        }

        return R.error("登录失败，验证码错误");
    }

    /**
     * 用户退出登录
     * @param session HttpSession 对象
     * @return 操作结果
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        // 清理 Session 中保存的当前登录用户的 id
        session.removeAttribute("user");
        log.info("用户退出登录");
        return R.success("退出成功");
    }
}
