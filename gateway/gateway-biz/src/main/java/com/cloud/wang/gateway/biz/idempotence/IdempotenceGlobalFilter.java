package com.cloud.wang.gateway.biz.idempotence;

import com.cloud.wang.common.core.code.ErrorCode;
import com.cloud.wang.common.core.r.R;
import com.cloud.wang.common.redis.JedisUtil;
import com.cloud.wang.gateway.api.code.GatewayErrorCode;
import com.cloud.wang.gateway.api.constant.BaseConstant;
import com.cloud.wang.gateway.api.utils.MonoUtil;
import com.cloud.wang.gateway.biz.filter.AbsBaseGlobalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 幂等性校验过滤器
 *
 * @author wang
 * @since 2022/05/18
 */
@Slf4j
@Component
public class IdempotenceGlobalFilter extends AbsBaseGlobalFilter {

    /**
     *  幂等性校验
     * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 只对post请求做幂等性
        if (HttpMethod.POST.matches(request.getMethod().name())) {
            HttpHeaders headers = request.getHeaders();
            List<String> tokenList = headers.get(BaseConstant.HEAD_TOKEN_NAME);
            // 需要传token
            if (!CollectionUtils.isEmpty(tokenList)) {
                //从请求里获取Post请求体
                String bodyStr = resolveBodyFromRequest(request);
                // 参数+token 存在于redis中
                String token = tokenList.get(0);
                String redisValue = JedisUtil.getStringValue(BaseConstant.RK_IDEMPOTENCE + token);
                if (!StringUtils.isEmpty(bodyStr)&&bodyStr.equals(redisValue)) {
                    return MonoUtil.error(R.error(GatewayErrorCode.RE_SUBMIT) , response);
                }
                if(bodyStr!=null)
                JedisUtil.setStringValue(BaseConstant.RK_IDEMPOTENCE + token, bodyStr, BaseConstant.IDEMPOTENCE_TIME);
            }
        }
        return MonoUtil.success(exchange,chain);
    }

    /**
     *  第2顺位执行，再判断幂等性
     * */
    @Override
    public int getOrder() {
        return 1;
    }

}
