package com.grape.grape.model;

import com.mybatisflex.core.paginate.Page;
import lombok.Data;

import java.util.List;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/3/27  14:46
 * @Version 1.1
 */

@Data
public class PageData {
    private List<?> list;
    private PageInfo pageInfo;

    public PageData pageInfoByIPage(Page<?> iPage){
        PageData pageData = new PageData();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(iPage.getPageNumber());
        pageInfo.setSize(iPage.getPageSize());
        pageInfo.setTotal(iPage.getTotalRow());
        pageData.setPageInfo(pageInfo);
        pageData.setList(iPage.getRecords());
        return pageData;
    }

    public PageData pageInfoByIPage(Page<?> iPage,List<?> list){
        PageData pageData = new PageData();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(iPage.getPageNumber());
        pageInfo.setSize(iPage.getPageSize());
        pageInfo.setTotal(iPage.getTotalRow());
        pageData.setPageInfo(pageInfo);
        pageData.setList(list);
        return pageData;
    }

    public PageData pageInfoByIPage(long page, long size, long total, List<?> list){
        PageData pageData = new PageData();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        pageInfo.setTotal(total);
        pageData.setPageInfo(pageInfo);
        pageData.setList(list);
        return pageData;
    }

    }
