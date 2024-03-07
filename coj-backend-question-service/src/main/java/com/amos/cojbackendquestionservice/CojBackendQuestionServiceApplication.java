package com.amos.cojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.amos.cojbackendquestionservice.mapper")
@ComponentScan("com.amos")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.amos.cojbackendserviceclient.service"})
public class CojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CojBackendQuestionServiceApplication.class, args);
    }

}
