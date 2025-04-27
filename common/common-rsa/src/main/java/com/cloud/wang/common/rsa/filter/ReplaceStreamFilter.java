package com.cloud.wang.common.rsa.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cloud.wang.common.core.constant.FeignConstant;
import com.cloud.wang.common.core.wrapper.RequestWrapper;
import com.cloud.wang.common.rsa.utils.AES;
import com.cloud.wang.common.rsa.utils.RSAUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
 
/**
 *  替换请求体的过滤逻辑
 *
 * @author wang
 * @since 2022-05-20
 **/
@Slf4j
public class ReplaceStreamFilter implements Filter {

    @Value("${rsa.privateKey}")
    private String privateKey;
 
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //转换自己的wrapper，实现多次读写
        RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);

        //GET请求不加密
        if (requestWrapper.getMethod().equals("GET")) {
            chain.doFilter(requestWrapper, response);
            return;
        }

        //Feign调用，内部请求，按照不加密的逻辑放行
        //前端 --> A  -->  B  -->  C
        if (requestWrapper.getHeader(FeignConstant.SOURCE_KEY).equals(FeignConstant.SOURCE_VALUE)) {
            chain.doFilter(requestWrapper, response);
            return;
        }

        //读出json请求体
        StringBuffer buffer = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        reader = requestWrapper.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        //解密
        JSONObject jsonObject = JSON.parseObject(buffer.toString());
        //AES加密得到的密文
        String content = jsonObject.getString("content");
        //加密之后的AES的key
        String aesKey = jsonObject.getString("aesKey");
        //先解密出aesKey
        aesKey = new String(RSAUtil.decryptByPrivateKey(RSAUtil.toBytes(aesKey) , privateKey , 256) , "UTF-8");
        //再用AES的key解密数据
        String data = AES.decryptFromBase64(content , aesKey);

        //把解密之后的aesKey一并交给下游，方便在AOP对出参加密
        jsonObject = JSON.parseObject(data);
        jsonObject.put("aesKey" , aesKey);

        //重置json请求体，保证下游业务无感知获取数据
        requestWrapper.setBody(jsonObject.toJSONString().getBytes());

        chain.doFilter(requestWrapper, response);
    }
 
    @Override
    public void destroy() {

    }
}