package com.cloud.wang.common.core.handler;

import com.cloud.wang.common.core.code.BaseErrorCode;
import com.cloud.wang.common.core.r.R;
import org.apache.http.NoHttpResponseException;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.net.ConnectException;

/**
 * 全局异常拦截器
 *
 * <p>
 *     如果各个业务服务 需要自定义的异常处理，可以在各自的biz中声明（不能声明在api中，因为可能
 *     其他biz引用了这个api，但是其他的biz并不需要这个自定义的异常处理），继承此类，实现定制化的异常处理
 * </p>
 *
 * @author wang
 * @since 2022-05-03
 */
@RestControllerAdvice
public class AbsGloablExceptionHandler
{
    private static final Logger logger = LoggerFactory.getLogger(AbsGloablExceptionHandler.class);

    //处理所有未知异常
    @ExceptionHandler(Exception.class)
    public R Exception(HttpServletRequest request, Exception e)
    {
        logger.error("url = {}, errorMsg = {}", request.getRequestURL(), e);
        return R.error(BaseErrorCode.SERVICE_ERROR);
    }

    // 响应超时
    @ExceptionHandler(NoHttpResponseException.class)
    public R NoHttpResponseException(HttpServletRequest request, Exception e)
    {
        logger.error("url = {}, errorMsg = {}", request.getRequestURL(), e);
        return R.error(BaseErrorCode.REQUEST_TIME_OUT);
    }

    // 连接超时
    @ExceptionHandler(ConnectException.class)
    public R ConnectException(HttpServletRequest request, Exception e)
    {
        logger.error("url = {}, errorMsg = {}", request.getRequestURL(), e);
        return R.error(BaseErrorCode.CONNECT_TIME_OUT);
    }

    /**
     * MyBatisSystemException 查询SQL异常
     */
    @ExceptionHandler(MyBatisSystemException.class)
    public R MyBatisSystemException(HttpServletRequest request, Exception e)
    {
        logger.error("url = {}, errorMsg = {}", request.getRequestURL(), e.getMessage());
        return R.error(BaseErrorCode.SQL_ERROR);
    }

}
