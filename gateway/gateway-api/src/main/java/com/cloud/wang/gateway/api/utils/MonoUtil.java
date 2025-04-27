package com.cloud.wang.gateway.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.cloud.wang.common.core.code.ErrorCode;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author wang
 * @since 2022/05/18
 */
public class MonoUtil {

    public static Mono<Void> error(Object error, ServerHttpResponse response){
        byte[] bits = JSONObject.toJSONString(error)
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    public static Mono<Void> success(ServerWebExchange exchange, GatewayFilterChain chain){
        return success(exchange,chain,()->{});
    }

    public static Mono<Void> success(ServerWebExchange exchange, GatewayFilterChain chain,Runnable runnable){
        return chain.filter(exchange).then(Mono.fromRunnable(runnable));
    }

}
