package com.grape.grape.config.mybatis;

import com.mybatisflex.annotation.InsertListener;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.Date;
import java.util.UUID;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/9/5  15:07
 * @Version
 */


public class MyInsertListener implements InsertListener {

    protected MetaObjectHandler handler = new MetaObjectHandler();

    @Override
    public void onInsert(Object entity) {
        getUser(entity, handler);
    }

    static void getUser(Object entity, MetaObjectHandler handler) {
//        LoginUserVo loginUser = DataContextSupport.getDataPermissions();
        handler.setFieldValByName("createdAt", new Date().getTime(), SystemMetaObject.forObject(entity));
//        handler.setFieldValByName("createBy",loginUser.getId(),SystemMetaObject.forObject(entity));
//        handler.setFieldValByName("createByDept",loginUser.getOrgId(),SystemMetaObject.forObject(entity));
        handler.setFieldValByName("updatedAt", new Date().getTime(), SystemMetaObject.forObject(entity));
//        handler.setFieldValByName("updateBy",loginUser.getId(),SystemMetaObject.forObject(entity));
////        handler.setFieldValByName("id", UUID.randomUUID().toString(), SystemMetaObject.forObject(entity));
//        handler.setFieldValByName("version", 1L, SystemMetaObject.forObject(entity));
    }
}
