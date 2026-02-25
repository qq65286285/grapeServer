package com.grape.grape.config.jwttools;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/5/8  9:58
 * @Version
 */
public class JwtToken  implements AuthenticationToken {
    private static final long serialVersionUID = 1L;

    private String token;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserKey() {
        return userKey;
    }

    private String userId;
    private String username;
    private String userKey;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public JwtToken(String token) {
        this.token = token;
    }

    public JwtToken(String userId,String username,String userKey){
        this.userId = userId;
        this.username = username;
        this.userKey = userKey;
    }
    @Override
    public Object getPrincipal() {
        return this;
    }

    @Override
    public Object getCredentials() {
        return this;
    }
}
