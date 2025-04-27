package com.cloud.wang.common.log.entity;

import lombok.Data;

import java.io.Serializable;

/**
 *  日志实体
 *
 * @author 王子洋
 * @date 2020-08-12
 * */
@Data
public class LogEntity implements Serializable {

    //操作人ID
    private Long userId;

    //操作人IP
    private String ip;

    //操作描述
    private String desc;

    //请求路径
    private String url;

    //请求类型
    private String method;

    //请求参数
    private Object requestParam;

    //响应参数
    private Object responseParam;
}
