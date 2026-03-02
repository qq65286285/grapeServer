package com.grape.grape.controller;

import com.grape.grape.entity.User;
import com.grape.grape.model.Resp;
import com.grape.grape.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 提供用户管理相关的API接口，包括用户注册、查询等功能
 * 
 * 主要功能：
 * 1. 用户注册
 * 2. 用户信息查询
 * 3. 用户名模糊查询
 * 
 * @author grape-team
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * 用户数据访问服务
     * 负责用户数据的CRUD操作和业务逻辑处理
     * 
     * 调用说明：
     * - 调用该服务的register()方法注册新用户
     * - 调用该服务的getById()方法查询用户信息
     * - 调用该服务的findByUsernameLike()方法模糊查询用户
     * 
     * 数据库调用：
     * - SELECT: 查询用户信息
     * - INSERT: 插入新用户
     * - UPDATE: 更新用户信息
     * - DELETE: 删除用户
     */
    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * 
     * 功能说明：
     * 1. 接收用户注册信息（用户名、密码、邮箱等）
     * 2. 验证用户信息的有效性
     * 3. 检查用户名是否已存在
     * 4. 对密码进行加密处理
     * 5. 将用户信息保存到数据库
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，解析用户注册信息
     * - 参数校验：验证用户名、密码等必填字段的完整性
     * - 重复性检查：检查用户名是否已被注册
     * - 密码加密：使用BCrypt算法加密用户密码
     * - 数据保存：将用户信息保存到数据库
     * - 响应构建：返回注册结果
     * - 结束：完成用户注册流程
     * 
     * 调用服务：
     * - 调用 userService.register(user) 注册新用户
     *   该方法内部会执行以下操作：
     *   1. 参数校验：验证用户名、密码等字段的有效性
     *   2. 重复性检查：SELECT COUNT(*) FROM user WHERE username = ?
     *   3. 密码加密：使用BCryptPasswordEncoder加密密码
     *   4. 数据保存：INSERT INTO user (...) VALUES (...)
     *   5. 返回注册结果
     * 
     * 调用外部服务：
     * - 调用 BCryptPasswordEncoder 加密密码
     *   BCrypt是Spring Security提供的密码加密工具
     *   特点：每次加密结果不同，包含盐值，安全性高
     * 
     * 数据库调用：
     * - 查询用户名是否存在：SELECT COUNT(*) FROM user WHERE username = ?
     *   参数：username（用户名）
     *   返回值：用户数量（0表示可用，>0表示已存在）
     * - 插入用户：INSERT INTO user (...) VALUES (...)
     *   参数：username, password, email, phone, created_time, created_by等
     *   返回值：受影响的行数（1表示成功）
     * 
     * @param user 用户注册信息，包含以下字段：
     *              - username: 用户名（必填，唯一）
     *              - password: 密码（必填，会自动加密）
     *              - email: 邮箱（选填）
     *              - phone: 手机号（选填）
     *              - nickname: 昵称（选填）
     * @return Resp 响应对象，包含注册结果
     *         - 成功时：code=0, data=User对象（不包含密码）
     *         - 失败时：code=400, message=错误描述（如"用户名已存在"）
     *         - 失败时：code=500, message=服务器内部错误
     */
    @PostMapping("register")
    public Resp register(@RequestBody User user) {
        // 方法开始：准备用户注册
        log.info("开始用户注册，用户名: {}", user.getUsername());
        
        try {
            // 调用服务层注册用户
            // 该调用会：
            // 1. 验证用户信息
            // 2. 检查用户名是否已存在
            // 3. 加密用户密码
            // 4. 保存用户信息到数据库
            User registeredUser = userService.register(user);
            
            // 方法结束：返回注册结果
            log.info("用户注册成功，用户名: {}，用户ID: {}", registeredUser.getUsername(), registeredUser.getId());
            return Resp.ok(registeredUser);
            
        } catch (Exception e) {
            // 异常处理：记录错误并返回错误信息
            log.error("用户注册失败，用户名: {}，错误: {}", user.getUsername(), e.getMessage(), e);
            return Resp.info(500, "注册失败: " + e.getMessage());
        }
    }

    /**
     * 根据主键查询用户信息
     * 
     * 功能说明：
     * 1. 接收用户ID作为路径参数
     * 2. 从数据库查询用户信息
     * 3. 返回用户信息（不包含密码等敏感信息）
     * 
     * 业务流程：
     * - 开始：接收HTTP GET请求，路径中包含用户ID
     * - 参数提取：从路径变量中提取用户ID
     * - 数据查询：调用服务层查询用户信息
     * - 安全处理：过滤密码等敏感信息
     * - 响应构建：返回用户信息
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 userService.getById(id) 根据ID查询用户
     *   该方法会执行：SELECT * FROM user WHERE id = ?
     * 
     * 数据库调用：
     * - 查询用户：SELECT * FROM user WHERE id = ?
     *   参数：id（用户主键ID）
     *   返回值：User实体对象或null（如果用户不存在）
     * 
     * @param id 用户主键ID，从URL路径中获取
     * @return Resp 响应对象，包含用户信息
     *         - 成功时：code=0, data=User对象（不包含密码）
     *         - 用户不存在时：code=404, message="用户不存在"
     *         - 查询失败时：code=500, message="查询用户失败: 错误描述"
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable String id) {
        // 方法开始：准备查询用户信息
        log.info("开始查询用户信息，用户ID: {}", id);
        
        try {
            // 调用服务层根据ID查询用户
            // 数据库调用：SELECT * FROM user WHERE id = ?
            User user = userService.getById(id);
            
            if (user != null) {
                // 用户存在，返回用户信息（密码已自动过滤）
                log.info("查询用户信息成功，用户ID: {}，用户名: {}", id, user.getUsername());
                return Resp.ok(user);
            } else {
                // 用户不存在
                log.info("用户不存在，用户ID: {}", id);
                return Resp.info(404, "用户不存在");
            }
            
        } catch (Exception e) {
            // 异常处理：记录错误并返回错误信息
            log.error("查询用户信息失败，用户ID: {}，错误: {}", id, e.getMessage(), e);
            return Resp.info(500, "查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户名模糊查询用户
     * 
     * 功能说明：
     * 1. 接收用户名作为查询条件
     * 2. 执行模糊查询，查找包含指定用户名的所有用户
     * 3. 返回匹配的用户列表
     * 
     * 业务流程：
     * - 开始：接收HTTP POST请求，请求体中包含用户名
     * - 参数提取：从请求体中提取用户名
     * - 参数校验：验证用户名不为空
     * - 模糊查询：调用服务层执行模糊查询
     * - 响应构建：返回用户列表
     * - 结束：完成查询流程
     * 
     * 调用服务：
     * - 调用 userService.findByUsernameLike(username) 模糊查询用户
     *   该方法会执行：SELECT * FROM user WHERE username LIKE ?
     *   使用LIKE模糊匹配，查询包含指定用户名的所有用户
     * 
     * 数据库调用：
     * - 模糊查询用户：SELECT * FROM user WHERE username LIKE ?
     *   参数：username（用户名，包含通配符%）
     *   返回值：List<User>用户列表（可能为空列表）
     * 
     * @param params 请求参数，包含：
     *              - username: 用户名（必填，支持模糊查询）
     * @return Resp 响应对象，包含查询结果
     *         - 成功时：code=0, data=List<User>用户列表
     *         - 参数错误时：code=400, message="用户名不能为空"
     *         - 查询失败时：code=500, message="查询用户失败: 错误描述"
     */
    @PostMapping("findByUsername")
    public Resp findByUsername(@RequestBody Map<String, String> params) {
        // 方法开始：准备模糊查询用户
        log.info("开始模糊查询用户，参数: {}", params);
        
        try {
            // 从请求体中提取用户名
            String username = params.get("username");
            
            // 参数校验：验证用户名不为空
            if (username == null || username.isEmpty()) {
                log.warn("模糊查询用户失败，用户名为空");
                return Resp.info(400, "用户名不能为空");
            }
            
            // 调用服务层模糊查询用户
            // 数据库调用：SELECT * FROM user WHERE username LIKE ?
            // 用户名会自动添加通配符%，实现模糊匹配
            List<User> users = userService.findByUsernameLike(username);
            
            // 方法结束：返回查询结果
            log.info("模糊查询用户成功，用户名: {}，匹配数量: {}", username, users.size());
            return Resp.ok(users);
            
        } catch (Exception e) {
            // 异常处理：记录错误并返回错误信息
            log.error("模糊查询用户失败，参数: {}，错误: {}", params, e.getMessage(), e);
            return Resp.info(500, "查询用户失败: " + e.getMessage());
        }
    }
}
