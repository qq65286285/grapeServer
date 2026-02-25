package com.grape.grape.service.biz;

import com.grape.grape.model.vo.LoginRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * 认证业务服务接口
 * 用于处理用户认证相关的业务逻辑
 */
public interface AuthBizService {

    /**
     * 用户登录
     * @param loginRequest 登录请求参数
     * @return 登录结果，包含token和用户信息
     */
    ResponseEntity<Map<String, Object>> login(LoginRequest loginRequest);

}
