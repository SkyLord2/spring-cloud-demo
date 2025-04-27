package com.cloud.wang.gateway.biz.auth;

import com.cloud.wang.common.core.r.R;
import com.cloud.wang.common.redis.JedisUtil;
import com.cloud.wang.gateway.api.code.GatewayErrorCode;
import com.cloud.wang.gateway.api.constant.BaseConstant;
import com.cloud.wang.gateway.api.utils.MonoUtil;
import com.cloud.wang.gateway.biz.filter.AbsBaseGlobalFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * token校验过滤器
 *
 * @author wang
 * @since 2022/05/18
 */
@Slf4j
@Component
public class AuthGlobalFilter extends AbsBaseGlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 白名单校验
        RequestPath path = request.getPath();
        String value = path.pathWithinApplication().value();
        // 组装真实请求路径
        Route route = this.getRoute(exchange);
        value = "/" + route.getUri().getHost() + value;
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (int i = 0; i < BaseConstant.WHITE_URL_LIST.size(); i++) {
            if (antPathMatcher.match(BaseConstant.WHITE_URL_LIST.get(i), value)) {
                return MonoUtil.success(exchange,chain);
            }
        }

        //提取token
        List<String> authorization = request.getHeaders().get(BaseConstant.HEAD_TOKEN_NAME);
        if (CollectionUtils.isEmpty(authorization)) {
            return MonoUtil.error(R.error(GatewayErrorCode.NO_TOKEN),response);
        }
        //判断token是否在请求头中存在
        String token = authorization.get(0);
        if (StringUtils.isBlank(token)) {
            return MonoUtil.error(R.error(GatewayErrorCode.NO_TOKEN),response);
        }

        //判断token是否在redis存在
        if (JedisUtil.getStringValue(token) == null) {
            return MonoUtil.error(R.error(GatewayErrorCode.ERROR_TOKEN),response);
        }

        // token续期
        JedisUtil.expire(token , BaseConstant.TOKEN_EXPIRE);
        ServerWebExchange build = exchange.mutate().request(request).build();
        return chain.filter(build);
    }


    /**
     *  第3顺位执行，再进行白名单和token校验
     * */
    @Override
    public int getOrder() {
        return 2;
    }

}
