package com.grape.grape.model;

import cn.hutool.core.convert.Convert;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.mybatisflex.core.paginate.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:Gin.44.Candy
 * @Date: 2023/3/28  13:45
 * @Version 1.1
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResp extends BaseResp{
    private PageData data;

    public PageResp pageInfoOk(Page<?> iPage){
        PageResp pageResp = new PageResp(new PageData().pageInfoByIPage(iPage));
        pageResp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        pageResp.setCode(ResultEnumI18n.SUCCESS.getCode());
        return pageResp;
    }

    public PageResp pageList(int page,int size,long total,List<?> list){
        PageResp pageResp = new PageResp(new PageData().pageInfoByIPage(
                Convert.toLong(page),Convert.toLong(size),total,list));
        pageResp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        pageResp.setCode(ResultEnumI18n.SUCCESS.getCode());
        return pageResp;
    }

    public PageResp pageInfoOk(Page<?> iPage, List<?> list){
        PageResp pageResp = new PageResp(new PageData().pageInfoByIPage(iPage,list));
        pageResp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        pageResp.setCode(ResultEnumI18n.SUCCESS.getCode());
        return pageResp;
    }
    public PageResp pageInfoOk(long page,long size,long total,List<?> list){
        PageResp pageResp = new PageResp(new PageData().pageInfoByIPage(page,size,total,list));
        pageResp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        pageResp.setCode(ResultEnumI18n.SUCCESS.getCode());
        return pageResp;
    }

    public PageResp pageInfoOk(int page,int size,long total,List<?> list){
        PageResp pageResp = new PageResp(new PageData().pageInfoByIPage(Convert.toLong(page),Convert.toLong(size),total,list));
        pageResp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        pageResp.setCode(ResultEnumI18n.SUCCESS.getCode());
        return pageResp;
    }


    public PageResp error(String errMessage){
        PageResp pageResp = new PageResp();
        pageResp.setCode(ResultEnumI18n.BUSINESS_ERROR.getCode());
        pageResp.setMessage(errMessage);
        return pageResp;
    }
}
