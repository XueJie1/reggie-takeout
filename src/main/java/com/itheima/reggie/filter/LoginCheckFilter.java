package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*") //
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{   // 放行路径
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        // 2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3. 不需要处理，直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 需要处理，判断登录状态，如果已登录，则放行
        Object empIdObj = request.getSession().getAttribute("employee");
        if (request.getSession().getAttribute("employee") != null && empIdObj instanceof Long) {
            Long empId = (Long) empIdObj;
            if (empId > 0) {    // 检查empId是否大于0
                log.info("用户已登录，id为{}", empId);
                BaseContext.setUserId(empId);
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 5. 未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {
                return match;
            }
        }
        return false;
    }
}
