package com.cloud.wang.common.rsa.annotation;

import java.lang.annotation.*;

/**
 *  加密注解
 *
 * @author wang
 * @date 2022-05-20
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {

}
