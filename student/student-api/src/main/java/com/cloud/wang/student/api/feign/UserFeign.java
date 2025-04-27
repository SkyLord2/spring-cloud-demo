package com.cloud.wang.student.api.feign;

import com.cloud.wang.common.core.constant.FeignConstant;
import com.cloud.wang.common.core.r.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(value = "user-service")
public interface UserFeign {

    R save (Map map , @RequestHeader(value = FeignConstant.SOURCE_KEY) String source);
}
