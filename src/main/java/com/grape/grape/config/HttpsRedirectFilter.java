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

        // 添加安全头部，允许混合内容
        httpResponse.setHeader("Content-Security-Policy", "upgrade-insecure-requests");
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // 检查请求是否来自 HTTPS 前端（通过 Referer 或 Origin 头）
        String referer = httpRequest.getHeader("Referer");
        String origin = httpRequest.getHeader("Origin");

        // 如果请求来自 HTTPS 前端，但当前是 HTTP 请求
        if ((referer != null && referer.startsWith("https://")) || 
            (origin != null && origin.startsWith("https://"))) {
            // 构建 HTTPS URL
            String serverName = httpRequest.getServerName();
            int serverPort = httpRequest.getServerPort();
            String requestURI = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();

            // 构建完整的 HTTPS URL
            StringBuilder httpsUrl = new StringBuilder();
            httpsUrl.append("https://").append(serverName);
            // 如果不是默认 HTTPS 端口（443），则添加端口号
            if (serverPort != 443) {
                httpsUrl.append(":").append(serverPort);
            }
            httpsUrl.append(requestURI);
            if (queryString != null) {
                httpsUrl.append("?").append(queryString);
            }

            // 重定向到 HTTPS URL
            httpResponse.sendRedirect(httpsUrl.toString());
            return;
        }

        // 继续处理请求
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
