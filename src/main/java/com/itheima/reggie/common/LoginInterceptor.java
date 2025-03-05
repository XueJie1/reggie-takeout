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
        // 从 session 或 token 中获取用户 ID
        Long userId = (Long) request.getSession().getAttribute("userId");
        // 无论 userId 是否为 null，都设置到 ThreadLocal 中
        BaseContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.clear(); // 清理 ThreadLocal
    }
}
