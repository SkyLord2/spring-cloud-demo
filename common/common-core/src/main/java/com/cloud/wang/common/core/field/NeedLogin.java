package com.cloud.wang.common.core.field;

import java.lang.annotation.*;

/**
 *  标明哪些字段登录才能看
 *
 * @author wang
 * @since 2022-08-02
 * */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedLogin {

}
