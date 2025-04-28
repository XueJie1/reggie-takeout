// LoginInterceptor.java
package com.itheima.reggie.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 session 中获取用户 ID (优先检查 "user"，然后是 "userId")
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId == null) {
            userId = (Long) request.getSession().getAttribute("userId");
        }
        // 设置到 ThreadLocal 中
        BaseContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.clear(); // 清理 ThreadLocal
    }
}
