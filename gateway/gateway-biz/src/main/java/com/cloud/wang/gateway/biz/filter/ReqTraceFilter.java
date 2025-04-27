package com.cloud.wang.gateway.biz.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author wang
 * @date 2022/05/19 11:02
 */

@Component
@Slf4j
public class ReqTraceFilter implements GlobalFilter, GatewayFilter, Ordered {

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final String GATEWAY_ROUTE_BEAN = "org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request= exchange.getRequest();

        String method = request.getMethodValue();

        if ("POST".equals(method) || "PUT".equals(method)) {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String bodyString = new String(bytes, StandardCharsets.UTF_8);
                        //TODO 得到Post请求的请求参数后，做你想做的事
                        //log.info("bodyString====={}",bodyString);

                        exchange.getAttributes().put("POST_BODY",bodyString);
                        DataBufferUtils.release(dataBuffer);

                        Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                            DataBuffer buffer = exchange.getResponse().bufferFactory()
                                    .wrap(bytes);
                            return Mono.just(buffer);
                        });
                        //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
                        //封装request，传给下一级
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        } else {
            return chain.filter(exchange);
        }
    }

    /**
     *  第1顺位执行，先传递请求体
     * */
    @Override
    public int getOrder() {
        return 0;
    }
}
