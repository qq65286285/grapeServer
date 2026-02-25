package com.grape.grape.service.biz;

import com.grape.grape.config.jwttools.JwtUtils;
import com.grape.grape.entity.User;
import com.grape.grape.model.vo.LoginRequest;
import com.grape.grape.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证业务服务实现类
 * 实现用户认证相关的业务逻辑
 */
@Service
public class AuthBizServiceImpl implements AuthBizService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<Map<String, Object>> login(LoginRequest loginRequest) {
        // 1. 参数校验
        if (!StringUtils.hasText(loginRequest.getUsername()) || !StringUtils.hasText(loginRequest.getPassword())) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "用户名和密码不能为空")
            );
        }

        // 2. 查询用户
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("error", "用户不存在")
            );
        }

        // 3. 验证密码（使用BCrypt加密验证）
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("error", "密码错误")
            );
        }

        // 4. 验证用户状态（如禁用状态）
        // if (user.getStatus() != 1) { // 假设1表示激活状态
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
        //             Collections.singletonMap("error", "账户已被禁用")
        //     );
        // }

        // 5. 生成JWT令牌
        String token = JwtUtils.createToken(user.getUsername());

        // 6. 返回登录结果
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        // response.put("roles", getUserRoles(user.getId()));  // 获取用户角色

        return ResponseEntity.ok(response);
    }

}
