package com.grape.grape.component;

import com.grape.grape.config.jwttools.JwtUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息工具类
 * 提供静态方法获取当前用户信息
 */
public class UserUtils {

    /**
     * 获取当前请求的HttpServletRequest
     * @return HttpServletRequest对象
     */
    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 从当前请求中获取JWT token
     * @return token字符串，如果没有则返回null
     */
    public static String getCurrentToken() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * 获取当前用户的用户名
     * @return 用户名，如果无法获取则返回null
     */
    public static String getCurrentUsername() {
        String token = getCurrentToken();
        if (token == null) {
            return null;
        }
        
        try {
            return JwtUtils.getClaim(token, "username");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前用户的ID
     * @return 用户ID，如果无法获取则返回null
     */
    public static String getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        
        // 这里可以根据用户名查询数据库获取用户ID
        // 暂时返回一个默认值，实际项目中需要根据具体实现修改
        // 例如：return userService.getUserIdByUsername(username);
        return "1";
    }
    
    /**
     * 获取当前登录用户的ID，通过查询数据库
     * @param userService 用户服务
     * @return 用户ID，如果无法获取则返回null
     */
    public static String getCurrentLoginUserId(com.grape.grape.service.UserService userService) {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        
        try {
            // 根据用户名查询用户信息
            com.grape.grape.entity.User user = userService.findByUsername(username);
            if (user != null && user.getId() != null) {
                return user.getId();
            }
        } catch (Exception e) {
            // 忽略异常，返回null
        }
        
        return null;
    }
}
