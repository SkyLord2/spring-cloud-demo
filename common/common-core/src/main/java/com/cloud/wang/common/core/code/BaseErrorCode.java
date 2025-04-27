package com.cloud.wang.common.core.code;

/**
 *  基础的错误码
 *
 * @author wang
 * @since 2022-05-03
 * */
public enum BaseErrorCode implements ErrorCode{
    SERVICE_ERROR(500 , "服务器异常，请稍后再试"),
    REQUEST_TIME_OUT(504 , "请求超时"),
    CONNECT_TIME_OUT(505 , "连接超时"),
    SQL_ERROR(506 , "SQL查询异常"),
    ;

    private Integer code;
    private String message;

    BaseErrorCode(Integer code , String message) {
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
