package com.grape.grape.config.mybatis;

import com.mybatisflex.annotation.UpdateListener;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.Date;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/9/5  15:07
 * @Version
 */


public class MyUpdateListener implements UpdateListener {

    protected MetaObjectHandler handler = new MetaObjectHandler();

    static void getUser(Object entity, MetaObjectHandler handler) {
//        LoginUserVo loginUser = DataContextSupport.getDataPermissions();
        handler.setFieldValByName("updatedAt", new Date().getTime(), SystemMetaObject.forObject(entity));
//        handler.setFieldValByName("updateBy",loginUser.getId(),SystemMetaObject.forObject(entity));
    }

    @Override
    public void onUpdate(Object entity) {
        getUser(entity, handler);
    }
}
