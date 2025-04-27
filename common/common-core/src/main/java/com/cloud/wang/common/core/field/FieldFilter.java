package com.cloud.wang.common.core.field;

import java.lang.annotation.*;

/**
 *  标明API是否需要对字段进行登录过滤
 *
 * @author wang
 * @since 2022-08-02
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldFilter {

}
