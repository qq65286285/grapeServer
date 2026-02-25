package com.grape.grape.model;

import com.grape.grape.model.dict.ResultEnumI18n;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.logging.log4j.ThreadContext;


/**
 * @Author:Gin.44.Candy
 * @Date: 2023/3/27  14:02
 * @Version 1.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Resp extends BaseResp{
    private Object data;

    private Resp(int code, String message) {
        super(code, message, ThreadContext.get("trace_log_id"));
    }

    public static Resp info(Integer code, String message) {
        return new Resp(code,message);
    }
    public static Resp info(ResultEnumI18n resultEnumI18n) {
        return new Resp(resultEnumI18n.getCode(),resultEnumI18n.getMessage());
    }


    public static Resp ok(Object data) {
        Resp resp = new Resp();
        resp.setData(data);
        resp.setCode(ResultEnumI18n.SUCCESS.getCode());
        resp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        return resp ;
    }

    public static Resp ok(int code ,Object data) {
        Resp resp = new Resp();
        resp.setData(data);
        resp.setCode(code);
        resp.setMessage(ResultEnumI18n.SUCCESS.getMessage());
        return resp ;
    }

    public static Resp error() {
        return info(ResultEnumI18n.SYSTEM_ERROR.getCode(), ResultEnumI18n.SYSTEM_ERROR.getMessage());
    }


}

