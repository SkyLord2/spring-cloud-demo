package com.cloud.wang.gateway.biz.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 请求头处理(排序置后执行)
 * 将链路后的其他服务需要的请求头都放置此处处理
 *
 * @author wang
 * @since 2022/05/19 14:44
 */
@Slf4j
@Component
public class HeadGlobalFiter extends AbsBaseGlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //转发
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest finalRequest = request;
        request = exchange.getRequest().mutate().headers(httpHeaders -> {
            //提取token
            List<String> tokens = finalRequest.getHeaders().get("token");
            //把token放到后续链路的请求头中，方便后续服务使用
            if (!CollectionUtils.isEmpty(tokens)) {
                String token = tokens.get(0);
                httpHeaders.add("token", token);
            }
            
        }).build();
        ServerWebExchange build = exchange.mutate().request(request).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
