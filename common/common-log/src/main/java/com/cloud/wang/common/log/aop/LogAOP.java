package com.cloud.wang.common.log.aop;

import com.cloud.wang.common.core.wrapper.RequestWrapper;
import com.cloud.wang.common.log.annotation.Log;
import com.cloud.wang.common.log.entity.LogEntity;
import com.cloud.wang.common.rsa.utils.AES;
import com.cloud.wang.common.utils.servlet.ServletUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 *  日志模块AOP，异步将请求日志记录到数据库
 *
 * @author wang
 * @date 2022-05-19
 * */
@Order(value = 1)
@Aspect
@Component
public class LogAOP {

    @Pointcut("@annotation(com.cloud.wang.common.log.annotation.Log)")
    public void point () {}

    /**
     *  环绕增强，记录日志
     * */
    @Around("point() && @annotation(log)")
    public Object around (ProceedingJoinPoint joinPoint , Log log) throws Throwable {
        //返回的结果
        Object returnValue = null;
        //日志实体
        LogEntity logEntity = new LogEntity();
        //从当前上下文提取request
        HttpServletRequest request = ServletUtil.getRequest();
        //请求体中的json参数
        String body = "";

        //把可能出错的逻辑，放到try-catch
        try {
            //用户id
            logEntity.setUserId((long)111);
            //获取用户IP
            logEntity.setIp("127.0.0.1");
            //获取操作描述
            logEntity.setDesc(log.desc());
            //请求路径
            logEntity.setUrl(request.getRequestURL().toString());
            //请求类型
            String method = request.getMethod();
            logEntity.setMethod(method);
            //GET请求从路径中获取参数
            if (method.equals("GET")) {
                String param = request.getQueryString();
                logEntity.setRequestParam(param);
            }else {
                //POST请求从请求体拿参数
                body = new RequestWrapper(request).getBodyString();
                logEntity.setRequestParam(body);
            }
        }catch (Exception e) {

        }finally {
            //不管怎样，保证业务必须执行
            returnValue = joinPoint.proceed();
        }

        logEntity.setResponseParam(returnValue);
        //TODO 日志异步入库
        System.out.println("response：" + logEntity.toString());

        return returnValue;
    }

}
