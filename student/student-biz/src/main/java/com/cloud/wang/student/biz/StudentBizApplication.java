package com.cloud.wang.student.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.cloud.wang"})
@MapperScan("com.cloud.wang.student.biz.*.mapper")
@EnableDiscoveryClient
public class StudentBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentBizApplication.class, args);
    }

}
