package com.grape.grape.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.core.paginate.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/3/27  14:45
 * @Version 1.1
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageInfo {
    @JsonProperty("current")
    private long page;
    private long size;
    private long total;

    public PageInfo pageInfoByIPage(Page<?> iPage){
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(iPage.getPageNumber());
        pageInfo.setSize(iPage.getPageSize());
        pageInfo.setTotal(iPage.getTotalRow());
        return pageInfo;
    }
}
