package com.cloud.wang.student.biz.student.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cloud.wang.common.core.constant.FeignConstant;
import com.cloud.wang.common.core.r.R;
import com.cloud.wang.common.log.annotation.Log;
import com.cloud.wang.common.redis.JedisUtil;
import com.cloud.wang.common.rsa.annotation.Encrypt;
import com.cloud.wang.student.api.entity.TbStudent;
import com.cloud.wang.student.api.feign.UserFeign;
import com.cloud.wang.student.biz.student.service.TbStudentService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wang
 * @since 2022-05-18
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class TbStudentController {

    @Autowired
    private TbStudentService studentService;

    @GetMapping(value = "/list")
    @Log(desc = "查询学生列表")
    public R test() {
        List<TbStudent> studentList = studentService.list();
        List<Long> idList = studentList.stream().map(e -> e.getId()).collect(Collectors.toList());

        /*Map<Long , List<TbStudent>> classMap = studentList.stream().collect(
                Collectors.groupingBy(TbStudent::getClassId)
        );*/
        return R.ok(studentList);
    }

    /**
     *  传输加密的一个例子。@Encrypt注解说明接口的出参需要加密，
     *  使用此注解，则在入参方向也必须是加密的，因为AOP里处理加
     *  密的时候是根据当前Request中的AES密钥加密的。
     * */
    @PostMapping(value = "/post")
    @Encrypt
    @Log(desc = "新增学生")
    public R postDemo(@RequestBody TbStudent student) throws Exception {
        System.out.println("111");
        return R.ok();
    }

    @GetMapping(value = "/batch")
    public R testUpdate() {
        List<TbStudent> studentList = studentService.list();
        studentList.get(0).setStuName("修改1");
        studentList.get(1).setStuName("修改2");
        studentList.get(2).setStuName("修改3");
        studentService.updateBatchByQueryWrapper(
                studentList ,
                //这里要求条件字段的值唯一，否则会出错
                student -> new QueryWrapper<>().eq("class_id" , student.getClassId())
        );
        return R.ok();
    }

    @GetMapping(value = "/test")
    public R testRedis() {
        TbStudent student = studentService.getOne(new QueryWrapper<TbStudent>().lambda().eq(TbStudent::getId , 1).eq(TbStudent::getDelFlag , 1));
        return R.ok(student);
    }

}

