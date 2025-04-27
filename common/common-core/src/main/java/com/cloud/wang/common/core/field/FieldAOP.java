/*
package com.cloud.wang.common.core.field;

import com.zhongyan.dz.incubator.annotation.NeedLogin;
import com.zhongyan.dz.incubator.annotation.FieldFilter;
import com.zhongyan.dz.incubator.core.Constants;
import com.zhongyan.dz.incubator.core.R;
import com.zhongyan.dz.incubator.utils.ServletUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

*/
/**
 *  对需要登录才能查看的字段进行过滤
 *
 * @author wang
 * @since 2022-08-02
 * *//*

@Aspect
@Component
public class FieldAOP {

    @Pointcut("@annotation(com.zhongyan.dz.incubator.annotation.FieldFilter)")
    public void point () {}

    */
/**
     *  环绕增强，过滤出参
     * *//*

    @Around(value = "point() && @annotation(fieldFilter)")
    public Object around (ProceedingJoinPoint joinPoint , FieldFilter fieldFilter) throws Throwable {
        //返回的结果
        Object returnValue = joinPoint.proceed();
        if (ServletUtil.getRequest().getHeader(Constants.AUTHORIZE_TOKEN) != null) {
            //已登录，直接放行
            return returnValue;
        }

        R<Map<String , Object>> r = (R<Map<String, Object>>) returnValue;
        Map<String , Object> dataMap = r.getData();
        //遍历map中的每一个对象
        for (Map.Entry<String , Object> entry : dataMap.entrySet()) {
            Object data = entry.getValue();

            if (!(data instanceof Collection)) {
                //单个对象
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(NeedLogin.class)) {
                        field.set(data , "无权查看");
                    }
                    field.setAccessible(false);
                }
            }else {
                //集合对象
                ((Collection<?>) data).forEach(e -> {
                    //遍历集合中的每一个对象
                    Field[] fields = e.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.isAnnotationPresent(NeedLogin.class)) {
                            try {
                                field.set(e , "无权查看");
                            } catch (IllegalAccessException ex) {
                                ex.printStackTrace();
                            }
                        }
                        field.setAccessible(false);
                    }
                });
            }

        }

        return R.ok(dataMap);
    }

}
*/
