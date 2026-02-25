package com.grape.grape.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.User;
import com.grape.grape.mapper.UserMapper;
import com.grape.grape.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 核心用户信息表，存储系统用户基础数据 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        // 校验用户名、手机号、邮箱是否已存在
        if (getByUserName(user.getUsername())  != null) {
            throw new RuntimeException("用户名已存在");
        }
//        if (StrUtil.isBlank(user.getMobile())  && getByMobile(user.getMobile())  != null) {
//            throw new RuntimeException("手机号已存在");
//        }
//        if (StrUtil.isBlank(user.getEmail())  && getByEmail(user.getEmail())  != null) {
//            throw new RuntimeException("邮箱已存在");
//        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置ID和创建时间
        user.setId(UUID.randomUUID().toString().replaceAll("-",""));
        user.setCreateTime(new Date().getTime());

        // 插入用户
        save(user);

        return user;    }

    @Override
    public User findByUsername(String username) {
        return getByUserName(username);
    }

    @Override
    public List<User> findByUsernameLike(String username) {
        QueryWrapper qw = new QueryWrapper();
        qw.like(User::getUsername, username);
        return list(qw);
    }

    private User getByMobile(String mobile) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq(User::getMobile, mobile);
        qw.limit(1);
        return getOne(qw);
    }

    private User getByEmail(String email) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq(User::getEmail, email);
        qw.limit(1);
        return getOne(qw);
    }

    private User getByUserName(String userName) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq(User::getUsername, userName);
        qw.limit(1);
        return getOne(qw);
    }

}
