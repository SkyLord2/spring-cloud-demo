package com.cloud.wang.common.core.code;

/**
 *  公共错误码，各业务服务器的错误码可以实现此类，去
 *  定义自己的业务code码，如GatewayErrorCode、OrderErrorCode（按微服务的拆分划分）
 *
 * @author wang
 * @since 2022-05-03
 * */
public interface ErrorCode {

    /**
     * 获取状态码
     */
    Integer getCode();

    /**
     * 获取错误信息
     */
    String getMessage();

}
