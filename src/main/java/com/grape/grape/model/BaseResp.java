package com.grape.grape.model;

import com.grape.grape.model.dict.ResultEnumI18n;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.ThreadContext;


/**
 * @Author:Gin.44.Candy
 * @Date: 2023/3/27  14:17
 * @Version 1.1
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResp {
    private int code;
    private String message;
    private String requestId = ThreadContext.get("trace_log_id");

    public static BaseResp ok() {
        return info(ResultEnumI18n.SUCCESS.getCode(), ResultEnumI18n.SUCCESS.getMessage());
    }
    public static BaseResp info(Integer code, String message) {
        return new BaseResp(code, message,ThreadContext.get("trace_log_id"));
    }



    public static BaseResp error() {
        return info(ResultEnumI18n.SYSTEM_ERROR.getCode(), ResultEnumI18n.SYSTEM_ERROR.getMessage());
    }

    public BaseResp(ResultEnumI18n resultEnum){
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    public static BaseResp Nomalconstructor(Boolean flag){
        if (flag){
            return BaseResp.ok();
        }else{
            return BaseResp.error();
        }
    }


}
