package com.grape.grape.config;


import com.grape.grape.config.jwttools.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * @author 鈥済in"
 */
@Configuration
public class CorsConfig implements  WebMvcConfigurer {

        @Autowired
        private JwtInterceptor jwtInterceptor;

        @Autowired
        private RequestLoggingInterceptor requestLoggingInterceptor;
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            // 允许所有路径
            registry.addMapping("/**")
                    // 允许前端的域名
                    .allowedOrigins("http://127.0.0.1:8080")
                    .allowedOrigins("http://127.0.0.1:8309")
                    // 允许前端的域名
                    .allowedOrigins("http://localhost:8080")
                    .allowedOrigins("http://localhost:8309")
                    .allowedOrigins("http://10.251.64.24:8080")
                    // 允许前端的域名
                    .allowedOrigins("http://192.168.23.168:8080")
                    .allowedOriginPatterns("http://192.168.*.*:*", "https://192.168.*.*:*")
                    .allowedOriginPatterns("http://*.*.*.*:*")
                    // 允许的 HTTP 方法
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    // 允许所有请求头
                    .allowedHeaders("*")
                    // 允许携带凭证
                    .allowCredentials(true)
                    // 预检请求的缓存时间
                    .maxAge(3600);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加请求日志拦截器(优先执行)
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**");

        // 添加JWT认证拦截器
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 豁免不需要校验的路径
                .excludePathPatterns("/user/register", "/auth/login", "/captcha/**", "/regedit/**", "/api/regedit/**")
                // 豁免Swagger相关路径
                .excludePathPatterns("/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/doc.html", "/webjars/**")
                // 豁免其他不需要token的接口
                .excludePathPatterns("/phone/**", "/image/**", "/folder/**", "/product/**", "/fileInfo/**")
                .excludePathPatterns("/environments/**", "/cases/**", "/caseVersions/**", "/caseExecutions/**")
                .excludePathPatterns("/deviceInfo/**", "/role/**", "/permission/**", "/userRole/**", "/rolePermission/**");
        }


}

