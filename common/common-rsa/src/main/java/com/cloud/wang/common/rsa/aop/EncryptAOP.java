package com.cloud.wang.common.rsa.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.wang.common.core.r.R;
import com.cloud.wang.common.core.wrapper.RequestWrapper;
import com.cloud.wang.common.rsa.annotation.Encrypt;
import com.cloud.wang.common.rsa.utils.AES;
import com.cloud.wang.common.utils.servlet.ServletUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *  传输加密模块AOP，对接口的出参进行加密
 *  注意顺序不能乱，此AOP必须第一个执行，因为
 *  最先执行的最后结束，这样才能在各个AOP都执行
 *  完毕之后完成最后的加密
 *
 * @author wang
 * @date 2022-05-19
 * */
@Order(value = 0)
@Aspect
@Component
public class EncryptAOP {

    @Pointcut("@annotation(com.cloud.wang.common.rsa.annotation.Encrypt)")
    public void point () {}

    /**
     *  环绕增强，加密出参
     * */
    @Around(value = "point() && @annotation(encrypt)")
    public Object around (ProceedingJoinPoint joinPoint , Encrypt encrypt) throws Throwable {
        //返回的结果
        Object returnValue = null;
        //从当前上下文提取request
        HttpServletRequest request = ServletUtil.getRequest();
        //请求体中的json参数
        String body = "";

        //把可能出错的逻辑，放到try-catch
        try {
            body = new RequestWrapper(request).getBodyString();
        }catch (Exception e) {

        }finally {
            //不管怎样，保证业务必须执行
            returnValue = joinPoint.proceed();
        }

        //把接口的出参，用前端此次传过来的AES密钥进行加密，然后返回
        JSONObject jsonObject = JSON.parseObject(body);
        String encryptData = AES.encryptToBase64(returnValue.toString() , jsonObject.getString("aesKey"));

        returnValue = R.ok(encryptData);
        return returnValue;
    }
}
