package com.grape.grape.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 请求日志拦截器
 * 记录所有HTTP请求的详细信息,方便排查连接问题
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    // 使用ThreadLocal存储请求开始时间
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        startTime.set(System.currentTimeMillis());

        // 记录请求信息
        log.info("=== Request Start ===");
        log.info("Request URL: {} {}", request.getMethod(), request.getRequestURI());
        log.info("Client IP: {}", getClientIP(request));
        log.info("User-Agent: {}", request.getHeader("User-Agent"));

        // 记录请求参数
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            log.info("Request Params: {}", queryString);
        }

        // 记录所有请求头
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // 不记录敏感信息
            if ("authorization".equalsIgnoreCase(headerName)) {
                headers.append(headerName).append("=").append("[PROTECTED]").append("; ");
            } else {
                headers.append(headerName).append("=").append(headerValue).append("; ");
            }
        }
        log.info("Request Headers: {}", headers.toString());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                          ModelAndView modelAndView) {
        // 可以在这里添加处理后的日志
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                               Exception ex) {
        Long start = startTime.get();
        long duration = 0;
        if (start != null) {
            duration = System.currentTimeMillis() - start;
            startTime.remove();
        }

        log.info("=== Request End ===");
        log.info("Response Status: {}", response.getStatus());
        log.info("Processing Time: {}ms", duration);

        // 如果有异常,记录异常信息
        if (ex != null) {
            log.error("Request Processing Exception: {} - {}", request.getMethod(), request.getRequestURI(), ex);
        }

        // 如果响应状态是错误状态,记录警告
        int status = response.getStatus();
        if (status >= 400) {
            log.warn("Request Failed: {} {} Return Status Code {}",
                request.getMethod(), request.getRequestURI(), status);
        }

        log.info("================");
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
