package com.grape.grape.service;

import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/9/6  11:42
 * @Version
 */
public interface MyBaseService<T> extends IService<T> {
    /**
     * <p>根据数据主键查询一条数据。
     * 带关联关系
     * @param id 数据主键
     * @return 查询结果数据
     */
    default T getOneWithRelationById(Serializable id) {
        return getMapper().selectOneWithRelationsById(id);
    }

    /**
     * 根据 Map 构建的条件来查询 1 条数据。
     *
     * @param whereConditions 条件
     * @return 实体类数据
     */
    default T selectOneWithRelationsByMap(Map<String, Object> whereConditions) {
        return getMapper().selectOneWithRelationsByMap(whereConditions);
    }

    /**
     * 根据查询条件查询 1 条数据。
     *
     * @param whereConditions 条件
     * @return 实体类数据
     */
    default T selectOneWithRelationsByCondition(QueryCondition whereConditions) {
        return getMapper().selectOneWithRelationsByCondition(whereConditions);
    }


    /**
     * 根据查询条件来查询 1 条数据。
     *
     * @param queryWrapper 条件
     * @return 实体类数据
     */
    default T selectOneWithRelationsByQuery(QueryWrapper queryWrapper) {
        return getMapper().selectOneWithRelationsByQuery(queryWrapper);
    }

    /**
     * 根据主表主键来查询 1 条数据。
     *
     * @param id     表主键
     * @param asType 接收数据类型
     * @return 实体类数据
     */
    default <R> R selectOneWithRelationsByIdAs(Serializable id, Class<R> asType) {
            return getMapper().selectOneWithRelationsByIdAs(id,asType);
    }



    /**
     * 根据查询条件来查询 1 条数据。
     *
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 实体类数据
     */
    default <R> R selectOneWithRelationsByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        return getMapper().selectOneWithRelationsByQueryAs(queryWrapper,asType);
    }

    /**
     * 查询实体类及其 Relation 注解字段。
     *
     * @param queryWrapper 条件
     */
    default List<T> selectListWithRelationsByQuery(QueryWrapper queryWrapper) {
        return  getMapper().selectListWithRelationsByQuery(queryWrapper);
    }

    /**
     * 查询实体类及其 Relation 注解字段。
     *
     * @param queryWrapper 条件
     * @param asType       要求返回的数据类型
     * @return 数据列表
     */
    default <R> List<R> selectListWithRelationsByQueryAs(QueryWrapper queryWrapper, Class<R> asType) {
        return getMapper().selectListWithRelationsByQueryAs(queryWrapper,asType);
    }

    /**
     * 查询实体类及其 Relation 注解字段。
     *
     * @param queryWrapper 条件
     * @param asType       返回的类型
     * @param consumers    字段查询
     * @return 数据列表
     */
    default <R> List<R> selectListWithRelationsByQueryAs(QueryWrapper queryWrapper, Class<R> asType, Consumer<FieldQueryBuilder<R>>... consumers) {
        return getMapper().selectListWithRelationsByQueryAs(queryWrapper,asType,consumers);
    }


    /**
     * 查询全部数据，及其 Relation 字段内容。
     *
     * @return 数据列表
     */
    default List<T> selectAllWithRelations() {
        return getMapper().selectAllWithRelations();
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, QueryWrapper queryWrapper) {
        return getMapper().paginateWithRelations(pageNumber,pageSize, queryWrapper);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber      当前页码
     * @param pageSize        每页的数据量
     * @param whereConditions 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, QueryCondition whereConditions) {
        return getMapper().paginateWithRelations(pageNumber,pageSize, new QueryWrapper().where(whereConditions));
    }


    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, Number totalRow, QueryWrapper queryWrapper) {
        return  getMapper().paginateWithRelations(pageNumber,pageSize,totalRow, queryWrapper);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber      当前页码
     * @param pageSize        每页的数据量
     * @param totalRow        数据总量
     * @param whereConditions 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Number pageNumber, Number pageSize, Number totalRow, QueryCondition whereConditions) {
        return getMapper().paginateWithRelations(pageNumber,pageSize,totalRow, new QueryWrapper().where(whereConditions));
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Page<T> page, QueryWrapper queryWrapper) {
        return getMapper().paginateWithRelations(page, queryWrapper);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param consumers    字段查询
     * @return 分页数据
     */
    default Page<T> paginateWithRelations(Page<T> page, QueryWrapper queryWrapper, Consumer<FieldQueryBuilder<T>>... consumers) {
        return getMapper().paginateWithRelations(page, queryWrapper,  consumers);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Number pageNumber, Number pageSize, QueryWrapper queryWrapper, Class<R> asType) {
        return getMapper().paginateWithRelationsAs(pageNumber, pageSize, queryWrapper, asType);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param pageNumber   当前页码
     * @param pageSize     每页的数据量
     * @param totalRow     数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Number pageNumber, Number pageSize, Number totalRow, QueryWrapper queryWrapper, Class<R> asType) {
        return getMapper().paginateWithRelationsAs(pageNumber,pageSize,totalRow,queryWrapper,asType);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Page<R> page, QueryWrapper queryWrapper, Class<R> asType) {
        return getMapper().paginateWithRelationsAs(page,queryWrapper,asType);
    }

    /**
     * 分页查询，及其 Relation 字段内容。
     *
     * @param page         包含了页码、每页的数据量，可能包含数据总量
     * @param queryWrapper 条件
     * @param asType       接收数据类型
     * @param consumers    字段查询
     * @return 分页数据
     */
    default <R> Page<R> paginateWithRelationsAs(Page<R> page, QueryWrapper queryWrapper, Class<R> asType, Consumer<FieldQueryBuilder<R>>... consumers) {
        return getMapper().paginateWithRelationsAs(page,queryWrapper,asType,consumers);
    }


}
