package com.cloud.wang.gateway.api.constant;

import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.List;

public class BaseConstant {

    //前端请求头中的token名
    public static final String HEAD_TOKEN_NAME = "token";

    //幂等性token在redis中的前缀
    public static final String RK_IDEMPOTENCE = "idempotence:";

    //幂等性时效
    public static final Integer IDEMPOTENCE_TIME = 5;

    //token过期时间
    public static final Integer TOKEN_EXPIRE = 60 * 60 * 24 * 7;

    public static final List<String> WHITE_URL_LIST = new ArrayList<String>(){{
        //规则：/{服务名}/{API路径}
        add("/student-service/demo/list");
        add("/student-service/demo/post");
    }};
}
