package com.cloud.wang.gateway.api.constant;

import lombok.Getter;

/**
 *  网关-权限相关的常量、枚举
 *
 * @author wang
 * @since 2022-05-03
 * */
public class AuthConstant {

    //常量直接以public形式定义
    public static final String TOKEN_PREFIX = "user-";

    /**
     *  用户角色的枚举
     * */
    @Getter
    public enum UserRole {
        ADMIN(1 , "超级管理员"),
        EMPLOYEE(2 , "普通员工"),
        ;

        private Integer value;
        private String desc;
        UserRole(Integer value , String desc) {
            this.value = value;
            this.desc = desc;
        }
    }

    //如果还需要其他的枚举，在下面接着定义。保证同一个模块的各种常量、枚举声明在一起，维护方便。
}
