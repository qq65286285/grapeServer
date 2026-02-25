package com.grape.grape.controller;

import com.grape.grape.model.vo.LoginRequest;
import com.grape.grape.service.biz.AuthBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthBizService authBizService;

    /**
     * 用户登录接口
     * @param loginRequest 包含 username 和 password 的请求体
     * @return 登录结果（包含token和用户信息）
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return authBizService.login(loginRequest);
    }

}



