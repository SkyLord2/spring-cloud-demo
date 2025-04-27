package com.cloud.wang.common.log.annotation;

import java.lang.annotation.*;

/**
 *  日志注解
 *
 * @author wang
 * @date 2022-05-19
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     *  操作描述
     * */
    String desc () default "未知";
}
