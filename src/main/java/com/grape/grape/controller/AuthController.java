package com.grape.grape.controller;

import com.grape.grape.model.vo.LoginRequest;
import com.grape.grape.service.biz.AuthBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证控制器
 * 提供用户认证相关的API接口，包括登录、登出等功能
 * 
 * @author grape-team
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * 认证业务服务
     * 处理用户认证相关的业务逻辑，包括登录验证、令牌生成等
     */
    @Autowired
    private AuthBizService authBizService;

    /**
     * 用户登录接口
     * 
     * 功能说明：
     * 1. 接收用户提交的用户名和密码
     * 2. 调用业务服务层进行身份验证
     * 3. 验证成功后生成JWT令牌
     * 4. 返回包含令牌和用户信息的响应数据
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析请求体中的登录信息
     * - 参数校验：验证用户名和密码是否为空
     * - 调用服务：调用AuthBizService.login()方法处理登录逻辑
     * - 返回结果：返回包含token、userId、username等信息的响应
     * - 结束：将响应数据以JSON格式返回给客户端
     * 
     * 调用服务：
     * - 调用 authBizService.login(loginRequest) 执行登录业务逻辑
     *   该方法会查询数据库验证用户身份，并生成JWT令牌
     * 
     * 数据库调用：
     * - 通过AuthBizService间接调用UserService查询用户信息
     * - 在UserService中执行SQL查询：SELECT * FROM user WHERE username = ?
     * - 验证密码匹配后，返回用户完整信息
     * 
     * @param loginRequest 登录请求对象，包含以下字段：
     *                    - username: 用户名（必填）
     *                    - password: 密码（必填）
     * @return ResponseEntity<Map<String, Object>> 响应实体，包含以下信息：
     *         - 成功时返回200状态码和用户信息：
     *           * token: JWT访问令牌
     *           * userId: 用户ID
     *           * username: 用户名
     *         - 失败时返回相应的错误状态码和错误信息：
     *           * 400: 参数错误（用户名或密码为空）
     *           * 401: 认证失败（用户不存在或密码错误）
     *           * 403: 账户禁用（用户状态异常）
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 方法开始：接收登录请求，准备调用业务服务
        
        // 调用业务服务层处理登录逻辑
        // 该调用会触发以下操作：
        // 1. 参数验证：检查用户名和密码是否有效
        // 2. 数据库查询：从user表查询用户信息
        // 3. 密码验证：使用BCrypt验证密码
        // 4. 令牌生成：生成JWT访问令牌
        // 5. 响应构建：组装返回给客户端的数据
        ResponseEntity<Map<String, Object>> response = authBizService.login(loginRequest);
        
        // 方法结束：返回登录结果给客户端
        return response;
    }

    /**
     * 用户登出接口
     * 
     * 功能说明：
     * 1. 验证用户的JWT令牌有效性
     * 2. 将令牌加入黑名单（可选实现）
     * 3. 清除用户会话信息
     * 4. 返回登出成功响应
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，从请求头中获取JWT令牌
     * - 令牌验证：验证JWT令牌的有效性和过期时间
     * - 会话清理：清除服务器端的用户会话数据
     * - 返回结果：返回登出成功的确认信息
     * - 结束：完成登出流程
     * 
     * 调用服务：
     * - 调用 JwtUtils.validateToken() 验证令牌有效性
     * - 调用 SessionService.clearSession() 清除用户会话（如果实现）
     * 
     * 数据库调用：
     * - 可选：更新user表的最后登出时间字段
     * - 可选：将已登出的令牌加入黑名单表
     * 
     * @return ResponseEntity<Map<String, Object>> 响应实体，包含以下信息：
     *         - 成功时返回200状态码和成功消息
     *         - 失败时返回相应的错误状态码和错误信息
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // 方法开始：处理用户登出请求
        
        // TODO: 实现登出逻辑
        // 1. 获取请求头中的JWT令牌
        // 2. 验证令牌有效性
        // 3. 将令牌加入黑名单
        // 4. 清除用户会话
        
        // 方法结束：返回登出结果
        return ResponseEntity.ok().body(Map.of(
            "message", "登出成功",
            "code", 200
        ));
    }

    /**
     * 刷新令牌接口
     * 
     * 功能说明：
     * 1. 接收过期的JWT令牌
     * 2. 验证令牌的有效性（虽然已过期，但签名必须正确）
     * 3. 生成新的JWT令牌
     * 4. 返回新的令牌信息
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，从请求体中获取刷新令牌
     * - 令牌验证：验证刷新令牌的签名和用户身份
     * - 令牌生成：使用用户信息生成新的访问令牌
     * - 返回结果：返回新的令牌给客户端
     * - 结束：完成令牌刷新流程
     * 
     * 调用服务：
     * - 调用 JwtUtils.refreshToken() 生成新令牌
     * - 调用 UserService.getUserById() 获取用户信息
     * 
     * 数据库调用：
     * - 查询user表获取用户最新信息
     * - 可选：更新user表的最后活跃时间
     * 
     * @param refreshToken 刷新令牌字符串
     * @return ResponseEntity<Map<String, Object>> 响应实体，包含新的访问令牌信息
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> refreshToken) {
        // 方法开始：处理令牌刷新请求
        
        // TODO: 实现令牌刷新逻辑
        // 1. 验证刷新令牌的有效性
        // 2. 从令牌中提取用户信息
        // 3. 查询用户最新信息
        // 4. 生成新的访问令牌
        // 5. 返回新令牌
        
        // 方法结束：返回刷新结果
        return ResponseEntity.ok().body(Map.of(
            "message", "令牌刷新成功",
            "code", 200
        ));
    }
}
