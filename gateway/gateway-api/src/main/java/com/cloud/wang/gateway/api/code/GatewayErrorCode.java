package com.cloud.wang.gateway.api.code;

import com.cloud.wang.common.core.code.ErrorCode;

/**
 *  网关的错误码，从10000开始
 *
 * @author wang
 * @since 2022-05-03
 * */
public enum GatewayErrorCode implements ErrorCode {

    RE_SUBMIT(10000 , "重复提交"),
    NO_TOKEN(10001 , "缺少token"),
    ERROR_TOKEN(10002 , "token错误"),
    ;

    private Integer code;
    private String message;

    GatewayErrorCode(Integer code , String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     */
    @Override
    public Integer getCode() {
        return this.code;
    }

    /**
     * 获取错误信息
     */
    @Override
    public String getMessage() {
        return this.message;
    }
}
