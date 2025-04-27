package com.cloud.wang.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  <p>
 *      项目启动时初始化Redis
 *  </p>
 *
 * @author wang
 * @since  2022-05-03
 * */
@Component
public class RedisInit implements ApplicationRunner {

    /**
     * redis 地址
     */
    @Value("${spring.redis.host}")
    private String host;

    /**
     * redis 端口
     */
    @Value("${spring.redis.port}")
    private Integer port;

    /**
     * redis 密码  如果有的话就写上，同时不要忘了在Nacos里进行配置
     */
    /*@Value("${spring.redis.password}")
    private String redisPassword;*/

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(redisUrl,Integer.valueOf(redisPort)));
        JedisUtil.init(nodes);*/

        JedisUtil.init(host , port);
        System.out.println("初始化Redis完成****************");
    }
}