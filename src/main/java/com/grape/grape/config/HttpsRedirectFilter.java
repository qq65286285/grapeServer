package com.grape.grape.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HTTPS 重定向过滤器
 * 用于处理混合内容问题
 */
@Component
public class HttpsRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 检查请求是否是 Swagger 相关资源
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.contains("/swagger-ui") || requestURI.contains("/v3/api-docs")) {
            // 对于 Swagger 相关资源，不进行重定向，也不添加 upgrade-insecure-requests 头部
            // 这样 Swagger UI 会使用相对路径加载资源，避免 HTTPS 错误
            chain.doFilter(request, response);
            return;
        }

        // 添加安全头部，允许混合内容
        httpResponse.setHeader("Content-Security-Policy", "upgrade-insecure-requests");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // 继续处理请求，不进行任何重定向
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法
    }

    @Override
    public void destroy() {
        // 销毁方法
    }
}
