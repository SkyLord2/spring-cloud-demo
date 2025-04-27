package com.cloud.wang.common.core.r;

import com.cloud.wang.common.core.code.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *  公共返回类
 *
 * @author wang
 * @since 2022-05-03
 * */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R {

    //状态码，业务上自己定义
    private Integer code;

    //消息
    private String message;

    //数据，可以是任何类型，支持上游自定义Map
    private Object data;

    //禁止new R()对象，只能通过此类提供的静态方法返回数据
    private R() {
    }

    public static R ok() {
        R r = new R();
        r.setCode(200);
        r.setMessage("success");
        return r;
    }

    /**
     *  成功，并设置返回的数据
     *
     * @param data 数据
     * */
    public static R ok(Object data) {
        R r = new R();
        r.setCode(200);
        r.setData(data);
        r.setMessage("success");

        return r;
    }

    /**
     *  失败，必须返回错误码和错误信息
     *
     * @param error 错误信息枚举
     * */
    public static R error(ErrorCode error) {
        R r = new R();
        r.setCode(error.getCode());
        r.setMessage(error.getMessage());
        return r;
    }

}
