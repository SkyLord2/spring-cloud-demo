package com.cloud.wang.gateway.biz.filter;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 通用过滤器
 * @author wang
 * @since 2022/05/18
 */
public abstract class AbsBaseGlobalFilter implements GlobalFilter,Ordered {

    protected Route route;

    // 真实url访问路径
    protected String url;

    /**
     * 获取route，route中包含真实路径
     *
     * @param exchange
     * @return org.springframework.cloud.gateway.route.Route
     */
    protected Route getRoute(ServerWebExchange exchange){
        init(exchange);
       return this.route;
    }

    /**
     * 获取真实url
     *
     * @return java.net.URI
     */
    protected String getUrl(ServerWebExchange exchange){
        init(exchange);
        return this.url;
    }

    /**
     * 获取host
     *
     * @return java.lang.String
     */
    protected String getHost(ServerWebExchange exchange){
        init(exchange);
        return this.route.getUri().getHost();
    }

    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     * @return 请求体
     */
    protected String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    /**
     * 提取ServerWebExchange 中的属性数据
     *
     * @param  key, cla
     * @return T
     */
    public static <T> String getToJsonObject(T obj ,String key ){

        if (obj == null || StringUtils.isBlank(key)) {
            return null;
        }
        JSONObject resultObj = JSONObject.parseObject(JSONObject.toJSONString(obj));
        String[] split = key.split("\\.");
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                // 最后一个
                return resultObj.getString(split[i]);
            }else{
                resultObj = resultObj.getJSONObject(split[i]);
            }
        }
        return null;
    }

    private void init(ServerWebExchange exchange){
        if (exchange == null && this.route == null && StringUtils.isBlank(this.url)) {
            throw new RuntimeException("exchange参数不可为空");
        }
        // 初始化route
        this.route = (Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        // 初始化url
        URI uri = exchange.getRequest().getURI();
        this.url = uri.toString().replaceAll(uri.getAuthority(),uri.getAuthority() + "/" +this.route.getUri().getHost() );
    }
}
