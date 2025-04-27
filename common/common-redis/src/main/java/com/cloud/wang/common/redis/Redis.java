package com.cloud.wang.common.redis;

import lombok.Data;

/**
 * Redis 实体类
 *
 * @author wang
 * @since  2022-05-03
 */
@Data
public class Redis {

    /**
     * 地址
     */
    private String address;

    /**
     * 密码
     */
    private String password;

    public Redis(String address) {
        this.address = address;
    }

    public Redis(String address, String password) {
        this.address = address;
        this.password = password;
    }
}