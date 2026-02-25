package com.grape.grape.config.mybatis;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/9/5  14:39
 * @Version
 */
public class TableInfoHelper {

    private static final Log logger = LogFactory.getLog(TableInfoHelper.class);

    /**
     * 储存反射类表信息
     */
    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 储存表名对应的反射类表信息
     */
    private static final Map<String, TableInfo> TABLE_NAME_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认表主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * 获取实体映射表信息
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        if (clazz == null || clazz.isPrimitive() || SimpleTypeRegistry.isSimpleType(clazz) || clazz.isInterface()) {
            return null;
        }
        // https://github.com/baomidou/mybatis-plus/issues/299
        Class<?> targetClass = ClassUtils.getUserClass(clazz);
        TableInfo tableInfo = TABLE_INFO_CACHE.get(targetClass);
        if (null != tableInfo) {
            return tableInfo;
        }
        //尝试获取父类缓存
        Class<?> currentClass = clazz;
        while (null == tableInfo && Object.class != currentClass) {
            currentClass = currentClass.getSuperclass();
            tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        //把父类的移到子类中来
        if (tableInfo != null) {
            TABLE_INFO_CACHE.put(targetClass, tableInfo);
        }
        return tableInfo;
    }

    /**
     * <p>
     * 根据表名获取实体映射表信息
     * </p>
     *
     * @param tableName 表名
     * @return 数据库表反射信息
     */
    public static TableInfo getTableInfo(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return null;
        }
        return TABLE_NAME_INFO_CACHE.get(tableName);
    }

    /**
     * <p>
     * 获取所有实体映射表信息
     * </p>
     *
     * @return 数据库表反射信息集合
     */
    public static List<TableInfo> getTableInfos() {
        return Collections.unmodifiableList(new ArrayList<>(TABLE_INFO_CACHE.values()));
    }

    /**
     * 清空实体表映射缓存信息
     *
     * @param entityClass 实体 Class
     */
    public static void remove(Class<?> entityClass) {
        TABLE_INFO_CACHE.remove(entityClass);
    }

}
