package com.grape.grape.config.mybatis;

import com.mybatisflex.core.table.TableInfo;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/9/5  14:36
 * @Version
 */
public class MetaObjectHandler {

    /**
     * 通用填充
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     */
    public MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        if (Objects.nonNull(fieldVal) && metaObject.hasSetter(fieldName)) {
            metaObject.setValue(fieldName, fieldVal);
        }
        return this;
    }

    /**
     * get value from java bean by propertyName
     *
     * @param fieldName  java bean property name
     * @param metaObject parameter wrapper
     * @return 字段值
     */
    public Object getFieldValByName(String fieldName, MetaObject metaObject) {
        return metaObject.hasGetter(fieldName) ? metaObject.getValue(fieldName) : null;
    }

    /**
     * find the tableInfo cache by metaObject </p>
     * 获取 TableInfo 缓存
     *
     * @param metaObject meta object parameter
     * @return TableInfo
     * @since 3.3.0
     */
    public TableInfo findTableInfo(MetaObject metaObject) {
        return TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
    }






    /**
     * 填充策略,默认有值不覆盖,如果提供的值为null也不填充
     *
     * @param metaObject metaObject meta object parameter
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value of Supplier
     * @return this
     * @since 3.3.0
     */
    public MetaObjectHandler fillStrategy(MetaObject metaObject, String fieldName, Object fieldVal) {
        if (getFieldValByName(fieldName, metaObject) == null) {
            setFieldValByName(fieldName, fieldVal, metaObject);
        }
        return this;
    }

    /**
     * 严格模式填充策略,默认有值不覆盖,如果提供的值为null也不填充
     *
     * @param metaObject metaObject meta object parameter
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value of Supplier
     * @return this
     * @since 3.3.0
     */
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        if (metaObject.getValue(fieldName) == null) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        return this;
    }
}
